package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class CategoryComboCallEndpoint implements Call<Response<Payload<CategoryCombo>>> {

    private final CategoryComboQuery query;
    private final CategoryComboService comboService;
    private final ResponseValidator<CategoryCombo> responseValidator;
    private final Handler<CategoryCombo> handler;
    private final ResourceHandler resourceHandler;
    private final DatabaseAdapter databaseAdapter;
    private final Date serverDate;
    private boolean isExecuted;

    public CategoryComboCallEndpoint(CategoryComboQuery query,
            CategoryComboService comboService,
            ResponseValidator<CategoryCombo> responseValidator,
            Handler<CategoryCombo> handler,
            ResourceHandler resourceHandler,
            DatabaseAdapter databaseAdapter, Date serverDate) {
        this.query = query;
        this.comboService = comboService;
        this.responseValidator = responseValidator;
        this.handler = handler;
        this.resourceHandler = resourceHandler;
        this.databaseAdapter = databaseAdapter;
        this.serverDate = new Date(serverDate.getTime());
    }


    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<CategoryCombo>> call() throws Exception {

        validateIsNotTryingToExcuteAgain();

        Response<Payload<CategoryCombo>> response = comboService.getCategoryCombos(getFields(),
                query.paging(),
                query.page(), query.pageSize())
                .execute();

        if (responseValidator.isValid(response)) {
            List<CategoryCombo> combos = response.body().items();

            handle(combos);
        }

        return response;
    }

    private void handle(List<CategoryCombo> combos) {
        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            for (CategoryCombo combo : combos) {
                handler.handle(combo);
            }
            resourceHandler.handleResource(ResourceModel.Type.CATEGORY_COMBO, serverDate);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    private void validateIsNotTryingToExcuteAgain() {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }
    }

    @NonNull
    private Fields<CategoryCombo> getFields() {

        return Fields.<CategoryCombo>builder().fields(CategoryCombo.uid, CategoryCombo.code,
                CategoryCombo.name, CategoryCombo.displayName,
                CategoryCombo.created, CategoryCombo.lastUpdated, CategoryCombo.deleted,
                CategoryCombo.displayName, CategoryCombo.isDefault, CategoryCombo.categories,
                CategoryCombo.categoryOptionCombos.with(CategoryOptionCombo.uid,
                        CategoryOptionCombo.code,
                        CategoryOptionCombo.name,
                        CategoryOptionCombo.displayName,
                        CategoryOptionCombo.created,
                        CategoryOptionCombo.lastUpdated,
                        CategoryOptionCombo.deleted,
                        CategoryOptionCombo.categoryCombo.with(CategoryCombo.uid),
                        CategoryOptionCombo.displayName,
                        CategoryOptionCombo.categoryOptions.with(
                                CategoryOption.uid
                        )))
                .build();
    }
}
