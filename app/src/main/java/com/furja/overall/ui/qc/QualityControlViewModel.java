package com.furja.overall.ui.qc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QualityControlViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QualityControlViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}