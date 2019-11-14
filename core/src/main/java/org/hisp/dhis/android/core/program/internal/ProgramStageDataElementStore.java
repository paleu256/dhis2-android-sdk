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

package org.hisp.dhis.android.core.program.internal;

import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;

final class ProgramStageDataElementStore {

    private ProgramStageDataElementStore() {}

    private static StatementBinder<ProgramStageDataElement> BINDER =
            new IdentifiableStatementBinder<ProgramStageDataElement>() {

        @Override
        public void bindToStatement(@NonNull ProgramStageDataElement programStageDataElement,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(programStageDataElement, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, programStageDataElement.displayInReports());
            sqLiteBind(sqLiteStatement, 8, UidsHelper.getUidOrNull(programStageDataElement.dataElement()));
            sqLiteBind(sqLiteStatement, 9, programStageDataElement.compulsory());
            sqLiteBind(sqLiteStatement, 10, programStageDataElement.allowProvidedElsewhere());
            sqLiteBind(sqLiteStatement, 11, programStageDataElement.sortOrder());
            sqLiteBind(sqLiteStatement, 12, programStageDataElement.allowFutureDate());
            sqLiteBind(sqLiteStatement, 13, UidsHelper.getUidOrNull(programStageDataElement.programStage()));

        }
    };

    static IdentifiableObjectStore<ProgramStageDataElement> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter,
                ProgramStageDataElementTableInfo.TABLE_INFO, BINDER, ProgramStageDataElement::create);
    }
}