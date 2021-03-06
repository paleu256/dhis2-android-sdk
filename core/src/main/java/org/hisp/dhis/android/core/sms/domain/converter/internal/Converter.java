package org.hisp.dhis.android.core.sms.domain.converter.internal;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SMSVersion;
import org.hisp.dhis.smscompression.SMSSubmissionWriter;
import org.hisp.dhis.smscompression.models.SMSMetadata;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public abstract class Converter<P> {
    private final LocalDbRepository localDbRepository;
    private final DHISVersionManager dhisVersionManager;

    Converter(LocalDbRepository localDbRepository,
              DHISVersionManager dhisVersionManager) {
        this.localDbRepository = localDbRepository;
        this.dhisVersionManager = dhisVersionManager;
    }

    public Single<String> readAndConvert() {
        return readAndConvert(0);
    }

    public Single<String> readAndConvert(int submissionId) {
        return Single.zip(
                localDbRepository.getMetadataIds(),
                localDbRepository.getUserName(),
                readItemFromDb(),
                CompressionData::new
        ).flatMap(
                d -> convert(d.item, d.metadata, d.user, submissionId)
        );
    }

    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    private Single<String> convert(@NonNull P dataItem, SMSMetadata metadata, String user, Integer submissionId) {
        return convert(dataItem, user, submissionId).map(submission -> {
            SMSSubmissionWriter writer = new SMSSubmissionWriter(metadata);
            SMSVersion smsVersion = dhisVersionManager.getSmsVersion();
            if (smsVersion == null) {
                throw D2Error.builder()
                        .errorCode(D2ErrorCode.SMS_NOT_SUPPORTED)
                        .errorDescription("SMS is not supported in version " + dhisVersionManager.getPatchVersion())
                        .errorComponent(D2ErrorComponent.SDK)
                        .build();
            }
            return base64(writer.compress(submission, smsVersion.getIntValue()));
        });
    }

    @SuppressLint("NewApi")
    private String base64(byte[] bytes) {
        String encoded;
        try {
            encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Throwable t) {
            encoded = null;
            // not android, so will try with pure java
        }
        if (encoded == null) {
            encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return encoded;
    }

    LocalDbRepository getLocalDbRepository() {
        return localDbRepository;
    }

    abstract Single<? extends SMSSubmission> convert(@NonNull P dataItem, String user, int submissionId);

    public abstract Completable updateSubmissionState(State state);

    abstract Single<P> readItemFromDb();

    private class CompressionData {
        final String user;
        final SMSMetadata metadata;
        final P item;

        CompressionData(SMSMetadata metadata, String user, P item) {
            this.user = user;
            this.metadata = metadata;
            this.item = item;
        }
    }
}
