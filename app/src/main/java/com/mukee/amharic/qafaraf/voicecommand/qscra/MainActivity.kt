package com.mukee.amharic.qafaraf.voicecommand.qscra


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.ActivityMainBinding
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment.Companion.CONNECTING_STATUS
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment.Companion.createConnectThread
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment.Companion.handler
import kotlinx.coroutines.DelicateCoroutinesApi

@Suppress("DEPRECATION")
@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(){

    private var backPressedTime: Long = 0
    private lateinit var mainActmodel: MainViewModel
    private lateinit var activityMainBinding: ActivityMainBinding
    //private var stst:String=""//"stop"
    //private var clicked1=false
    //var mmm="stop"

    //private lateinit var viewModel: ShareData


    //private var ttBarT: String? =null
    private var deviceName: String? = null
    private var deviceAddress: String? = null


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.bottomNavView.background= null
        activityMainBinding.connectedBT.visibility = View.INVISIBLE


        //To Save Data in view model
        mainActmodel = ViewModelProvider(this)[MainViewModel::class.java]

        //To recieve data from AudioFragment for Toolbar title
        val viewModel = ViewModelProvider(this)[ShareData::class.java]


        val bottomNavigationView= activityMainBinding.bottomNavView
        val btnSt= activityMainBinding.start14
        val toolbar =activityMainBinding.toolbar // findViewById<Toolbar>(R.id.toolbar)

        val navController =findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)
        //setupActionBarWithNavController(findNavController(R.id.fragmentContainerView))


        //To recieve data from AudioFragment for Toolbar title
        viewModel.getToolbar().observe(this) { item ->
            if(item!=null){
                toolbar.title= item
            }
        }


        //To send the start and stop icon to start and stop the classification process
        var pras ="stop"
        btnSt.setOnClickListener{
            //findNavController().navigate(R.id.audioFragment)

            //To Send state of the record icon to AudioFragment
            when(pras){
                "stop"-> {
                    pras= "start"
                    activityMainBinding.start14.setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.pause_24))
                    navController.navigate(R.id.audioFragment)
                }
                "start"-> {
                    pras= "stop"
                    Toast.makeText(this, "Classification Stopped.", Toast.LENGTH_SHORT).show()
                    activityMainBinding.start14.setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.play_24))

                }
            }
            viewModel.setStartB(pras)

            //println("from main: $sv")
        }







        //Receiving device nam and address from the bluetooth manager
        deviceName = intent.getStringExtra("deviceName")//arguments?.getString("deviceName") //
        //println(deviceName)
        if (deviceName != null) {
            //onPause()
            // Get the device address to make BT Connection
            deviceAddress = intent.getStringExtra("deviceAddress")//arguments?.getString("deviceAddress")//
            // Show progree and connection status
            //println(deviceAddress)
            toolbar.subtitle = "Connecting to $deviceName..."
            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            createConnectThread =
                bluetoothAdapter.let {
                    AudioFragment.CreateConnectThread(
                        bluetoothAdapter = it,
                        address = deviceAddress
                    )
                }
            createConnectThread!!.start()
        }

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> { toolbar.subtitle = "Connected to $deviceName"
                            activityMainBinding.connectedBT.visibility = View.VISIBLE}
                            //activityMainBinding.start14.setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.record_voice)) }

                        -1 -> { toolbar.subtitle = "Device fails to connect"
                            activityMainBinding.connectedBT.visibility = View.INVISIBLE } }
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            //finishAfterTransition()
            finish()
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
        //  // Terminate Bluetooth Connection and close app
        /*if (createConnectThread != null) {
            createConnectThread!!.cancel()
        }

         */

    }





}

/*when(sts1){

                "Start"-> sts1="Stop"
                "Stop"-> sts1="Start"
            }

             */

//clicked1
/*if(clicked1){
    sts1= "Stop"
    clicked1= false
    onPause()
}else{
    sts1= "Start"
    clicked1=true
}

 */

//mainActViewModel.saveData(sts1.toString(), clicked1)
//val bundle = bundleOf("ischeck" to sts1)
//findNavController(R.id.fragmentContainerView).navigate(R.id.audioFragment, bundle)
/*
private fun observeData(){
        mainActViewModel.readFromDataStoreRecordbtn.observe(this) {
            state = it
        }

        /*userManager.userNAmeFlow.asLiveData().observe(this) {
            stst = it
            //println(stst)
        }

         */
        /*userManager.userControlFlow.asLiveData().observe(this) {
            state = it //if (it) "Stop" else "Start"
            //println(state)
        }

         */
    }
 */



/*mai.readFromDataStoreOverlap.observe(viewLifecycleOwner) { nOverlap ->
            n_Overlap=nOverlap
            frSettingBinding.spinnerOverlap.setSelection(
                n_Overlap,
                false
            )
            nu_Overlap = 0.25f * n_Overlap
        }

         */



//var sts:String
/*activityMainBinding.start14.setOnClickListener{
    when(sts1){
        "Start"-> sts1="Stop"
        "Stop"-> sts1="Start"
    }

     //clicked1
    /*if(clicked1){
        sts1= "Stop"
        clicked1= false
        onPause()
    }else{
        sts1= "Start"
        clicked1=true
    }

     */

    mainActViewModel.saveData(sts1.toString(), clicked1)
    //val bundle = bundleOf("ischeck" to sts)
    //findNavController(R.id.fragmentContainerView).navigate(R.id.audioFragment, bundle)
 }

 */




//var sts: String= "Stop"
//var clicked1: Boolean=true
//val floatingbtn=activityMainBinding.start14
/*activityMainBinding.start14.setOnClickListener {
    //"Start"Stop
    if(clicked){
        sts= "Stop"
        clicked= false
    }else{
        sts= "Start"
        clicked=true
    }

    val bundle = bundleOf("ischeck" to sts)
    findNavController(R.id.fragmentContainerView).navigate(R.id.audioFragment, bundle)
}

 */

/*
//val floatingB =activityMainBinding.start14
//floatingB.
activityMainBinding.start14.setOnClickListener{
    bundle.putString("params", "Stop")
    if(clicked){
        bundle.putString("stop", "Stop")
        //onPause()
        //findNavController().navigate(R.id.audioFragment, bundle)
        //false
    }else{
        bundle.putString("stop", "Start")
        //fragmentB.setMenuVisibility(true)
        //onResume()
        //true
    }

 */




//}
