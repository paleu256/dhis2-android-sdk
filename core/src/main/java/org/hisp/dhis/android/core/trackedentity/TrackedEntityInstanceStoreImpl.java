/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.nonNull;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

class TrackedEntityInstanceStoreImpl implements TrackedEntityInstanceStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityInstanceModel.TABLE + " (" +
            TrackedEntityInstanceModel.Columns.UID + ", " +
            TrackedEntityInstanceModel.Columns.CREATED + ", " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + ", " +
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY + ", " +
            TrackedEntityInstanceModel.Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?, ?)";

    private final SQLiteStatement insertStatement;
    private final DatabaseAdapter databaseAdapter;

    TrackedEntityInstanceStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
            @NonNull String organisationUnit, @NonNull String trackedEntity, @Nullable State state) {

        nonNull(uid);
        nonNull(organisationUnit);
        nonNull(trackedEntity);

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, lastUpdated);
        sqLiteBind(insertStatement, 4, organisationUnit);
        sqLiteBind(insertStatement, 5, trackedEntity);
        sqLiteBind(insertStatement, 6, state);

        long returnValue = databaseAdapter.executeInsert(TrackedEntityInstanceModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return returnValue;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityInstanceModel.TABLE);
    }
}
