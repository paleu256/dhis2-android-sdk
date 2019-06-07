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

package org.hisp.dhis.android.testapp.enrollment;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(D2JunitRunner.class)
public class EnrollmentObjectRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void update_organisation_unit() throws D2Error {
        String orgUnitUid = "new_org_unit";
        OrganisationUnitStore.create(databaseAdapter).insert(OrganisationUnit.builder().uid(orgUnitUid).build());

        EnrollmentObjectRepository repository = objectRepository();

        repository.setOrganisationUnitUid(orgUnitUid);
        assertThat(repository.get().organisationUnit(), is(orgUnitUid));

        repository.delete();
        OrganisationUnitStore.create(databaseAdapter).delete(orgUnitUid);
    }

    @Test(expected = D2Error.class)
    public void not_update_organisation_unit_if_not_exists() throws D2Error {
        String orgUnitUid = "new_org_unit";

        EnrollmentObjectRepository repository = objectRepository();

        try {
            repository.setOrganisationUnitUid(orgUnitUid);
        } finally {
            repository.delete();
        }
    }

    @Test
    public void update_enrollment_date() throws D2Error {
        Date enrollmentDate = new Date();

        EnrollmentObjectRepository repository = objectRepository();

        repository.setEnrollmentDate(enrollmentDate);
        assertThat(repository.get().enrollmentDate(), is(enrollmentDate));

        repository.delete();
    }

    @Test
    public void update_incident_date() throws D2Error {
        Date incidentDate = new Date();

        EnrollmentObjectRepository repository = objectRepository();

        repository.setIncidentDate(incidentDate);
        assertThat(repository.get().incidentDate(), is(incidentDate));

        repository.delete();
    }

    @Test
    public void update_follow_up() throws D2Error {
        EnrollmentObjectRepository repository = objectRepository();

        repository.setFollowUp(true);
        assertThat(repository.get().followUp(), is(true));

        repository.delete();
    }

    @Test
    public void update_enrollment_status() throws D2Error {
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.COMPLETED;

        EnrollmentObjectRepository repository = objectRepository();

        repository.setStatus(enrollmentStatus);
        assertThat(repository.get().status(), is(enrollmentStatus));

        repository.delete();
    }

    @Test
    public void update_coordinate() throws D2Error {
        Coordinates coordinate = Coordinates.create(10.00, 11.00);

        EnrollmentObjectRepository repository = objectRepository();

        repository.setCoordinate(coordinate);
        assertThat(repository.get().coordinate(), is(coordinate));

        repository.delete();
    }

    private EnrollmentObjectRepository objectRepository() throws D2Error {
        String enrollmentUid = d2.enrollmentModule().enrollments.add(
                EnrollmentCreateProjection.create(
                        "DiszpKrYNg8", "lxAQ7Zs9VYR", "nWrB0TfWlvh"));
        return d2.enrollmentModule().enrollments.uid(enrollmentUid);
    }
}