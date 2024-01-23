package com.mukee.amharic.qafaraf.voicecommand.qscra

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ShareData : ViewModel() {
    private val selectedToolbar = MutableLiveData<String>()
    private val selectedStartB = MutableLiveData<String>()


    fun setToolbar(item: String) {
        selectedToolbar.value = item
    }
    fun getToolbar(): MutableLiveData<String> {
        return selectedToolbar
    }

    fun setStartB(item2: String) {
        selectedStartB.value = item2
    }

    fun getStartB(): MutableLiveData<String> {
        return selectedStartB
    }

}
