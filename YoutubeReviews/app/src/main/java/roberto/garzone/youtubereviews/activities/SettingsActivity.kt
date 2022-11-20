package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import roberto.garzone.youtubereviews.models.User
import roberto.garzone.youtubereviews.R
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
    private var newPasswordClicked = false
    private var newPassword : String = ""
    private lateinit var user : User
    private lateinit var auth : FirebaseAuth
    private lateinit var currUser : FirebaseUser

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
        user = getIntent.getSerializableExtra("user") as User

        auth = FirebaseAuth.getInstance()
        currUser = auth.currentUser!!

        night = originalNight
        originalEmail = user.getEmail()

        mBack.setOnClickListener {
            val backIntent = Intent(this@SettingsActivity, SongsListActivity::class.java)
            backIntent.putExtra("night mode", originalNight)

            startActivity(backIntent)
            finish()
        }

        if(currUser.isAnonymous) {
            mEmail.visibility = View.INVISIBLE
            mEmailText.visibility = View.INVISIBLE
            mEmailText.isEnabled = false
            mPassword.visibility = View.INVISIBLE
            mPassword.isEnabled = false
            mPfImage.visibility = View.INVISIBLE
            mPfImage.isEnabled = false
        }

        mEmailText.hint = originalEmail

        mNight.setOnClickListener {
            if (mNight.isChecked) {
                night = "checked"
                mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
                mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
                mNightText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
                mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
                mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
                mEmailText.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
                mEmailText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            } else {
                night = "unchecked"
                mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
                mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
                mNightText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
                mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
                mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
                mEmailText.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
                mEmailText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            }
        }

        mPassword.setOnClickListener {
            newPasswordClicked = true

            val dialog = ChangePasswordDialog()
            dialog.show(supportFragmentManager, "Change password")
        }

        mPfImage.setOnClickListener {
            val pfIntent = Intent(this@SettingsActivity, ProfileImageActivity::class.java)
            pfIntent.putExtra("activity", "settings")
            pfIntent.putExtra("night mode", night)
            pfIntent.putExtra("user", user)

            startActivity(pfIntent)
            finish()
        }

        mSave.setOnClickListener {
            val preferences = getSharedPreferences("AppSettingsPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            editor.putString(NIGHT_MODE, night)
            editor.apply()

            Toast.makeText(this@SettingsActivity, resources.getString(R.string.settings_saving_complete), Toast.LENGTH_SHORT).show()

            if(changeEmail())
                updateFirebase()

            val backIntent = Intent(this@SettingsActivity, SongsListActivity::class.java)
            backIntent.putExtra("night mode", night)
            backIntent.putExtra("user", user)

            Toast.makeText(this@SettingsActivity, R.string.settings_save, Toast.LENGTH_SHORT).show()

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
        backIntent.putExtra("user", user)

        startActivity(backIntent)
        finish()
    }

    /**
     * This method checks if the email is correct
     * @return Boolean
     */
    private fun changeEmail() : Boolean {
        val emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
        val newEmail = mEmailText.text.toString()

        return when {
            newEmail.isEmpty() -> true
            newEmail == user.getEmail() -> {
                mEmailText.error = resources.getString(R.string.settings_new_email)
                false
            }
            !newEmail.matches(Regex(emailRegex)) -> {
                mEmailText.error = resources.getString(R.string.sign_in_email_format_error)
                false
            }
            else -> {
                user.setEmail(newEmail)
                true
            }
        }
    }

    /**
     * This method defines what the activity must do when the user clicks ok button on dialog
     * @param new : String
     */
    override fun onOkClicked(new: String) {
        newPassword = new
    }

    /**
     * This method defines what the activity must do when the user clicks delete dialog's button
     */
    override fun onDeleteClicked() {
        newPasswordClicked = false
    }

    private fun updateFirebase() {
        val email = user.getEmail()
        val firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.collection("users").document(user.getUsername())

        if (user.getEmail() != originalEmail) {
            documentRef.update("Email", email)
            user.setEmail(email)
            currUser.updateEmail(user.getEmail())
        }

        if (newPasswordClicked) {
            documentRef.update("Password", newPassword)
            user.setPassword(newPassword)
            currUser.updatePassword(user.getPassword())
        }
    }

    /**
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (originalNight == "checked") {
            mNight.isChecked = true
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mNightText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mEmailText.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mEmailText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mNight.isChecked = false
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mNightText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmailText.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmailText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }
}