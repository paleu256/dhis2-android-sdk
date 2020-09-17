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

package org.hisp.dhis.android.core.event.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;

@Reusable
public final class EventPersistenceCallFactory {

    private final IdentifiableDataHandler<Event> eventHandler;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;

    @Inject
    EventPersistenceCallFactory(
            @NonNull IdentifiableDataHandler<Event> eventHandler,
            @NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader) {
        this.eventHandler = eventHandler;
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
    }

    Completable persistEvents(final Collection<Event> events) {
        return Completable.defer(() -> {
            eventHandler.handleMany(events,
                    event -> event.toBuilder()
                            .state(State.SYNCED)
                            .build(),
                    false);

            Set<String> searchUnitUids = getMissingOrganisationUnitUids(events);
            return organisationUnitDownloader.downloadSearchOrganisationUnits(searchUnitUids);
        });
    }

    private Set<String> getMissingOrganisationUnitUids(Collection<Event> events) {
        Set<String> uids = new HashSet<>();
        for (Event event : events) {
            if (event.organisationUnit() != null) {
                uids.add(event.organisationUnit());
            }
        }
        uids.removeAll(organisationUnitStore.selectUids());
        return uids;
    }

    public Completable persistRelationships(final List<Event> events) {
        return persistEventsInternal(events, true, false, false);
    }

    private Completable persistEventsInternal(
            final List<Event> events, boolean asRelationship, boolean isFullUpdate, boolean overwrite) {
        return Completable.defer(() -> {
            eventHandler.handleMany(events, asRelationship, isFullUpdate, overwrite);
            return Completable.complete();
        });
    }
}