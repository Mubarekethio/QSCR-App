package com.mukee.amharic.qafaraf.voicecommand.qscra.fragments

import android.Manifest
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

/**
 * Fragment responsible for requesting necessary permissions.
 */
@RequiresApi(Build.VERSION_CODES.S)
class PermissionsFragment : Fragment() {



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                navigateToAudioFragment()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
                // Handle permission request denied error
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionsIfNeeded()
    }

    /**
     * Requests permissions if needed.
     */
    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf<String>()

        // Check if each permission is already granted, and add it to the list if not
        for (permission in PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        // If there are permissions to request, launch the permission request
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        } else {
            // If no permissions need to be requested, navigate to the audio fragment
            navigateToAudioFragment()
        }
    }

    /**
     * Navigates to the audio fragment.
     */
    private fun navigateToAudioFragment() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView).navigate(
                actionPermissionFragmentToAudioFragment()
            )
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private val PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
        )

        /**
         * Convenience method used to check if all permissions required by this app are granted.
         */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
