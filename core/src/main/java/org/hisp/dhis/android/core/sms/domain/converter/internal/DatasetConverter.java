package org.hisp.dhis.android.core.sms.domain.converter.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.smscompression.models.AggregateDatasetSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSDataValue;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

public class DatasetConverter extends Converter<List<DataValue>> {

    private final String dataSet;
    private final String orgUnit;
    private final String period;
    private final String attributeOptionComboUid;

    public DatasetConverter(LocalDbRepository localDbRepository,
                            String dataSet,
                            String orgUnit,
                            String period,
                            String attributeOptionComboUid) {
        super(localDbRepository);
        this.dataSet = dataSet;
        this.orgUnit = orgUnit;
        this.period = period;
        this.attributeOptionComboUid = attributeOptionComboUid;
    }

    @Override
    Single<? extends SMSSubmission> convert(@NonNull List<DataValue> values, String user, int submissionId) {
        return Single.fromCallable(() -> {
            AggregateDatasetSMSSubmission subm = new AggregateDatasetSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setOrgUnit(orgUnit);
            subm.setPeriod(period);
            subm.setDataSet(dataSet);
            subm.setAttributeOptionCombo(attributeOptionComboUid);
            subm.setValues(translateValues(values));
            return subm;
        });
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<SMSDataValue> translateValues(List<DataValue> values) {
        ArrayList<SMSDataValue> list = new ArrayList<>();
        for (DataValue value : values) {
            list.add(new SMSDataValue(
                    value.categoryOptionCombo(),
                    value.dataElement(),
                    value.value()));
        }
        return list;
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateDataSetSubmissionState(
                dataSet, orgUnit, period, attributeOptionComboUid, state);
    }

    @Override
    Single<List<DataValue>> readItemFromDb() {
        return getLocalDbRepository().getDataValues(orgUnit, period, attributeOptionComboUid);
    }
}
