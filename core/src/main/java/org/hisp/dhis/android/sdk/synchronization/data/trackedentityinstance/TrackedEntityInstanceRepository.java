package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance.ITrackedEntityInstanceRepository;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackedEntityInstanceRepository  implements ITrackedEntityInstanceRepository {
    TrackedEntityInstanceLocalDataSource mLocalDataSource;
    TrackedEntityInstanceRemoteDataSource mRemoteDataSource;

    public TrackedEntityInstanceRepository(
            TrackedEntityInstanceLocalDataSource localDataSource,
            TrackedEntityInstanceRemoteDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }
    @Override
    public void save(TrackedEntityInstance trackedEntityInstance) {
        mLocalDataSource.save(trackedEntityInstance);
    }

    @Override
    public ImportSummary sync(TrackedEntityInstance trackedEntityInstance) {
        ImportSummary importSummary = mRemoteDataSource.save(trackedEntityInstance);

        if (importSummary.isSuccessOrOK()) {
            updateTrackedEntityInstanceTimestamp(trackedEntityInstance);
        }

        return importSummary;
    }

    @Override
    public List<ImportSummary2> sync(List<TrackedEntityInstance> trackedEntityInstanceList) {

        List<ImportSummary2> importSummaries = mRemoteDataSource.save(trackedEntityInstanceList);

        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = toMap(trackedEntityInstanceList);
        if (importSummaries != null) {
            DateTime dateTime = mRemoteDataSource.getServerTime();
            for (ImportSummary2 importSummary2 : importSummaries) {
                if (importSummary2.isSuccessOrOK()) {
                    System.out.println("IMPORT SUMMARY: " + importSummary2.getDescription() + importSummary2.getHref());
                    TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceMap.get(importSummary2.getReference());
                    if (trackedEntityInstance != null) {
                        updateTrackedEntityInstanceTimestamp(trackedEntityInstance, dateTime.toString(), dateTime.toString());
                    }
                }
            }
        }
        return importSummaries;
    }

    @Override
    public Map<String,TrackedEntityInstance> toMap(List<TrackedEntityInstance> trackedEntityInstanceList){
        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = new HashMap<>();
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstanceList) {
            trackedEntityInstanceMap.put(trackedEntityInstance.getUid(), trackedEntityInstance);
        }

        return trackedEntityInstanceMap;
    }

    private void updateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance) {
        TrackedEntityInstance remoteTrackedEntityInstance = mRemoteDataSource.getTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstance());
        if(trackedEntityInstance.getRelationships()!=null && trackedEntityInstance.getRelationships().size()==0){
            //Restore relations before save.
            trackedEntityInstance.setRelationships(null);
            trackedEntityInstance.getRelationships();
        }
        updateTrackedEntityInstanceTimestamp(remoteTrackedEntityInstance, remoteTrackedEntityInstance.getCreated(), remoteTrackedEntityInstance.getLastUpdated());
    }

    private void updateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance, String createdDate, String lastUpdated) {
        trackedEntityInstance.setCreated(createdDate);
        trackedEntityInstance.setLastUpdated(lastUpdated);

        mLocalDataSource.save(trackedEntityInstance);
    }


    @Override
    public Map<String, TrackedEntityInstance> getRecursiveRelationatedTeis(
            TrackedEntityInstance trackedEntityInstance,
            Map<String, TrackedEntityInstance> relatedTeiList) {
        if (trackedEntityInstance.getRelationships() != null
                && trackedEntityInstance.getRelationships().size() > 0) {
            for (Relationship relationship : trackedEntityInstance.getRelationships()) {
                if (relationship.getTrackedEntityInstanceB().equals(
                        trackedEntityInstance.getUid())) {
                    String target = relationship.getTrackedEntityInstanceA();
                    relatedTeiList = addRelatedNotPushedTeis(relatedTeiList, target);
                } else if (relationship.getTrackedEntityInstanceA().equals(
                        trackedEntityInstance.getUid())) {
                    String target = relationship.getTrackedEntityInstanceB();
                    relatedTeiList = addRelatedNotPushedTeis(relatedTeiList, target);
                }
            }
        }
        return relatedTeiList;
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstanceUid) {
        return mLocalDataSource.getTrackedEntityInstance(trackedEntityInstanceUid);
    }

    @Override
    public List<TrackedEntityInstance> getAllLocalTeis() {
        return mLocalDataSource.getAllLocalTeis();
    }

    private Map<String, TrackedEntityInstance> addRelatedNotPushedTeis(
            Map<String, TrackedEntityInstance> relatedTeiList, String target) {
        TrackedEntityInstance relatedTrackedEntityInstance =
                TrackerController.getTrackedEntityInstance(target);
        if (!relatedTrackedEntityInstance.isFromServer()
                && relatedTrackedEntityInstance.getCreated() == null) {
            if (!relatedTeiList.containsKey(relatedTrackedEntityInstance.getUid())) {
                relatedTeiList.put(relatedTrackedEntityInstance.getUid(),
                        relatedTrackedEntityInstance);
                relatedTeiList = getRecursiveRelationatedTeis(relatedTrackedEntityInstance,
                        relatedTeiList);
            }
        }
        return relatedTeiList;
    }
}