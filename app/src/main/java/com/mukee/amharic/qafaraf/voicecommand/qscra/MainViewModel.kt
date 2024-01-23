package com.mukee.amharic.qafaraf.voicecommand.qscra

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)
    //private val repo= DataStoreRepository(application)

    val readFromDataStoreResult= repository.readFromDataStoreResult.asLiveData()
    val readFromDataStoreThershold = repository.readFromDataStoreThershold.asLiveData()
    val readFromDataStoreThread = repository.readFromDataStoreThread.asLiveData()

    val readFromDataStorecModel= repository.readFromDataStorecModel.asLiveData()
    val readFromDataStoreDelgate = repository.readFromDataStoreDelgate.asLiveData()
    val readFromDataStoreOverlap = repository.readFromDataStoreOverlap.asLiveData()

    //val readFromDataStoreModel= repository.readFromDataStoreModel.asLiveData()
    //val readFromDataStoreRecordbtn =repository.readFromDataStoreRecordbtn.asLiveData()
    val readFromDataStorestsbtn=repository.readFromDataStorestsbtn.asLiveData()
    //val readFromDataStoreToolbardbtn =repository.readFromDataStoreToolbardbtn.asLiveData()

    //selectedModel: Boolean,

    fun saveToDataStore(nResult: Int, nTheshold:Float, nThread: Int, currMod: String,
                        nCurDelgate: Int, nOverlap: Int, ) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(nResult , nTheshold, nThread, currMod, nCurDelgate, nOverlap, )

    }


    fun saveData(floatSt: String, clicked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveData(floatSt, clicked)
    }


}