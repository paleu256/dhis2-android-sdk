/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.localanalytics

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2DIComponentAccessor
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl

internal data class MetadataForDataFilling(val organisationUnits: List<OrganisationUnit>,
                                           val periods: List<Period>,
                                           val categoryOptionCombos: List<CategoryOptionCombo>,
                                           val aggregatedDataElements: List<DataElement>,
                                           val programs: List<Program>)

internal class LocalAnalyticsDatabaseFiller(private val d2: D2) {
    private val da = d2.databaseAdapter()
    private val d2DIComponent = D2DIComponentAccessor.getD2DIComponent(d2)

    fun fillDatabase(metadataParams: LocalAnalyticsMetadataParams, dataParams: LocalAnalyticsDataParams) {
        val metadata = fillMetadata(metadataParams)
        fillData(dataParams, metadata)
    }

    private fun fillMetadata(metadataParams: LocalAnalyticsMetadataParams): MetadataForDataFilling {
        val generator = LocalAnalyticsMetadataGenerator(metadataParams)

        val organisationUnits = generator.getOrganisationUnits()
        OrganisationUnitStore.create(da).insert(organisationUnits)

        val categoryCombos = generator.getCategoryCombos()
        CategoryComboStore.create(da).insert(categoryCombos)

        val categoryOptionCombos = generator.getCategoryOptionCombos(categoryCombos)
        CategoryOptionComboStoreImpl.create(da).insert(categoryOptionCombos)

        val defaultCategoryCombo = categoryCombos.first()
        val aggregatedDataElements = generator.getDataElementsAggregated(categoryCombos)
        DataElementStore.create(da).insert(aggregatedDataElements +
                generator.getDataElementsTracker(defaultCategoryCombo))

        d2DIComponent.periodHandler().generateAndPersist()

        val programs = generator.getPrograms(defaultCategoryCombo)
        ProgramStore.create(da).insert(programs)

        ProgramStageStore.create(da).insert(generator.getProgramStages(programs))

        TrackedEntityAttributeStore.create(da).insert(generator.getTrackedEntityAttributes())

        val periodTypes = listOf(PeriodType.Monthly, PeriodType.Weekly, PeriodType.Daily)
        val periods = d2.periodModule().periods().byPeriodType().`in`(periodTypes).blockingGet()

        return MetadataForDataFilling(organisationUnits, periods, categoryOptionCombos, aggregatedDataElements,
                programs)
    }

    private fun fillData(dataParams: LocalAnalyticsDataParams, metadata: MetadataForDataFilling) {
        val generator = LocalAnalyticsDataGenerator(dataParams)

        val dv = generator.generateDataValues(metadata)
        DataValueStore.create(da).insert(dv)

        val teis = generator.generateTrackedEntityInstances(metadata.organisationUnits)
        TrackedEntityInstanceStoreImpl.create(da).insert(teis)

        val enrollments = generator.generateEnrollments(teis, metadata.programs.first())
        EnrollmentStoreImpl.create(da).insert(enrollments)
    }
}