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

package org.hisp.dhis.android.core.imports;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ImportStatusColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;

import java.util.Date;

@AutoValue
public abstract class TrackerImportConflict extends BaseObject {

    @Nullable
    public abstract String conflict();

    @Nullable
    public abstract String value();

    @Nullable
    public abstract String trackedEntityInstance();

    @Nullable
    public abstract String enrollment();

    @Nullable
    public abstract String event();

    @Nullable
    public abstract String tableReference();

    @Nullable
    public abstract String errorCode();

    @Nullable
    @ColumnAdapter(ImportStatusColumnAdapter.class)
    public abstract ImportStatus status();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @NonNull
    public static TrackerImportConflict create(Cursor cursor) {
        return AutoValue_TrackerImportConflict.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new AutoValue_TrackerImportConflict.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder extends BaseObject.Builder<Builder> {
        public abstract Builder conflict(String conflict);

        public abstract Builder value(String value);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        public abstract Builder enrollment(String enrollment);

        public abstract Builder event(String event);

        public abstract Builder tableReference(String tableReference);

        public abstract Builder errorCode(String errorCode);

        public abstract Builder status(ImportStatus status);

        public abstract Builder created(Date created);

        public abstract TrackerImportConflict build();
    }
}