/*
  Copyright 2023 Mukee. All Rights Reserved.
    ~ Name:- Mubarek Kebede
    ~ Email:- mubare.k1449@gmail.com
    ~ Ph.Number:- +251929920355/+251914070683
    ~ Github:- https://github.com/Mubarekethio
    ~ Linkedin:- https://www.linkedin.com/in/mubarek-kebede-582012148/
 */

package com.mukee.amharic.qafaraf.voicecommand.qscra

import android.content.Context
import android.media.AudioRecord
import android.os.SystemClock
import android.util.Log
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioClassificationListener
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment.Companion.connectedThread
import kotlinx.coroutines.DelicateCoroutinesApi
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions
import java.util.Locale
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit



@DelicateCoroutinesApi
class AudioClassificationHelper(
    val context: Context,
    private val listener: AudioClassificationListener,
    var currentModel: String = Afaraf_MODEL,
    var classificationThreshold: Float = DISPLAY_THRESHOLD,
    var overlap: Float = DEFAULT_OVERLAP_VALUE,
    var numOfResults: Int = DEFAULT_NUM_OF_RESULTS,
    var currentDelegate: Int = 0,
    var numThreads: Int = 2
) {
    private lateinit var classifier: AudioClassifier
    private lateinit var tensorAudio: TensorAudio
    private lateinit var recorder: AudioRecord
    private lateinit var executor: ScheduledThreadPoolExecutor

    private val classifyRunnable = Runnable {
        classifyAudio()
    }

    init {
        initClassifier()
    }

    fun initClassifier() {
        // Set general detection options, e.g. number of used threads
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(numThreads)

        // Use the specified hardware for running the model. Default to CPU.
        // Possible to also use a GPU delegate, but this requires that the classifier be created
        // on the same thread that is using the classifier, which is outside of the scope of this
        // sample's design.
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        // Configures a set of parameters for the classifier and what results will be returned.
        val options = AudioClassifier.AudioClassifierOptions.builder()
            .setScoreThreshold(classificationThreshold)
            .setMaxResults(numOfResults)
            .setBaseOptions(baseOptionsBuilder.build())
            .build()

        try {
            // Create the classifier and required supporting objects
            classifier = AudioClassifier.createFromFileAndOptions(context, currentModel, options)
            tensorAudio = classifier.createInputTensorAudio()
            recorder = classifier.createAudioRecord()
            startAudioClassification()
        } catch (e: IllegalStateException) {
            listener.onError(
                "Audio Classifier failed to initialize. See error logs for details"
            )


            Log.e("AudioClassification", "TFLite failed to load with error: " + e.message)
        }
    }

    fun startAudioClassification() {
        if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return
        }

        recorder.startRecording()
        executor = ScheduledThreadPoolExecutor(1)

        // Each model will expect a specific audio recording length. This formula calculates that
        // length using the input buffer size and tensor format sample rate.
        // For example, Afaraf expects 0.975 second length recordings.
        // This needs to be in milliseconds to avoid the required Long value dropping decimals.
        val lengthInMilliSeconds = ((classifier.requiredInputBufferSize * 1.0f) /
                classifier.requiredTensorAudioFormat.sampleRate) * 1000

        val interval = (lengthInMilliSeconds * (1 - overlap)).toLong()

        executor.scheduleAtFixedRate(
            classifyRunnable,
            0,
            interval,
            TimeUnit.MILLISECONDS)
    }

    private fun classifyAudio() {
        tensorAudio.load(recorder)
        var inferenceTime = SystemClock.uptimeMillis()
        //optlistener.onResult(currentModel, classificationThreshold, overlap, numOfResults, currentDelegate, numThreads)
        //var cmdText: String? = null
        val output = classifier.classify(tensorAudio)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        listener.onResult(output[0].categories, inferenceTime)

        if (output[0].categories.isNotEmpty()){
            //println(output[0].categories[0].label.toString())
            //cmdText = output[0].categories[0].label.toString()
            val cmdText: String = when (output[0].categories[0].label.toString().lowercase(Locale.getDefault())) {
                "derreh", "ወደ ሆላ" -> "derreh"
                "gurra", "ወደ ግራ" -> "gurra"
                "migda", "ወደ ቀኝ" -> "migda"
                "qembis", "ጀምር" -> "qembis"
                "solis", "ቁም" -> "solis"
                //"background", "_noise" -> "N"
                //"unknown", "_unknown" -> "u"
                else-> "av"

                //"a_noise", "_noise" -> cmdText = "N"
                //else -> //println("not labeled") }

            }
            if (connectedThread != null) {
                connectedThread!!.write(cmdText)
            }




        }


    }

    fun stopAudioClassification() {
        recorder.stop()
        executor.shutdownNow()
    }
    companion object{
        const val DELEGATE_CPU = 0
        const val DELEGATE_NNAPI = 1
        const val DISPLAY_THRESHOLD = 0.8f
        const val DEFAULT_NUM_OF_RESULTS = 1
        const val DEFAULT_OVERLAP_VALUE = 0.5f
        const val Afaraf_MODEL = "Qf_mM_V700.tflite" //Qafar-af-V5.tflite" //"Qafar-af-V6.tflite//qafaraf-mm-v5.tflite" //"afaraf.tflite"//"Qf_mM_V99.tflite"
        const val Amharic_MODEL = "amharic-1.tflite"
    }
}

