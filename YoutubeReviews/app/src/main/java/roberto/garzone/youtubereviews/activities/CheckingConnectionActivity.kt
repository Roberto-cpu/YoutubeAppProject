package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.receivers.InternetConnectionReceiver
import java.util.*

/**
 * This class manages the check of the connection activity
 */
class CheckingConnectionActivity : AppCompatActivity(), InternetConnectionReceiver.InternetConnectionReceiverInterface {

    // Instance variables
    private var mNoConnection : TextView = TODO()
    private var mConnectBtn : Button = TODO()
    private var mConnImage : ImageView = TODO()

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checking_connection_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mNoConnection = findViewById(R.id.no_connection_text)
        mConnectBtn = findViewById(R.id.check_again_btn)
        mConnImage = findViewById(R.id.connection_image)

        mConnectBtn.setOnClickListener {
            checkingConnection()
        }
    }

    /**
     * This method starts an action when the activity starts
     */
    override fun onStart() {
        super.onStart()

        mNoConnection.visibility = View.INVISIBLE
        mConnectBtn.visibility = View.INVISIBLE
        mConnectBtn.isEnabled = false

        checkingConnection()
    }

    /**
     * This method starts the broadcast to verify of internet connection
     */
    private fun checkingConnection() {
        var isConnected : Boolean = InternetConnectionReceiver.isConnected(this)
        connectionResult(isConnected)
    }

    /**
     * This method defines what to do when the broadcast's result arrive
     * @param isConnected : Boolean
     */
    private fun connectionResult(isConnected : Boolean) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isConnected) {
                    startActivity(Intent(this@CheckingConnectionActivity, LoginActivity::class.java))
                    finish()
                } else {
                    mNoConnection.visibility = View.VISIBLE
                    mConnectBtn.visibility = View.VISIBLE
                    mConnectBtn.isEnabled = true
                    mConnImage.setImageResource(R.mipmap.no_wifi)
                }
            }
        }, 1000)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        connectionResult(isConnected)
    }

}