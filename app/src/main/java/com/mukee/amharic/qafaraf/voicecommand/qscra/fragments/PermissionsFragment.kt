package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.PermissionsFragmentDirections.actionPermissionFragmentToAudioFragment


//@RequiresApi(Build.VERSION_CODES.S)
//private val PERMISSIONS_REQUIRED = arrayOf(permission.RECORD_AUDIO)
//private val PERMISSIONS_REQUIRED1=arrayOf(permission.BLUETOOTH_ADMIN)

@RequiresApi(Build.VERSION_CODES.S)
private val PermissionRequired = arrayOf(permission.RECORD_AUDIO)





/*
 * The sole purpose of this fragment is to request permissions and, once granted, display the
 * audio fragment to the user.
 */
class PermissionsFragment : Fragment() {



    @RequiresApi(Build.VERSION_CODES.S)
    private val PERMISSIONLOCATION= arrayOf(
        permission.RECORD_AUDIO,
        permission.BLUETOOTH_ADMIN,
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        permission.BLUETOOTH_SCAN,
        permission.BLUETOOTH_CONNECT,
        permission.BLUETOOTH_PRIVILEGED
    )

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                navigateToAudioFragment()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    //@RequiresApi(Build.VERSION_CODES.S)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val permission1 =
            ContextCompat.checkSelfPermission(requireContext(), permission.BLUETOOTH_ADMIN)
        val permission2=
            ContextCompat.checkSelfPermission(requireContext(), permission.RECORD_AUDIO)


        if(permission1!= PackageManager.PERMISSION_GRANTED || PackageManager.PERMISSION_GRANTED==permission2){
            navigateToAudioFragment()
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONLOCATION,
                1
            )
        }
        else {
            with(requestPermissionLauncher) {
                launch(permission.RECORD_AUDIO)
            }
        }

    }

    private fun navigateToAudioFragment() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView).navigate(
                actionPermissionFragmentToAudioFragment()
            )
        }
    }

    companion object {
        /** Convenience method used to check if all permissions required by this app are granted */
        @RequiresApi(Build.VERSION_CODES.S)
        fun hasPermissions(context: Context) = PermissionRequired.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            //ContextCompat.checkSelfPermission(context, it)==PackageManager.PERMISSION_GRANTED
        }

    }
}