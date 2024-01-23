package com.mukee.amharic.qafaraf.voicecommand.qscra

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "my_preference"

const val PREFERENCE_NAME2 = "my_preference2"
//const val PREFERENCE_NAME3 = "my_preference3"


class DataStoreRepository(context: Context) {

    private object PreferenceKeys {
        val numResult = preferencesKey<Int>("num_result")
        val threShold= preferencesKey<Float>("threshold")
        val numThread= preferencesKey<Int>("num_thread")

        //val curModel = preferencesKey<Boolean>("cur_model")

        val currModel = preferencesKey<String>("curr_model")

        val curDelgate = preferencesKey<Int>("cur_delgate")
        val overlap= preferencesKey<Int>("overlap")

        val clicked= preferencesKey<Boolean>("isClicked")
        val floatSt= preferencesKey<String>("sts")

        //to Change the title from the selected model
        //val toTitle= preferencesKey<String>("totitle")

    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    private val dataStore2: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME2
    )


    //selectedModel: Boolean

    suspend fun saveToDataStore(numResult: Int, threShold: Float, numThread: Int, currModel: String,
                                curDelgate: Int, overlap: Int){
        dataStore.edit { preference ->
            preference[PreferenceKeys.numResult] = numResult
            preference[PreferenceKeys.threShold]= threShold
            preference[PreferenceKeys.numThread]= numThread

            preference[PreferenceKeys.currModel] = currModel

            //preference[PreferenceKeys.curModel]= selectedModel
            preference[PreferenceKeys.curDelgate]= curDelgate
            preference[PreferenceKeys.overlap]= overlap

        }
    }

    suspend fun saveData(floatSt:String,clicked: Boolean ) {
        dataStore2.edit { preference ->
            preference[PreferenceKeys.floatSt]=floatSt
            preference[PreferenceKeys.clicked] = clicked
        }
    }


    val readFromDataStoreResult: Flow<Int> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val nResult = preference[PreferenceKeys.numResult] ?: 1
            nResult
        }






    val readFromDataStoreThershold: Flow<Float> =dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val nTheshold = preference[PreferenceKeys.threShold] ?: 0.8F
            nTheshold//myAge
        }

    val readFromDataStoreThread: Flow<Int> =dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val nThread = preference[PreferenceKeys.numThread] ?: 2
            nThread
        }


    val readFromDataStorecModel: Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val currModel = preference[PreferenceKeys.currModel] ?: "Qafar-af"
            currModel
        }


    val readFromDataStoreDelgate: Flow<Int> =dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val nCurDelgate = preference[PreferenceKeys.curDelgate] ?: 0
            nCurDelgate
        }
    val readFromDataStoreOverlap: Flow<Int> =dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val nOverlap = preference[PreferenceKeys.overlap] ?: 2
            nOverlap
        }

    val readFromDataStorestsbtn: Flow<String> = dataStore2.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val floatSt = preference[PreferenceKeys.floatSt] ?: "stop"
            floatSt
        }


}


/*

val readFromDataStoreModel: Flow<Boolean> =dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val selectedModel = preference[PreferenceKeys.curModel] ?: false
            selectedModel
        }





val readFromDataStoreToolbardbtn: Flow<String> = dataStore3.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val title = preference[PreferenceKeys.toTitle] ?: "Qafaraf Command"
            title
        }

 */