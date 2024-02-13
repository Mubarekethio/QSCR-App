package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import com.mukee.amharic.qafaraf.voicecommand.qscra.BtManager.*
import com.mukee.amharic.qafaraf.voicecommand.qscra.MainViewModel
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.ShareData
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.FragmentAudioBinding
import com.mukee.amharic.qafaraf.voicecommand.qscra.ui.ProbabilitiesAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import org.tensorflow.lite.support.label.Category


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



    }
    companion object {
        //lateinit var mmSocket: BluetoothSocket
        var connectedThread: ConnectedThread? = null
        //@SuppressLint("StaticFieldLeak")
        //var createConnectThread: CreateConnectThread? = null
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



