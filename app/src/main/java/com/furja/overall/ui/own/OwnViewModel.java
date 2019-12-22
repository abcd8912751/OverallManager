package com.furja.overall.ui.own;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OwnViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OwnViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}