package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mukee.amharic.qafaraf.voicecommand.qscra.MainViewModel
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.ShareData
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.FragmentSettingBinding
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class SettingFragment : Fragment() {

    private var _frSettingBinding: FragmentSettingBinding? = null
    private val frSettingBinding get() = _frSettingBinding!!
    private lateinit var mainViewModel: MainViewModel


    private var nuresult: Int= 1
    private var nuthread: Int =2
    private var nuthershold: Float =0.8F
    private var cModel: String=""
    private var nuOverlap: Float= 0.5F
    private var nOverlap: Int= 2
    private var currentDelegate: Int = 0
    //private var isModel: String = ""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _frSettingBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return frSettingBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val viewModel = ViewModelProvider(requireActivity())[ShareData::class.java]


        /*val callback =

         */
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigate(R.id.audioFragment)
        }




        // Allow the user to change the max number of results returned by the audio classifier.
        // Currently allows between 1 and 5 results, but can be edited here.

        mainViewModel.readFromDataStoreResult.observe(viewLifecycleOwner) { nResult ->
            nuresult = nResult
            frSettingBinding.resultsValue.text = nResult.toString()
            }

        frSettingBinding.resultsMinus.setOnClickListener {
            if (nuresult > 1) {
                nuresult--
                frSettingBinding.resultsValue.text = nuresult.toString()
            }
        }
        frSettingBinding.resultsPlus.setOnClickListener {
            if (nuresult < 7) {
                nuresult++
                frSettingBinding.resultsValue.text = nuresult.toString()
            }
        }

        // Allow the user to change the number of threads used for classification
        mainViewModel.readFromDataStoreThread.observe(viewLifecycleOwner) { nThread ->
            frSettingBinding.threadsValue.text = nThread.toString()
            nuthread =nThread
        }
        frSettingBinding.threadsMinus.setOnClickListener {
            if (nuthread > 1) {
                nuthread--
                frSettingBinding.threadsValue.text = nuthread.toString()
            }
        }
        frSettingBinding.threadsPlus.setOnClickListener {
            if (nuthread < 4) {
                nuthread++
                frSettingBinding.threadsValue.text = nuthread.toString()
            }
        }

        // Allow the user to change the confidence threshold required for the classifier to return
        // a result. Increments in steps of 10%.

        mainViewModel.readFromDataStoreThershold.observe(viewLifecycleOwner) { nTheshold ->
            frSettingBinding.thresholdValue.text = nTheshold.toString()//name_txt.text
            nuthershold =nTheshold
        }
        frSettingBinding.thresholdMinus.setOnClickListener {
            if (nuthershold >= 0.2) {
                //audioHelper.stopAudioClassification()
                nuthershold -= 0.1f
                //audioHelper.initClassifier()
                frSettingBinding.thresholdValue.text =
                    String.format("%.2f", nuthershold)
            }
        }
        frSettingBinding.thresholdPlus.setOnClickListener {
            if (nuthershold <= 0.8) {
                nuthershold += 0.1f
                frSettingBinding.thresholdValue.text =
                    String.format("%.2f", nuthershold)
            }
        }


        //
        //Amharic_MODEL

        // Allow the user to select between the Qafar af and Amharic language audio models.
        mainViewModel.readFromDataStorecModel.observe(viewLifecycleOwner) { currModel->
            when(currModel){
                "Qafar-af"->frSettingBinding.afaraf.isChecked=true
                "Amharic"-> frSettingBinding.amharic.isChecked=true
            }
        }
        frSettingBinding.modelSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.afaraf -> {
                    cModel ="Qafar-af"
                    viewModel.setToolbar("Qafar-af")
                }
                //Sending Data to mainActivity for toolbar }

                R.id.amharic -> {
                    cModel ="Amharic"
                    viewModel.setToolbar("አማርኛ")
                }
            }
        }




        //fragmentAudioBinding.bottomSheetLayout.label_text_view
        // Allow the user to change the amount of overlap used in classification. More overlap
        // can lead to more accurate resolves in classification.

        mainViewModel.readFromDataStoreOverlap.observe(viewLifecycleOwner) { nnOverlap ->
            nOverlap=nnOverlap
            frSettingBinding.spinnerOverlap.setSelection(
                nOverlap,
                false
            )
            nuOverlap = 0.25f * nOverlap
        }

        frSettingBinding.spinnerOverlap.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    nuOverlap = 0.25f * position
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // no op
                }
            }



        mainViewModel.readFromDataStoreDelgate.observe(viewLifecycleOwner) { nCurDelgate->
            currentDelegate= nCurDelgate
            //frSettingBinding.resultsValue.text = nCurDelgate.toString()
            frSettingBinding.spinnerDelegate.setSelection(
                currentDelegate,
                false
            )
        }

        // When clicked, change the underlying hardware used for inference. Current options are CPU
        // and NNAPI. GPU is another available option, but when using this option you will need
        // to initialize the classifier on the thread that does the classifying. This requires a
        // different app structure than is used in this sample.
        frSettingBinding.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentDelegate = position
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    /* no op */
                }
            }


        frSettingBinding.savebtn.setOnClickListener {
            val nuresult1 = frSettingBinding.resultsValue.text.toString()
            val nTheshold= frSettingBinding.thresholdValue.text.toString()
            val nThread  = frSettingBinding.threadsValue.text.toString()
            nOverlap  =frSettingBinding.spinnerOverlap.selectedItemId.toInt()
            currentDelegate= frSettingBinding.spinnerDelegate.selectedItemId.toInt()
            //println(currentModel)



            //println(nuresult1)


            //Saving Setting Parameter to MainViewModel // isModel.toBoolean()
            mainViewModel.saveToDataStore(nuresult1.toInt(), nTheshold.toFloat(), nThread.toInt(),
                currMod = cModel, currentDelegate, nOverlap, )

            view.findNavController().navigate(R.id.audioFragment)//, bundle)
            //Navigation.findNavController(requireActivity(), R.id.fragmentContainerView).navigate(
              //  SettingFragmentDirections.actionSettingFragmentToAudioFragment())
        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _frSettingBinding = null
    }
}




/*

        /*
        // Allow the user to select between the Qafar af and Amharic language audio models.
        mainViewModel.readFromDataStoreModel.observe(viewLifecycleOwner) { selectedModel->
            //currentModel= if(selectedModel) "Amharic_model" else "Qafaraf_Model"
            if(selectedModel) {
                //currentModel= AudioClassificationHelper.Amharic_MODEL// "Amharic_MODEL"
                frSettingBinding.amharic.isChecked=true
            } else{
                //currentModel=  AudioClassificationHelper.Afaraf_MODEL//"Afaraf_MODEL"
                frSettingBinding.afaraf.isChecked=true
            }
        }
        frSettingBinding.modelSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.afaraf -> {
                    isModel= "false"
                    //currentModel =AudioClassificationHelper.Afaraf_MODEL// "Afaraf_MODEL"
                }
                R.id.amharic -> {
                    isModel="true"
                    //currentModel = AudioClassificationHelper.Amharic_MODEL//"Amharic_MODEL"
                }
            }
        }
         */

 */











/*
//mainViewModel.saveAudActv(toolbar)


            //val viewModel = ViewModelProvider(requireActivity())[ShareData::class.java]

            //viewModel.setData(toolbar)//editText.text.toString())

            //if (av){
                //viewModel.setData(toolbar)//editText.text.toString())
            //}
                //val intent =Intent()
            //val button = view.findViewById<Button>(R.id.sendDatalbtn)
            /*button.setOnClickListener {
                val editText = view.findViewById<EditText>(R.id.fragment1Data)
                viewModel.setData(editText.text.toString())
            }

             */


            /*if (av){
                val intent = Intent(requireContext(), MainActivity::class.java)
                //val intent =Intent()
                // Send device details to the MainActivity
                intent.putExtra("ttBar", toolbar)

                mainViewModel.saveAudActv(toolbar)
                //findNavController().navigate(R.id.audioFragment)
                startActivity(intent)//requireActivity()
                //mainViewModel.saveAudActv(toolbar)

                //context?.startActivity(intent)

            }
            */
            //mainViewModel.saveAudActv(toolbar)



            //intent.putExtra("deviceAddress", deviceInfoModel.getDeviceHardwareAddress())



            //



            //send data from Setting fragment to Audio fragment
            /*val bundle = bundleOf("num_result" to nResult, "thresholdd" to nTheshold,
                "num_thread" to nThread, "cur_delegate" to currentDelegate.toString(), "ismodel" to isModel,
                "current_model" to currentModel, "overlap" to nu_Overlap.toString(), "isChecked" to isChecked )
             */
            //val bundle = bundleOf("num_result" to nResult,)



 */