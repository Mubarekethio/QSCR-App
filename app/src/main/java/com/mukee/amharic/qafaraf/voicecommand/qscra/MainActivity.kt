package com.mukee.amharic.qafaraf.voicecommand.qscra



import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mukee.amharic.qafaraf.voicecommand.qscra.BtManager.Companion.handler
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.ActivityMainBinding
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment.Companion.CONNECTING_STATUS
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(){

    private var backPressedTime: Long = 0
    private lateinit var mainActmodel: MainViewModel
    private lateinit var activityMainBinding: ActivityMainBinding

    private var deviceName: String? = null
    private var deviceAddress: String? = null


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Hide background of the bottom navigation view
        activityMainBinding.bottomNavView.background = null

        // Hide connectedBT view by default
        activityMainBinding.connectedBT.visibility = View.INVISIBLE

        // Initialize ViewModel to save data
        mainActmodel = ViewModelProvider(this)[MainViewModel::class.java]

        // Initialize ViewModel to receive data from AudioFragment for Toolbar title
        val viewModel = ViewModelProvider(this)[ShareData::class.java]

        // Initialize views and navigation controller
        val bottomNavigationView = activityMainBinding.bottomNavView
        val btnSt = activityMainBinding.start14
        val toolbar = activityMainBinding.toolbar
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)

        // Observe changes in toolbar title and update UI accordingly
        viewModel.getToolbar().observe(this) { item ->
            item?.let {
                toolbar.title = item
            }
        }

        // Handle click event of start button to start/stop the classification process
        var pras = "stop"
        btnSt.setOnClickListener {
            // Send state of the record icon to AudioFragment
            when (pras) {
                "stop" -> {
                    pras = "start"
                    activityMainBinding.start14.setImageDrawable(
                        ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.pause_24
                        )
                    )
                    navController.navigate(R.id.audioFragment)
                }

                "start" -> {
                    pras = "stop"
                    Toast.makeText(this, "Classification Stopped.", Toast.LENGTH_SHORT).show()
                    activityMainBinding.start14.setImageDrawable(
                        ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.play_24
                        )
                    )
                }
            }
            viewModel.setStartB(pras)
        }

        // Initialize Bluetooth connection
        handleBluetoothConnection()

        // Initialize handler for updating UI based on Bluetooth connection status
        initHandler()
    }


    /* Handles Bluetooth connection.
    */
    private fun handleBluetoothConnection() {
        deviceName = intent.getStringExtra("deviceName")
        if (deviceName != null) {
            deviceAddress = intent.getStringExtra("deviceAddress")
            activityMainBinding.toolbar.subtitle = "Connecting to $deviceName..."

            try {
                val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val bluetoothAdapter = bluetoothManager?.adapter

                if (bluetoothAdapter != null) {
                    val createConnectThread = BtManager.CreateConnectThread(
                        context = applicationContext,
                        address = deviceAddress
                    )
                    createConnectThread.start()
                } else {
                    Log.e("Bluetooth Error", "Bluetooth adapter not available.")
                    Toast.makeText(this, "Bluetooth adapter not available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Bluetooth Error", "Error establishing Bluetooth connection", e)
                Toast.makeText(this, "Error connecting to Bluetooth device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initializes the handler for updating UI elements based on Bluetooth connection status.
     */
    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                try {
                    when (msg.what) {
                        CONNECTING_STATUS -> {
                            when (msg.arg1) {
                                1 -> {
                                    activityMainBinding.toolbar.subtitle = "Connected to $deviceName"
                                    activityMainBinding.connectedBT.visibility = View.VISIBLE
                                }
                                -1 -> {
                                    activityMainBinding.toolbar.subtitle = "Device fails to connect"
                                    activityMainBinding.connectedBT.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Handler Error", "Error handling message: ${msg.what}", e)
                }
            }
        }
    }



    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

     */

    /**
     * Handles the back button press event.
     */
    @Deprecated("Overrides deprecated member", level = DeprecationLevel.ERROR)
    override fun onBackPressed() {
        // Your existing onBackPressed() implementation
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}

