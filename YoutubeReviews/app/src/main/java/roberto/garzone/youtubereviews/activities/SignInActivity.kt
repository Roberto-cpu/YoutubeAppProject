package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.dialogs.ProfileImageEmailDialog
import roberto.garzone.youtubereviews.models.User

/**
 * This class manages the sign in activity
 */
class SignInActivity : AppCompatActivity(), ProfileImageEmailDialog.EmailDialogInterface {

    // Instance variables
    private var mLayout : ConstraintLayout = TODO()
    private var mTitle : TextView = TODO()
    private var mImage : ImageView = TODO()
    private var mEmail : EditText = TODO()
    private var mPassword : EditText = TODO()
    private var mConfPassword : EditText = TODO()
    private var mPasswordBtn : CheckBox = TODO()
    private var mConfPasswordBtn : CheckBox = TODO()
    private var mSignIn : Button = TODO()
    private var mDelete : Button = TODO()
    private var mToolbar : Toolbar = TODO()
    private var mUsername : EditText = TODO()

    private var night : String = ""
    private var imageUri : Uri

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.sign_in_layout)
        mToolbar = findViewById(R.id.sign_in_toolbar)
        mTitle = findViewById(R.id.sign_in_title)
        mImage = findViewById(R.id.sign_in_profile_image)
        mEmail = findViewById(R.id.sign_in_email)
        mPassword = findViewById(R.id.sign_in_password)
        mConfPassword = findViewById(R.id.sign_in_confirm_password)
        mPasswordBtn = findViewById(R.id.sign_in_checkbox_password)
        mConfPasswordBtn = findViewById(R.id.sign_in_checkbox_confirm_password)
        mSignIn = findViewById(R.id.sign_in_confirm_button)
        mDelete = findViewById(R.id.sign_in_delete_button)
        mUsername = findViewById(R.id.sign_in_user)

        val getIntent = intent

        if (getIntent != null) {
            // TODO("From login activity must be sent a null user instance")
            night = getIntent.getStringExtra("night mode").toString()

            if (getIntent.getStringExtra("image uri") != "") {
                imageUri = Uri.parse(getIntent.getStringExtra("image uri").toString())
            }
        }

        val imageBitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        if (imageBitmap != null) mImage.setImageBitmap(imageBitmap)

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        mImage.setOnClickListener {
            // TODO("User instance must be implemented and exchanged from pages with intents")
            if (mEmail.text.toString().isEmpty()) {
                val dialog = ProfileImageEmailDialog()
                dialog.show(supportFragmentManager, "email dialog")
            } else {
                val piIntent = Intent(this@SignInActivity, ProfileImageActivity::class.java)
                piIntent.putExtra("email", mEmail.text.toString())
                startActivity(piIntent)
                finish()
            }
        }

        mPasswordBtn.setOnClickListener {
            showHidePassword(mPasswordBtn, mPassword)
        }

        mConfPasswordBtn.setOnClickListener {
            showHidePassword(mConfPasswordBtn, mConfPassword)
        }

        mSignIn.setOnClickListener {
            signIn()
        }

        mDelete.setOnClickListener {
            startActivity(Intent(this@SignInActivity, LoginActivity::class.java))
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
     * This method shows or hides password
     */
    private fun showHidePassword(checkbox : CheckBox, text : EditText) {
        if (checkbox.isChecked) {
            text.transformationMethod = HideReturnsTransformationMethod.getInstance()
            checkbox.text = resources.getString(R.string.check_box_hide_password)
        } else {
            text.transformationMethod = PasswordTransformationMethod.getInstance()
            checkbox.text = resources.getString(R.string.check_box_show_password)
        }
    }

    /**
     * This method save a user into firebase firestore
     */
    private fun signIn() {
        val username : String = mUsername.text.toString()
        val email : String = mEmail.text.toString()
        val pwd : String = mPassword.text.toString()
        val confPwd : String = mConfPassword.text.toString()

        val emailRegex = "^(.+)@(.+)$"
        val pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$"

        when {
            email.isEmpty() -> mEmail.error = resources.getString(R.string.email_error)
            !email.matches(Regex(emailRegex)) -> mEmail.error = resources.getString(R.string.sign_in_email_format_error)
            pwd.isEmpty() -> mPassword.error = resources.getString(R.string.password_error)
            !pwd.matches(Regex(pwdRegex)) -> mPassword.error = resources.getString(R.string.sign_in_password_format_error)
            pwd != confPwd -> {
                Toast.makeText(this@SignInActivity, resources.getString(R.string.sign_in_password_different_to_confirm_password), Toast.LENGTH_SHORT).show()
                mPassword.setText("")
                mConfPassword.setText("")
            }
            username.isEmpty() -> mUsername.error = resources.getString(R.string.sign_in_username_empty)
            else -> {
                val firestore = FirebaseFirestore.getInstance()
                val user = User(username, email, pwd)
                val userInfo = HashMap<String, String>()

                userInfo["Username"] = user.getUsername()
                userInfo["Email"] = user.getEmail()
                userInfo["Password"] = user.getPassword()

                firestore.collection("users").document(user.getEmail()).set(userInfo).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this@SignInActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignInActivity, resources.getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * This method defines what to do when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this@SignInActivity, LoginActivity::class.java))
        finish()
    }

    override fun onContinueClicked(email: String) {
        val piIntent = Intent(this@SignInActivity, ProfileImageActivity::class.java)
        piIntent.putExtra("email", email)
        startActivity(piIntent)
        finish()
    }

    /**
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mTitle.setTextColor(resources.getColor(R.color.colorRed))
            mTitle.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mEmail.setTextColor(resources.getColor(R.color.colorWhite))
            mEmail.setHintTextColor(resources.getColor(R.color.colorWhite))
            mPassword.setTextColor(resources.getColor(R.color.colorWhite))
            mPassword.setHintTextColor(resources.getColor(R.color.colorWhite))
            mConfPassword.setTextColor(resources.getColor(R.color.colorWhite))
            mConfPassword.setHintTextColor(resources.getColor(R.color.colorWhite))
            mPasswordBtn.setTextColor(resources.getColor(R.color.colorWhite))
            mPasswordBtn.setButtonDrawable(R.color.colorWhite)
            mConfPasswordBtn.setTextColor(resources.getColor(R.color.colorWhite))
            mConfPasswordBtn.setButtonDrawable(R.color.colorWhite)
            mSignIn.setTextColor(resources.getColor(R.color.colorWhite))
            mDelete.setTextColor(resources.getColor(R.color.colorWhite))
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mTitle.setTextColor(resources.getColor(R.color.colorRed))
            mTitle.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mEmail.setTextColor(resources.getColor(R.color.colorBlack))
            mEmail.setHintTextColor(resources.getColor(R.color.colorBlack))
            mPassword.setTextColor(resources.getColor(R.color.colorBlack))
            mPassword.setHintTextColor(resources.getColor(R.color.colorBlack))
            mConfPassword.setTextColor(resources.getColor(R.color.colorBlack))
            mConfPassword.setHintTextColor(resources.getColor(R.color.colorBlack))
            mPasswordBtn.setTextColor(resources.getColor(R.color.colorBlack))
            mPasswordBtn.setButtonDrawable(R.color.colorBlack)
            mConfPasswordBtn.setTextColor(resources.getColor(R.color.colorBlack))
            mConfPasswordBtn.setButtonDrawable(R.color.colorBlack)
            mSignIn.setTextColor(resources.getColor(R.color.colorBlack))
            mDelete.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }
}