package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.ktx.Firebase
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.databinding.ChangePasswordDialogLayoutBinding
import roberto.garzone.youtubereviews.dialogs.ChangePasswordDialog

/**
 * This class manages the settings activity
 */
class SettingsActivity : AppCompatActivity(), ChangePasswordDialog.ChangePasswordDialogInterface {

    // Instance variables
    companion object {
        const val NIGHT_MODE : String = "night_mode"
    }

    private lateinit var mToolbar : Toolbar 
    private lateinit var mLayout : ConstraintLayout 
    private lateinit var mNightText : TextView 
    private lateinit var mEmail : Button 
    private lateinit var mPassword : Button 
    private lateinit var mPfImage : Button 
    private lateinit var mBack : Button 
    private lateinit var mSave : Button 
    private lateinit var mNight : ToggleButton
    private lateinit var mEmailText : EditText

    private var night : String = ""
    private var originalNight : String = ""
    private var originalEmail : String = ""
    private var newPassword : String = ""


    /**
     * This method creates the activity layout
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mToolbar = findViewById(R.id.settings_toolbar)
        mLayout = findViewById(R.id.settings_layout)
        mNightText = findViewById(R.id.settings_text_night)
        mEmail = findViewById(R.id.settings_chg_email)
        mPassword = findViewById(R.id.settings_chg_pwd)
        mPfImage = findViewById(R.id.settings_chg_pi)
        mBack = findViewById(R.id.settings_back_button)
        mSave = findViewById(R.id.settings_save)
        mNight = findViewById(R.id.settings_toggle_button)
        mEmailText = findViewById(R.id.settings_chg_email_text)

        val getIntent = intent

        originalNight = getIntent.getStringExtra("night mode").toString()
        originalEmail = getIntent.getStringExtra("oldEmail").toString()

        night = originalNight

        mBack.setOnClickListener {
            val backIntent = Intent(this@SettingsActivity, SongsListActivity::class.java)
            backIntent.putExtra("night mode", originalNight)

            startActivity(backIntent)
            finish()
        }

        mNight.setOnClickListener {
            if (mNight.isChecked) {
                night = "checked"
                mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
                mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
                mNightText.setTextColor(resources.getColor(R.color.colorWhite))
                mSave.setTextColor(resources.getColor(R.color.colorWhite))
            } else {
                night = "unchecked"
                mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
                mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
                mNightText.setTextColor(resources.getColor(R.color.colorBlack))
                mSave.setTextColor(resources.getColor(R.color.colorBlack))
            }
        }

        mPassword.setOnClickListener {
            val dialog = ChangePasswordDialog()
            dialog.show(supportFragmentManager, "Change password")
        }

        mSave.setOnClickListener {
            val preferences = getSharedPreferences("AppSettingsPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            editor.putString(NIGHT_MODE, night)
            editor.apply()

            Toast.makeText(this@SettingsActivity, resources.getString(R.string.settings_saving_complete), Toast.LENGTH_SHORT).show()

            if(changeEmail()) {
                updateFirebase()
            }

            val backIntent = Intent(this@SettingsActivity, SongsListActivity::class.java)
            backIntent.putExtra("night mode", night)

            startActivity(backIntent)
            finish()
        }
    }

    /**
     * This method defines what to do when the activity starts
     */
    override fun onStart() {
        super.onStart()
        darkMode()
    }

    /**
     * This method defines what to do when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()
        val backIntent = Intent(this@SettingsActivity, SongsListActivity::class.java)
        backIntent.putExtra("night mode", originalNight)

        startActivity(backIntent)
        finish()
    }

    private fun changeEmail() : Boolean {
        val newEmail : String = mEmailText.text.toString().trim { it <= ' ' }

        return if (newEmail.isNotEmpty()) {
            return if (originalEmail != newEmail) {
                originalEmail = newEmail
                true
            } else {
                mEmail.error = resources.getString(R.string.settings_new_email)
                false
            }
            true
        } else false
    }

    override fun onOkClicked(new: String) {
        newPassword = new
    }

    private fun updateFirebase() {
        val email = mEmailText.text.toString()
        TODO("Firebase must be updated with the new information" )
    }

    /**
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (originalNight == "checked") {
            mNight.isChecked = true
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mNightText.setTextColor(resources.getColor(R.color.colorWhite))
            mSave.setTextColor(resources.getColor(R.color.colorWhite))
        } else {
            mNight.isChecked = false
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mNightText.setTextColor(resources.getColor(R.color.colorBlack))
            mSave.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }
}