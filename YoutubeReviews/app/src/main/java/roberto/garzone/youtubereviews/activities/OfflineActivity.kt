package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import roberto.garzone.youtubereviews.R

/**
 * This class manages the offline activity
 */
class OfflineActivity : AppCompatActivity() {

    // Instance variable
    private var mAgain : Button = TODO()

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_connection_layout)

        mAgain = findViewById(R.id.check_connection_again)

        mAgain.setOnClickListener {
            startActivity(Intent(this@OfflineActivity, CheckingConnectionActivity::class.java))
            finish()
        }
    }
}