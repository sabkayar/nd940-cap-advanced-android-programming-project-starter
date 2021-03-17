package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.loadUrl
import com.example.android.politicalpreparedness.network.models.Address
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class RepresentativeFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }
    lateinit var binding: FragmentRepresentativeBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.address = Address()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.loadUrlIntent.observe(viewLifecycleOwner, Observer {
            requireActivity().loadUrl(it)
        })

        val adapter = RepresentativeAdapter(viewModel)
        binding.representativesRecyclerView.adapter = adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.useMyLocationClicked.observe(viewLifecycleOwner, Observer {
            it?.let { clicked ->
                if (clicked) {
                    if (isLocationPermissionsGranted()) {
                        requestLocation()
                    } else {
                        requestLocationPermissions()
                    }
                    viewModel.useMyLocationClickDone()
                }
            }
        })

        viewModel.geoAddress.observe(viewLifecycleOwner, Observer { address ->
            address?.let {
                binding.address=address
                viewModel.callRepresentativesApi(address)
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    private val locationRequest: LocationRequest
        get() {
            return LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }

    private fun requestLocationPermissions() {
        when {
            isLocationPermissionsGranted() -> {
                // Perform Action
                requestLocation()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION) ->
                showSettingsPrompt()
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) ->
                showSettingsPrompt()
            else -> {
                requestPermissions(arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_CODE_FOREGROUND_LOCATION)
            }

        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    /*exception.startResolutionForResult(requireActivity(),
                            Companion.REQUEST_CHECK_SETTINGS)*/
                    // For calling from fragment
                    startIntentSenderForResult(exception.resolution.intentSender, REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    //Ignore the error
                }
            } else {
                Snackbar.make(
                        binding.representativesRecyclerView,
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    requestLocation()
                }.show()
            }
        }

        task.addOnCompleteListener {
            if (it.isSuccessful) {
                startLocationUpdates()
            }
        }

    }

    private fun isLocationPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_FOREGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    requestLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(requireActivity(), "Use My Location feature is disabled due to permissions denied", Toast.LENGTH_SHORT).show()
                    showSettingsPrompt()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }

        }
    }

    private fun showSettingsPrompt() {
        Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.settings) {
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("OnActivity result called, Request Code $requestCode")
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == RESULT_OK) {
                    requestLocation()
                }
                Timber.d("Result Code: $resultCode")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                var latLng = "0.0, 0.0"
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    latLng = "${location.latitude}, ${location.longitude}"
                    Timber.d(latLng)
                }
                stopLocationUpdates()
                Timber.d("Latest Location: $latLng")
                viewModel.callGeoCodingApi(latLng)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }


    companion object {
        private const val REQUEST_CHECK_SETTINGS: Int = 100
        private const val REQUEST_CODE_FOREGROUND_LOCATION: Int = 101
    }


}