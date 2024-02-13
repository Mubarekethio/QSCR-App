package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.getDefaultAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.finishAfterTransition
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.mukee.amharic.qafaraf.voicecommand.qscra.AudioClassificationHelper
import com.mukee.amharic.qafaraf.voicecommand.qscra.MainViewModel
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.ShareData
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.FragmentAudioBinding
import com.mukee.amharic.qafaraf.voicecommand.qscra.ui.ProbabilitiesAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import org.tensorflow.lite.support.label.Category
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


interface AudioClassificationListener {
    fun onError(error: String)
    fun onResult(results: List<Category>, inferenceTime: Long)
}


@DelicateCoroutinesApi
class AudioFragment : Fragment() {

    private lateinit var mainAudViewModel: MainViewModel
    //private var nOverlap: Int= 2

    //private var mmm: String=""
    //var  moddd = ""

    //private var txr: Boolean=false
    private val clicked1= true


    private var _fraudioBinding: FragmentAudioBinding? = null
    private val frAudioBinding get() = _fraudioBinding!!

    private val adapter by lazy { ProbabilitiesAdapter() }
    private lateinit var audioHelper: AudioClassificationHelper


    private val audioClassificationListener = object : AudioClassificationListener {
        @SuppressLint("MissingPermission", "NotifyDataSetChanged")
        override fun onResult(results: List<Category>, inferenceTime: Long) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                adapter.notifyDataSetChanged()
                if(inferenceTime.toString().isNotEmpty()){
                    frAudioBinding.inferenceTimeVal.text =String.format("%d ms", inferenceTime)
                }
                }
            }
        @SuppressLint("NotifyDataSetChanged")
        override fun onError(error: String) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _fraudioBinding = FragmentAudioBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_audio, container, false)
        return frAudioBinding.root
    }



    //@OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setHasOptionsMenu(false)

        //to Receive data from SettingFragment
        mainAudViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //to send data from fragment to mainActivity for toolbar
        val viewModel = ViewModelProvider(requireActivity())[ShareData::class.java]


        //if (connectedThread != null) {
            //connectedThread!!.write(cmdText)
        //}

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            //findNavController().navigate(R.id.audioFragment)
            finishAfterTransition(requireActivity())

        }

        //var txr=
        //To receive data from AudioFragment for Toolbar title
        viewModel.getStartB().observe(viewLifecycleOwner) { item2 ->
            mainAudViewModel.saveData(item2, clicked1)
        }

        frAudioBinding.recyclerView.adapter = adapter


        mainAudViewModel.readFromDataStorestsbtn.observe(viewLifecycleOwner) { sts ->
            //println("fromSaved $sts")
            frAudioBinding.txt7.text = sts
            if (sts=="stop"){
                frAudioBinding.inferenceTimeVal.text= null
                //Toast.makeText(requireContext(), "Classification Stopped.", Toast.LENGTH_SHORT).show()
                //arrayOfNulls<>().also { frAudioBinding.recyclerView.adapter = it }//adapter.categoryList[].null
                //view.findNavController().navigate(R.id.audioFragment)
                onPause()

            }else{

                Toast.makeText(requireContext(), "Classification Started.", Toast.LENGTH_SHORT).show()
                audioHelper = AudioClassificationHelper(
                    requireContext(),
                    audioClassificationListener
                )
                mainAudViewModel.readFromDataStorecModel.observe(viewLifecycleOwner) { cModel ->
                    //println(cModel)
                    when(cModel){
                        "Amharic"->{
                            audioHelper.stopAudioClassification()
                            audioHelper.currentModel = AudioClassificationHelper.Amharic_MODEL
                            frAudioBinding.txt.text = getString(R.string.AmharicModel)

                            //Sending Data to mainActivity for toolbar
                            //viewModel.setToolbar("አማርኛ")
                            audioHelper.initClassifier()
                            audioHelper.startAudioClassification()

                        }
                        "Qafar-af"->{
                            audioHelper.stopAudioClassification()
                            audioHelper.currentModel = AudioClassificationHelper.Afaraf_MODEL
                            frAudioBinding.txt.text = getString(R.string.QafarafModel)
                            //Sending Data to mainActivity for toobar
                            //viewModel.setToolbar("Qafar-af")
                            audioHelper.initClassifier()
                            audioHelper.startAudioClassification()

                        }
                    }
                }


                mainAudViewModel.readFromDataStoreDelgate.observe(viewLifecycleOwner) { nCurDelgate ->
                    audioHelper.stopAudioClassification()
                    audioHelper.currentDelegate = nCurDelgate
                    audioHelper.initClassifier()
                    audioHelper.startAudioClassification()
                    when(nCurDelgate){
                        0->frAudioBinding.txt1.text = getString(R.string.cpuDelgate)
                        1->frAudioBinding.txt1.text = getString(R.string.NNAPIDelegate)
                    }

                }
                mainAudViewModel.readFromDataStoreOverlap.observe(viewLifecycleOwner) { nnOverlap ->
                    audioHelper.stopAudioClassification()
                    audioHelper.overlap = 0.25F * nnOverlap
                    audioHelper.initClassifier()
                    audioHelper.stopAudioClassification()

                    frAudioBinding.txt2.text = buildString {
                        append(getString(R.string.nuOverlap))
                        append(" ")
                        append(audioHelper.overlap.toString())
                    }
                }

                mainAudViewModel.readFromDataStoreResult.observe(viewLifecycleOwner) { nResult ->
                    audioHelper.stopAudioClassification()
                    audioHelper.numOfResults = nResult
                    audioHelper.initClassifier()
                    audioHelper.startAudioClassification()
                    frAudioBinding.txt3.text = buildString {
                        append(getString(R.string.nuResult))
                        append(" ")
                        append(audioHelper.numOfResults.toString())
                    }
                }

                mainAudViewModel.readFromDataStoreThershold.observe(viewLifecycleOwner) { nTheshold ->
                    audioHelper.stopAudioClassification()
                    audioHelper.classificationThreshold = nTheshold
                    audioHelper.initClassifier()
                    audioHelper.startAudioClassification()

                    frAudioBinding.txt4.text = buildString {
                        append(getString(R.string.threshold))
                        append(" ")
                        append(audioHelper.classificationThreshold)
                    }
                }
                mainAudViewModel.readFromDataStoreThread.observe(viewLifecycleOwner) { nThread ->
                    audioHelper.stopAudioClassification()
                    audioHelper.numThreads = nThread
                    audioHelper.initClassifier()
                    audioHelper.startAudioClassification()

                    frAudioBinding.txt5.text = buildString {
                        append(getString(R.string.thread))
                        append(" ")
                        append(audioHelper.numThreads)
                    }
                }
                //onResume()
            }
        }










            /* when (item2) {
                 "start" -> {
                     //txr=true
                     frAudioBinding.txt6.text = item2
                     onResume()
                     Toast.makeText(requireContext(), "Classification Started.", Toast.LENGTH_SHORT).show()
                 }
                 "stop" -> {
                     //txr= false
                     frAudioBinding.txt6.text = item2
                     onPause()
                     frAudioBinding.inferenceTimeVal.text = null
                     Toast.makeText(requireContext(), "Classification Stopped.", Toast.LENGTH_SHORT)
                         .show()
                 }
             }

             */



        //to read saved state o



        //val txr= frAudioBinding.txt6.text.toString()
        //frAudioBinding.txt7.text=txr.toString()

        //if(txr){
            //frAudioBinding.txt7.text=txr.toString()

        //}else{
            //frAudioBinding.recyclerView.adapter = adapter
        //}





        /*val callback =requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigate(R.id.aboutFragment)
        }
         */






    }


    ///BTMANAGER CLASS
    @SuppressLint("MissingPermission")
    class CreateConnectThread(bluetoothAdapter: BluetoothAdapter, address: String?) :
        Thread() {
        init {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            var tmp: BluetoothSocket? = null
            val uuid = bluetoothDevice.uuids[0].uuid
            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                //tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Socket's create() method failed", e)
            }
            if (tmp != null) {
                mmSocket = tmp
            }
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.

            @Suppress("DEPRECATION") val bluetoothAdapter = getDefaultAdapter()
            bluetoothAdapter.cancelDiscovery()
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect()
                Log.e("Status", "Device connected")
                handler!!.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget()
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close()
                    Log.e("Status", "Cannot connect to device")
                    handler!!.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget()
                } catch (closeException: IOException) {
                    Log.e(ContentValues.TAG, "Could not close the client socket", closeException)
                }
                return
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = ConnectedThread(mmSocket)
            connectedThread!!.start()
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel1() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }
    }


    /* =============================== Thread for Data Transfer =========================================== */
    class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket!!.inputStream
                tmpOut = mmSocket.outputStream
            } catch (_: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            val buffer = ByteArray(1024) // buffer store for the stream
            var bytes = 0 // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = mmInStream!!.read().toByte()
                    var readMessage: String
                    if (buffer[bytes] == '\n'.code.toByte()) {
                        readMessage = String(buffer, 0, bytes)
                        Log.e("Arduino Message", readMessage)
                        handler!!.obtainMessage(MESSAGE_READ, readMessage).sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        fun write(input: String?) {
            val bytes = input!!.toByteArray() //converts entered String into bytes
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (_: IOException) {
            }
        }


    }

    /*/* ============================ Terminate Connection at BackPress ====================== */
    override fun onBackPressed() {
        //  // Terminate Bluetooth Connection and close app
        if (createConnectThread != null) {
            createConnectThread!!.cancel()
        }
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }*/

    companion object {
        var handler: Handler? = null
        lateinit var mmSocket: BluetoothSocket

        var connectedThread: ConnectedThread? = null
        var createConnectThread: CreateConnectThread? = null
        const val CONNECTING_STATUS = 1

        // used in bluetooth handler to identify message status
        const val MESSAGE_READ = 2 // used in bluetooth handler to identify message update
    }







    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(AudioFragmentDirections.actionAudioFragmentToPermissionFragment())
        }

        if (::audioHelper.isInitialized ) {
            audioHelper.startAudioClassification()
        }
    }


    override fun onPause() {
        super.onPause()
        if (::audioHelper.isInitialized ) {
            audioHelper.stopAudioClassification()
            frAudioBinding.inferenceTimeVal.text=null
            //frAudioBinding.recyclerView.adapter=null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fraudioBinding = null
    }

}



/*if(sts=="start"){
                frAudioBinding.txt6.text = sts
                //audioHelper.startAudioClassification()
                //audioHelper.initClassifier()
                onResume()
            }
            else{
                frAudioBinding.txt6.text = sts
                frAudioBinding.recyclerView.adapter = adapter
                onPause()
            }

             */


/*
 */




/*when(sts){
    "start"->{


    }
    "stop"-> {
        //txr=false
        frAudioBinding.txt6.text = sts
        onPause()
        //frAudioBinding.recyclerView.adapter = adapter
    }
}

 */

/*

/*
        mainAudViewModel.readFromDataStoreModel.observe(viewLifecycleOwner) { selectedModel ->
            //isSelected= selectedModel.toString()
            if (selectedModel) {
                audioHelper.stopAudioClassification()
                audioHelper.currentModel = AudioClassificationHelper.Amharic_MODEL
                frAudioBinding.txt.text = getString(R.string.AmharicModel)
                //Sending Data to mainActivity for toobar
                viewModel.setToolbar("አማርኛ")
                audioHelper.initClassifier()
            } else {
                audioHelper.stopAudioClassification()
                audioHelper.currentModel = AudioClassificationHelper.Afaraf_MODEL
                frAudioBinding.txt.text = getString(R.string.QafarafModel)
                //Sending Data to mainActivity for toobar
                viewModel.setToolbar("Qafar-af")

                audioHelper.initClassifier()
            }
        }
         */







 */

        //val sp = arguments?.getString("ischeck")

        //println(sp)
        /*frAudioBinding.btn1.setOnClickListener {
            val action = AudioFragmentDirections.actionAudioFragmentToSettingFragment()
            audioHelper.stopAudioClassification()
            findNavController().navigate(action)
            //Navigation.findNavController(view).navigate(R.id.action_audioFragment_to_settingFragment)
        }
        frAudioBinding.btn2.setOnClickListener {

            val action = AudioFragmentDirections
                .actionAudioFragmentToAboutFragment()
            audioHelper.stopAudioClassification()
            findNavController().navigate(action)



        }

         */




//mainAudViewModel.readFromDataStoreRecordbtn.observe(viewLifecycleOwner) { isClicked ->
//print(isClicked)
//}

//mainAudViewModel.readFromDataStorestsbtn.observe(viewLifecycleOwner){stsBtn->
//println(stsBtn)

//ststop= stsBtn
//println(ststop)
//}

/*

/*mainAudViewModel.readFromDataStoreRecordbtn.observe(viewLifecycleOwner) {
    state = it
    frAudioBinding.txt6.text=state.toString()
    //println(state)
}

 */
//println(state) //state

//val ststop = arguments?.getString("ischeck")

mainAudViewModel.readFromDataStorestsbtn.observe(viewLifecycleOwner){ stsbtn->
    val ststop = stsbtn
    println(stsbtn)
    frAudioBinding.txt6.text=ststop
}



/*mainAudViewModel.readFromDataStoreRecordbtn.observe(viewLifecycleOwner) { clicked->
    //frAudioBinding.txt6.text=clicked.toString()
    //println(clicked)
    //frAudioBinding.
}

 */

//println(ststop)
/*when(ststop){
    "Start"-> {

        audioHelper.startAudioClassification()
        //audioHelper.initClassifier()

        //onResume()
    }
    "Stop"->{
        onPause()
        //audioHelper.stopAudioClassification()
    }
}


 */


/*ststop = arguments?.getString("ischeck").toString()
println(ststop)
when (ststop) {
    "Stop" -> {
        //onPause()
        audioHelper.stopAudioClassification()
        onPause()
    }
    "Start" -> {
        onResume()
    }
}


 */

 */



