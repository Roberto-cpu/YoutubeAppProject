package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * This class manages the splash screen activity
 */
class SplashScreenActivity : AppCompatActivity() {

    /**
     * This method start a timer function to change the activity
     */
    override fun onStart() {
        super.onStart()

        // The timer class manages the splash screen durability
        Timer().schedule(object : TimerTask(){
            override fun run() {
                val intent = Intent(this@SplashScreenActivity, CheckingConnectionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 900)
    }
}