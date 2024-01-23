package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mukee.amharic.qafaraf.voicecommand.qscra.DeviceInfoModel
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.FragmentConnectBinding
import com.mukee.amharic.qafaraf.voicecommand.qscra.ui.DeviceListAdapter
import kotlinx.coroutines.DelicateCoroutinesApi


@Suppress("DEPRECATION")
@DelicateCoroutinesApi
class ConnectFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.S)
    private val PERMISSIONLOCATION= arrayOf(
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    private var _frconnectBinding: FragmentConnectBinding? = null
    private val frconnectBinding get() = _frconnectBinding!!


    //private val adapter by lazy { return@lazy DeviceListAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        //val view = inflater.inflate(R.layout.fragment_connect, container, false)
        _frconnectBinding = FragmentConnectBinding.inflate(inflater, container, false)

        return frconnectBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireActivity()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigate(R.id.audioFragment)
        }
        //
        /*val callback= requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
                // (https://issuetracker.google.com/issues/139738913)
                finishAfterTransition(requireActivity())
            } else {
                findNavController().navigate(R.id.audioFragment)
                //super.onBackPressed()
            }
            //
        }

         */



        val permission1 =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN)
        val permission2 =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)

        //val permission2 =
        //  ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONLOCATION,
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONLOCATION, 1)
        }



        val bluetoothManager: BluetoothManager = getSystemService(requireContext(),BluetoothManager::class.java)!!
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter?.isEnabled== false){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val requestEnable = 1 //REQUEST_ENABLE_BT

            this.startActivityForResult(enableBtIntent, requestEnable)
        }
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        //}
        //val pairedDevices = bluetoothAdapter.bondedDevices
        val deviceList: MutableList<Any> = ArrayList()
        if (pairedDevices != null) {
            if (pairedDevices.isNotEmpty()) {
                // There are paired devices. Get the name and address of each paired device.
                for (device: BluetoothDevice in pairedDevices) {
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    //val gettitle = ""
                    val deviceInfoModel =
                        DeviceInfoModel(deviceName, deviceHardwareAddress)//, gettitle)
                    deviceList.add(deviceInfoModel)
                }

                // Display paired device using recyclerView
                val recyclerView = frconnectBinding.recyclerViewDevice//findViewById<RecyclerView>(R.id.recyclerViewDevice)
                recyclerView.layoutManager = LinearLayoutManager(requireActivity())
                val deviceListAdapter = DeviceListAdapter(requireContext(), deviceList)
                recyclerView.adapter = deviceListAdapter
                recyclerView.itemAnimator = DefaultItemAnimator()
            }else{
                Toast.makeText(requireContext(),
                    "First Pair a Bluetooth device from you device Setting", Toast.LENGTH_LONG).show()
            }

            }

        }

    /*@RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(ConnectFragmentDirections.actionConnectFragmentToPermissionFragment())
        }

    }
     */

    override fun onDestroyView() {
        super.onDestroyView()
        //findNavController().navigate(R.id.audioFragment)
        _frconnectBinding = null
    }





}






/*private fun buttonSave() {

    //Gets the user input and saves it
    frconnectBinding.btnSave.setOnClickListener {
        frconnectBinding.tvAge.text = name.toString()
        frconnectBinding.tvName.text = age.toString()
        frconnectBinding.tvGender.text = gender.toString()

        //name = frconnectBinding.etName.text.toString()
        //age = frconnectBinding.etAge.text.toString().toInt()
        val isMale = frconnectBinding.switchGender.isChecked

        //Stores the values
        GlobalScope.launch {
            settingManager.storeUser(age.toInt(), name, isMale)
        }
    }

}


/*mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    mainViewModel.readFromDataStore55.observe(viewLifecycleOwner) { myName ->
        frconnectBinding.tvName.text = myName //name_txt.text
    }
    mainViewModel.readFromDataStore2.observe(viewLifecycleOwner) { myAge ->
        frconnectBinding.tvAge.text = myAge.toString()//name_txt.text
    }
    mainViewModel.readFromDataStore3.observe(viewLifecycleOwner) { myGender ->

        if (myGender){
            frconnectBinding.tvGender.text = "Male"
        }else{
            frconnectBinding.tvGender.text = "Female"
        }
        //val isMale = switch_gender.isChecked
        //myGender = if (myGender) "Male" else "Female"
        //tv_gender.text = gender
        frconnectBinding.tvGender.text = myGender.toString() //name_txt.text
    }

    frconnectBinding.btnSave.setOnClickListener {
        val myName = frconnectBinding.etName.text.toString()
        val myAge = frconnectBinding.etAge.text.toString()
        val myGender = frconnectBinding.etName.text.toString()
        mainViewModel.saveToDataStore(myName, myAge.toInt(), myGender.toBoolean())
    }

    */
    // Finds the TextView in the cust
    /*frconnectBinding.okbtn.setOnClickListener{
        val action = ConnectFragmentDirections.actionConnectFragmentToSettingFragment()
        findNavController().navigate(action)
    }
     */
 */