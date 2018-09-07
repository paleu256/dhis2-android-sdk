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

package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

public abstract class AbsStoreTestCase {
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseAdapter databaseAdapter;
    private String dbName = null;

    @Before
    public void setUp() throws IOException {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext().getApplicationContext()
                , dbName);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase("password");
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext().getApplicationContext());
    }

    @After
    public void tearDown() throws IOException {
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
    }

    protected SQLiteDatabase database() {
        return sqLiteDatabase;
    }

    protected DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    protected GenericCallData getGenericCallData(D2 d2) {
        return GenericCallData.create(
                databaseAdapter(), d2.retrofit(), new Date(), d2.systemInfoModule().versionManager);
    }

    protected Cursor getCursor(String table, String[] columns) {
        return sqLiteDatabase.query(table, columns,
                null, null, null, null, null);
    }
}
