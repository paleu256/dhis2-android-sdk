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

package org.hisp.dhis.android.core.user.internal;

import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkModelStoreImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo.Columns;

import java.util.List;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;

public final class UserOrganisationUnitLinkStoreImpl extends LinkModelStoreImpl<UserOrganisationUnitLink>
        implements UserOrganisationUnitLinkStore {

    private static final StatementBinder<UserOrganisationUnitLink> BINDER = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.user());
        sqLiteBind(sqLiteStatement, 2, o.organisationUnit());
        sqLiteBind(sqLiteStatement, 3, o.organisationUnitScope());
        sqLiteBind(sqLiteStatement, 4, o.root());
    };

    private UserOrganisationUnitLinkStoreImpl(DatabaseAdapter databaseAdapter,
                                              SQLiteStatement insertStatement,
                                              String masterColumn,
                                              SQLStatementBuilderImpl builder,
                                              StatementBinder<UserOrganisationUnitLink> binder) {
        super(databaseAdapter, insertStatement, builder, masterColumn, binder, UserOrganisationUnitLink::create);
    }

    public static UserOrganisationUnitLinkStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder =
                new SQLStatementBuilderImpl(UserOrganisationUnitLinkTableInfo.TABLE_INFO);

        return new UserOrganisationUnitLinkStoreImpl(
                databaseAdapter,
                databaseAdapter.compileStatement(statementBuilder.insert()),
                Columns.ORGANISATION_UNIT_SCOPE,
                statementBuilder,
                BINDER);
    }

    @Override
    public List<String> queryRootCaptureOrganisationUnitUids() throws RuntimeException {
        return selectStringColumnsWhereClause(Columns.ORGANISATION_UNIT,
                        Columns.ROOT + " = 1 " + "AND "
                                + Columns.ORGANISATION_UNIT_SCOPE + " = '"
                                + OrganisationUnit.Scope.SCOPE_DATA_CAPTURE + "'");
    }
}