@file:Suppress("DEPRECATION")

package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.dialogs.LoginGuestDialog
import roberto.garzone.youtubereviews.models.User

/**
 * This class manages the login activity behaviour
 */
class LoginActivity : AppCompatActivity(), LoginGuestDialog.LoginGuestDialogInterface {

    // Instance variables
    private lateinit var mLayout : ConstraintLayout 
    private lateinit var mTitle : TextView 
    private lateinit var mEmail : EditText 
    private lateinit var mPassword : EditText 
    private lateinit var mLogin : Button 
    private lateinit var mSignin : Button 
    private lateinit var mGuest : Button 
    private lateinit var mToolbar : Toolbar 
    private lateinit var mRememberMe : CheckBox 
    private lateinit var mNightMode : CheckBox 
    private lateinit var mLoginText : TextView 
    private lateinit var mSignInText : TextView 

    private var night : String = ""
    private lateinit var auth : FirebaseAuth

    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.login_layout)
        mTitle = findViewById(R.id.login_title)
        mEmail = findViewById(R.id.login_email)
        mPassword = findViewById(R.id.login_password)
        mLogin = findViewById(R.id.login_confirm_button)
        mSignin = findViewById(R.id.login_sign_in_button)
        mGuest = findViewById(R.id.login_guest_button)
        mToolbar = findViewById(R.id.login_toolbar)
        mRememberMe = findViewById(R.id.remember_me_btn)
        mNightMode = findViewById(R.id.night_mode_btn)
        mLoginText = findViewById(R.id.login_as_text)
        mSignInText = findViewById(R.id.sign_in_text)

        // Initialize Shared Preferences
        preferences = getSharedPreferences("LoginPreferences", MODE_PRIVATE)
        editor = preferences.edit()

        val email : String = preferences.getString("email", "No Email Stored")!!
        val password : String = preferences.getString("password", "")!!

        if (email != "No Email Stored" && password != "") {
            mEmail.setText(email)
            mPassword.setText(password)
        }

        mEmail.setOnClickListener {
            mEmail.hint = ""
        }

        mPassword.setOnClickListener {
            mPassword.hint = ""
        }

        mLogin.setOnClickListener {
            loginClicked()
        }

        mSignin.setOnClickListener {
            signInClicked()
        }

        mGuest.setOnClickListener {
            loginAsGuest()
        }

        mNightMode.setOnClickListener {
            if (mNightMode.isChecked) setPageStyle("checked")
            else setPageStyle("unchecked")
        }

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        auth = FirebaseAuth.getInstance()
    }

    /**
     * This method defines the activity's behaviour at its start
     */
    override fun onStart() {
        super.onStart()
        getSharedPreferences()
        setPageStyle()
    }

    /**
     * This method sends to the sign in activity
     */
    private fun signInClicked() {
        val signInIntent = Intent(this@LoginActivity, SignInActivity::class.java)
        signInIntent.putExtra("night mode", night)
        signInIntent.putExtra("image uri", "")
        signInIntent.putExtra("user", User("", "", ""))

        startActivity(signInIntent)
        finish()
    }

    /**
     * This method logs a user into the app
     */
    private fun loginClicked() {
        val email : String = mEmail.text.toString()
        val pwd : String = mPassword.text.toString()

        when {
            email.isEmpty() -> mEmail.error = resources.getString(R.string.email_error)
            pwd.isEmpty() -> mPassword.error = resources.getString(R.string.password_error)
            else -> {
                auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener {
                    if (it.isSuccessful) {

                        if (mRememberMe.isChecked) {
                            editor.putString("email", mEmail.text.toString())
                            editor.putString("password", mPassword.text.toString())
                        } else {
                            editor.putString("email", "No Email Stored")
                            editor.putString("password", "")
                        }

                        val loginIntent = Intent(this@LoginActivity, SongsListActivity::class.java)
                        loginIntent.putExtra("night mode", night)

                        startActivity(loginIntent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, resources.getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                        mEmail.setText("")
                        mPassword.setText("")
                    }
                }
            }
        }
    }

    /**
     * This method logs the user as a guest
     */
    private fun loginAsGuest() {
        val dialog = LoginGuestDialog()
        dialog.show(supportFragmentManager, "guest dialog")
    }

    /**
     * This method defines the activity behaviour when the user click the OK dialog's button
     */
    override fun onYesClicked() {
        auth.signInAnonymously().addOnCompleteListener {
            if(it.isSuccessful) {
                val guestIntent = Intent(this@LoginActivity, SongsListActivity::class.java)
                guestIntent.putExtra("night mode", night)

                startActivity(guestIntent)
                finish()
            }
        }
    }

    /**
     * This method reads the shared preferences information
     */
    private fun getSharedPreferences() {
        val preferences : SharedPreferences = getSharedPreferences("AppSettingsPreferences", Context.MODE_PRIVATE)
        night = preferences.getString("night_mode", "checked").toString()
    }

    /**
     * This method sets the page style on the base of shared preferences saved information
     */
    private fun setPageStyle() {
        if (night == "checked") {
            mNightMode.isChecked = true
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_dark_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mLoginText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mLogin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSignin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mGuest.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSignInText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mRememberMe.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mNightMode.isChecked = false
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_light_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mLogin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSignin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mGuest.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mLoginText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSignInText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mRememberMe.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }

    /**
     * This method sets the page style on the base of toolbar's checkbox saved information
     */
    private fun setPageStyle(night_mode : String) {
        night = night_mode
        if (night_mode == "checked") {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_dark_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mLoginText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mLogin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSignin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mGuest.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSignInText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mRememberMe.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_light_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mLogin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSignin.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mGuest.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mLoginText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSignInText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mRememberMe.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }
}
