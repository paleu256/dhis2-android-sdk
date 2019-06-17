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

package org.hisp.dhis.android.core.common.objectstyle.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ObjectStyleHandlerShould {

    private static final String ELEMENT_UID = "element_uid";
    private static final String OBJECT_TABLE = "ObjectTable";

    @Mock
    private ObjectWithoutUidStore<ObjectStyle> store;

    @Mock
    private ObjectStyle objectStyle;

    // object to test
    private ObjectStyleHandler objectStyleHandler;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        objectStyleHandler = new ObjectStyleHandlerImpl(store);

        when(objectStyle.color()).thenReturn("#ffffff");
        when(objectStyle.icon()).thenReturn("icon");
    }

    @Test
    public void try_to_delete_style_passing_null_arguments() {
        objectStyleHandler.handle(null, ELEMENT_UID, OBJECT_TABLE);

        verify(store, times(1)).deleteWhereIfExists("uid = '" + ELEMENT_UID + "'");
        verify(store, never()).updateOrInsertWhere(any(ObjectStyle.class));
    }

    @Test
    public void handle_style() {
        objectStyleHandler.handle(objectStyle, ELEMENT_UID, OBJECT_TABLE);

        verify(store, never()).deleteWhereIfExists(anyString());
        verify(store, times(1)).updateOrInsertWhere(any(ObjectStyle.class));
    }
}