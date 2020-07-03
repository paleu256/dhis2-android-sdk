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

package org.hisp.dhis.android.core.data.program;

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.program.AccessLevel;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

public class ProgramSamples {

    public static Program getProgram() {
        Program.Builder builder = Program.builder();

        fillNameableProperties(builder);
        builder
                .id(1L)
                .version(1)
                .onlyEnrollOnce(true)
                .enrollmentDateLabel("enrollment_date_label")
                .displayIncidentDate(false)
                .incidentDateLabel("incident_date_label")
                .registration(true)
                .selectEnrollmentDatesInFuture(true)
                .dataEntryMethod(false)
                .ignoreOverdueEvents(false)
                .selectIncidentDatesInFuture(true)
                .useFirstStageDuringRegistration(true)
                .displayFrontPageList(false)
                .programType(ProgramType.WITH_REGISTRATION)
                .relatedProgram(ObjectWithUid.create("program_uid"))
                .trackedEntityType(TrackedEntityType.builder().uid("tracked_entity_type").build())
                .categoryCombo(ObjectWithUid.create("category_combo_uid"))
                .access(Access.create(null, null, DataAccess.create(true, true)))
                .expiryDays(2)
                .completeEventsExpiryDays(3)
                .minAttributesRequiredToSearch(1)
                .maxTeiCountToReturn(2)
                .featureType(FeatureType.POINT)
                .accessLevel(AccessLevel.PROTECTED)
                .build();
        return builder.build();
    }

    public static Program getChildProgram() {
        return Program.builder()
                .id(1L)
                .uid("IpHINAT79UW")
                .name("Child Programme")
                .displayName("Child Programme")
                .created(parseDate("2013-03-04T11:41:07.494"))
                .lastUpdated(parseDate("2017-01-26T19:39:33.356"))
                .shortName("Child Programme")
                .displayShortName("Child Programme")
                .version(5)
                .onlyEnrollOnce(true)
                .enrollmentDateLabel("Date of enrollment")
                .displayIncidentDate(true)
                .incidentDateLabel("Date of birth")
                .registration(true)
                .selectEnrollmentDatesInFuture(false)
                .dataEntryMethod(false)
                .ignoreOverdueEvents(false)
                .selectIncidentDatesInFuture(false)
                .useFirstStageDuringRegistration(true)
                .displayFrontPageList(false)
                .programType(ProgramType.WITH_REGISTRATION)
                .trackedEntityType(TrackedEntityType.builder().uid("nEenWmSyUEp").build())
                .categoryCombo(ObjectWithUid.create("nM3u9s5a52V"))
                .access(Access.create(true, true, DataAccess.create(true, false)))
                .accessLevel(AccessLevel.PROTECTED)
                .style(ObjectStyle.builder().color("#fff").icon("my-icon-name").build())
                .build();
    }

    public static Program getAntenatalProgram() {
        return Program.builder()
                .id(1L)
                .uid("lxAQ7Zs9VYR")
                .name("Antenatal care visit")
                .displayName("Antenatal care visit")
                .created(parseDate("2016-04-12T15:30:43.783"))
                .lastUpdated(parseDate("2017-01-19T14:32:05.307"))
                .shortName("Antenatal care")
                .displayShortName("Antenatal care")
                .version(3)
                .onlyEnrollOnce(false)
                .enrollmentDateLabel("Enrollment Date")
                .displayIncidentDate(false)
                .incidentDateLabel("Incident date")
                .registration(false)
                .selectEnrollmentDatesInFuture(false)
                .dataEntryMethod(true)
                .ignoreOverdueEvents(true)
                .selectIncidentDatesInFuture(true)
                .useFirstStageDuringRegistration(true)
                .displayFrontPageList(false)
                .programType(ProgramType.WITHOUT_REGISTRATION)
                .trackedEntityType(TrackedEntityType.builder().uid("nEenWmSyUEp").build())
                .categoryCombo(ObjectWithUid.create("m2jTvAj5kkm"))
                .access(Access.create(true, true, DataAccess.create(true, true)))
                .accessLevel(AccessLevel.PROTECTED)
                .style(ObjectStyle.builder().color("#333").icon("program-icon").build())
                .build();
    }

    public static Program getProgram(String name, ProgramType type, CategoryCombo categoryCombo) {
        return getProgram().toBuilder()
                .name(name)
                .uid(new UidGeneratorImpl().generate())
                .trackedEntityType(null)
                .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
                .relatedProgram(null)
                .programType(type)
                .build();
    }
}