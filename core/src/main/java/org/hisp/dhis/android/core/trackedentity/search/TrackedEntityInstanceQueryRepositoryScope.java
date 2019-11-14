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

package org.hisp.dhis.android.core.trackedentity.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
abstract class TrackedEntityInstanceQueryRepositoryScope implements BaseScope {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

    @NonNull
    public abstract RepositoryMode mode();

    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract RepositoryScopeFilterItem query();

    @NonNull
    public abstract List<RepositoryScopeFilterItem> attribute();

    @NonNull
    public abstract List<RepositoryScopeFilterItem> filter();

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    @Nullable
    public abstract String trackedEntityType();

    @Nullable
    public abstract Boolean includeDeleted();

    @Nullable
    public abstract List<State> states();

    public String formattedProgramStartDate() {
        return programStartDate() == null ? null : QUERY_FORMAT.format(programStartDate());
    }

    public String formattedProgramEndDate() {
        return programEndDate() == null ? null : QUERY_FORMAT.format(programEndDate());
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryRepositoryScope.Builder()
                .attribute(Collections.emptyList())
                .filter(Collections.emptyList())
                .orgUnits(Collections.emptyList())
                .mode(RepositoryMode.OFFLINE_ONLY)
                .includeDeleted(false);
    }

    public static TrackedEntityInstanceQueryRepositoryScope empty() {
        return builder().build();
    }

    @AutoValue.Builder
    abstract static class Builder {

        public abstract Builder mode(RepositoryMode mode);

        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder query(RepositoryScopeFilterItem query);

        public abstract Builder attribute(List<RepositoryScopeFilterItem> attribute);

        public abstract Builder filter(List<RepositoryScopeFilterItem> filter);

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder states(List<State> states);

        public abstract Builder includeDeleted(Boolean includeDeleted);

        abstract TrackedEntityInstanceQueryRepositoryScope autoBuild();

        // Auxiliary fields to access values
        abstract List<State> states();

        public TrackedEntityInstanceQueryRepositoryScope build() {
            if (states() != null) {
                mode(RepositoryMode.OFFLINE_ONLY);
            }

            return autoBuild();
        }
    }
}
