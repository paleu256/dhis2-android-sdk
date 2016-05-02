package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FormEntityFilter extends FormEntity {

    @Nullable
    private Picker picker;

    @Nullable
    private OnFormEntityChangeListener onFormEntityChangeListener;

    public FormEntityFilter(String id, String label) {
        super(id, label);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.FILTER;
    }

    @Nullable
    public Picker getPicker() {
        return picker;
    }

    public void setPicker(@Nullable Picker picker) {
        this.picker = picker;
    }

    @Nullable
    public OnFormEntityChangeListener getOnFormEntityChangeListener() {
        return onFormEntityChangeListener;
    }

    public void setOnFormEntityChangeListener(@Nullable OnFormEntityChangeListener listener) {
        this.onFormEntityChangeListener = listener;
    }
}
