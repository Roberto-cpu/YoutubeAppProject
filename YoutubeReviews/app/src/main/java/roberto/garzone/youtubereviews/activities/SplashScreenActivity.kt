package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import roberto.garzone.youtubereviews.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class manages the splash screen activity
 */
class SplashScreenActivity : AppCompatActivity() {

    // Instance variables
    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isCameraPermissionGranted = false


    /**
     * This method start a timer function to change the activity
     */
    override fun onStart() {
        super.onStart()

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
            isCameraPermissionGranted = permissions[android.Manifest.permission.CAMERA] ?: isCameraPermissionGranted
        }

        val granted = requestPermission()

        if (granted) {
            // The timer class manages the splash screen durability
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val intent = Intent(this@SplashScreenActivity, CheckingConnectionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 900)
        } else {
            Toast.makeText(this@SplashScreenActivity, R.string.permission_request, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() : Boolean {
        var granted = true

        isReadPermissionGranted = ContextCompat.checkSelfPermission(this@SplashScreenActivity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(this@SplashScreenActivity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        isCameraPermissionGranted = ContextCompat.checkSelfPermission(this@SplashScreenActivity,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()

        if (!isReadPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            granted = false
        }

        if (!isWritePermissionGranted) {
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            granted = false
        }

        if (!isCameraPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.CAMERA)
            granted = false
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

        return granted
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
}