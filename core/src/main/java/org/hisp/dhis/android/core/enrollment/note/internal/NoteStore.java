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

package org.hisp.dhis.android.core.enrollment.note.internal;

import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class NoteStore {

    private static final StatementBinder<Note> BINDER = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.enrollment());
        sqLiteBind(sqLiteStatement, 2, o.value());
        sqLiteBind(sqLiteStatement, 3, o.storedBy());
        sqLiteBind(sqLiteStatement, 4, o.storedDate());
        sqLiteBind(sqLiteStatement, 5, o.uid());
        sqLiteBind(sqLiteStatement, 6, o.state());
    };

    private static final WhereStatementBinder<Note> WHERE_UPDATE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 7, o.enrollment());
        sqLiteBind(sqLiteStatement, 8, o.value());
        sqLiteBind(sqLiteStatement, 9, o.storedBy());
        sqLiteBind(sqLiteStatement, 10, o.storedDate());
    };

    private static final WhereStatementBinder<Note> WHERE_DELETE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.enrollment());
        sqLiteBind(sqLiteStatement, 2, o.value());
        sqLiteBind(sqLiteStatement, 3, o.storedBy());
        sqLiteBind(sqLiteStatement, 4, o.storedDate());
    };

    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            NoteTableInfo.TABLE_INFO, NoteTableInfo.Columns.ENROLLMENT);

    private NoteStore() {}

    public static ObjectWithoutUidStore<Note> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithoutUidStore(databaseAdapter, NoteTableInfo.TABLE_INFO,
                BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, Note::create);
    }
}