package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionComboStoreImpl extends BaseStore<CategoryOptionCombo> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboModel.TABLE + " (" +
                    CategoryOptionComboModel.Columns.UID + ", " +
                    CategoryOptionComboModel.Columns.CODE + ", " +
                    CategoryOptionComboModel.Columns.NAME + ", " +
                    CategoryOptionComboModel.Columns.DISPLAY_NAME + ", " +
                    CategoryOptionComboModel.Columns.CREATED + ", " +
                    CategoryOptionComboModel.Columns.LAST_UPDATED + ", " +
                    CategoryOptionComboModel.Columns.CATEGORY_COMBO + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    public static final String EQUAL_QUESTION_MARK = " =?";
    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryOptionComboModel.TABLE +
            " WHERE " + CategoryOptionComboModel.Columns.UID +  EQUAL_QUESTION_MARK + ";";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + CategoryOptionComboModel.TABLE + " SET " +
                    CategoryOptionComboModel.Columns.UID + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.CODE + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.NAME + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.DISPLAY_NAME + EQUAL_QUESTION_MARK + ", "
                    +
                    CategoryOptionComboModel.Columns.CREATED +  EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.CATEGORY_COMBO + EQUAL_QUESTION_MARK
                    + " WHERE " +
                    CategoryOptionComboModel.Columns.UID +  EQUAL_QUESTION_MARK + ";";

    public CategoryOptionComboStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                databaseAdapter.compileStatement(UPDATE_STATEMENT),
                databaseAdapter.compileStatement(DELETE_STATEMENT),
                CategoryOptionComboModel.TABLE);
    }


    @Override
    public void validate(@NonNull CategoryOptionCombo optionCombo) {
        isNull(optionCombo.uid());
    }

    @Override
    public void bindForDelete(@NonNull CategoryOptionCombo optionCombo) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, optionCombo.uid());
    }

    @Override
    public void bindUpdate(@NonNull CategoryOptionCombo oldOptionCombo,
            @NonNull CategoryOptionCombo newOptionCombo) {
        final int whereUidIndex = 7;
        bind(updateStatement, newOptionCombo);

        sqLiteBind(updateStatement, whereUidIndex, oldOptionCombo.uid());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryOptionCombo newOptionCombo) {
        sqLiteBind(sqLiteStatement, 1, newOptionCombo.uid());
        sqLiteBind(sqLiteStatement, 2, newOptionCombo.code());
        sqLiteBind(sqLiteStatement, 3, newOptionCombo.name());
        sqLiteBind(sqLiteStatement, 4, newOptionCombo.displayName());
        sqLiteBind(sqLiteStatement, 5, newOptionCombo.created());
        sqLiteBind(sqLiteStatement, 6, newOptionCombo.lastUpdated());

        //noinspection ConstantConditions
        if (newOptionCombo.categoryCombo() != null
                && newOptionCombo.categoryCombo().uid() != null) {
            //noinspection ConstantConditions
            sqLiteBind(sqLiteStatement, 7, newOptionCombo.categoryCombo().uid());
        }

    }


}

