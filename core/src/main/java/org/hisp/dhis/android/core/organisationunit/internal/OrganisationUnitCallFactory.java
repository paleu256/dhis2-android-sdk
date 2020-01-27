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
package org.hisp.dhis.android.core.organisationunit.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserInternalAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

@Reusable
class OrganisationUnitCallFactory {

    private final OrganisationUnitService organisationUnitService;
    private final OrganisationUnitHandler handler;
    private final OrganisationUnitDisplayPathTransformer pathTransformer;

    private final APICallExecutor apiCallExecutor;
    private final ResourceHandler resourceHandler;
    private final ProgramStoreInterface programStore;
    private final IdentifiableObjectStore<DataSet> dataSetStore;

    @Inject
    OrganisationUnitCallFactory(@NonNull OrganisationUnitService organisationUnitService,
                                @NonNull OrganisationUnitHandler handler,
                                @NonNull OrganisationUnitDisplayPathTransformer pathTransformer,
                                @NonNull APICallExecutor apiCallExecutor,
                                @NonNull ResourceHandler resourceHandler,
                                @NonNull ProgramStoreInterface programStore,
                                @NonNull IdentifiableObjectStore<DataSet> dataSetStore) {

        this.organisationUnitService = organisationUnitService;
        this.handler = handler;
        this.pathTransformer = pathTransformer;
        this.apiCallExecutor = apiCallExecutor;
        this.resourceHandler = resourceHandler;
        this.programStore = programStore;
        this.dataSetStore = dataSetStore;
    }

    public Callable<Unit> create(final User user,
                                 final Set<String> programUids,
                                 final Set<String> dataSetUids) {

        return () -> {
            handler.resetLinks();

            downloadCaptureOrgunits(user, programUids, dataSetUids);
            downloadSearchOrgunits(user, programUids, dataSetUids);

            removeNotLinkedProgramsAndDataSets(programUids, dataSetUids);

            resourceHandler.handleResource(Resource.Type.ORGANISATION_UNIT);

            return new Unit();
        };
    }

    private void downloadCaptureOrgunits(final User user,
                                         final Set<String> programUids,
                                         final Set<String> dataSetUids) throws D2Error {
        Set<String> captureOrgunitsUids = findRoots(UserInternalAccessor.accessOrganisationUnits(user));
        downloadOrgunits(captureOrgunitsUids, user,
                OrganisationUnit.Scope.SCOPE_DATA_CAPTURE, programUids, dataSetUids);
    }

    private void downloadSearchOrgunits(final User user,
                                        final Set<String> programUids,
                                        final Set<String> dataSetUids) throws D2Error {
        Set<String> searchOrgunitsUids = findRoots(UserInternalAccessor.accessTeiSearchOrganisationUnits(user));
        downloadOrgunits(searchOrgunitsUids, user,
                OrganisationUnit.Scope.SCOPE_TEI_SEARCH, programUids, dataSetUids);
    }

    private void downloadOrgunits(final Set<String> orgUnits,
                                  final User user,
                                  final OrganisationUnit.Scope scope,
                                  final Set<String> programUids,
                                  final Set<String> dataSetUids) throws D2Error {

        handler.setData(programUids, dataSetUids, user, scope);

        for (String uid : orgUnits) {
            OrganisationUnitQuery.Builder queryBuilder = OrganisationUnitQuery.builder().orgUnit(uid);

            List<OrganisationUnit> pageOrgunits;
            OrganisationUnitQuery pageQuery;
            do {
                pageQuery = queryBuilder.build();
                pageOrgunits = apiCallExecutor.executePayloadCall(getOrganisationUnitAndDescendants(pageQuery));

                handler.handleMany(pageOrgunits, pathTransformer);

                queryBuilder.page(pageQuery.page() + 1);
            }
            while (pageOrgunits.size() == pageQuery.pageSize());
        }
    }

    private void removeNotLinkedProgramsAndDataSets(final Set<String> programUids,
                                                    final Set<String> dataSetUids) {

        List<String> programsLinked = new ArrayList<>();
        List<String> dataSetsLinked = new ArrayList<>();

        for (String programUid : programUids) {
            if (!programsLinked.contains(programUid)) {
                programStore.deleteIfExists(programUid);
            }
        }

        for (String dataSetUid : dataSetUids) {
            if (!dataSetsLinked.contains(dataSetUid)) {
                dataSetStore.deleteIfExists(dataSetUid);
            }
        }
    }

    private retrofit2.Call<Payload<OrganisationUnit>> getOrganisationUnitAndDescendants(OrganisationUnitQuery query) {
        return organisationUnitService.getOrganisationUnits(
                OrganisationUnitFields.allFields, OrganisationUnitFields.path.like(query.orgUnit()),
                query.paging(), query.pageSize(), query.page());
    }
}