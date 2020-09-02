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
package org.hisp.dhis.android.core.imports.internal.conflicts

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.junit.Before
import org.junit.Test

class MissingAttributeConflictShould {

    private val attributeUid = "DI85uC13Bzo"

    private val context: TrackerImportConflictItemContext = mock()

    private val attributeStore: IdentifiableObjectStore<TrackedEntityAttribute> = mock()

    private val attribute: TrackedEntityAttribute = mock()

    private val importConflict = ImportConflict.create(
            "Attribute.attribute",
            "Missing mandatory attribute $attributeUid")

    @Before
    fun setUp() {
        whenever(context.attributeStore) doReturn attributeStore
        whenever(attributeStore.selectByUid(attributeUid)) doReturn attribute
    }

    @Test
    fun `Should match error message`() {
        assert(MissingAttributeConflict.matches(importConflict))
    }

    @Test
    fun `Should match attribute uid`() {
        val value = MissingAttributeConflict.getTrackedEntityAttribute(importConflict)
        assert(value == attributeUid)
    }

    @Test
    fun `Should create display description`() {
        whenever(attribute.displayFormName()) doReturn "Attribute form name"

        val displayDescription = MissingAttributeConflict.getDisplayDescription(importConflict, context)
        assert(displayDescription == "Missing mandatory attribute Attribute form name")
    }
}