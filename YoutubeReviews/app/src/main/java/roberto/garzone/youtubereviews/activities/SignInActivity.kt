package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.dialogs.ProfileImageEmailDialog
import roberto.garzone.youtubereviews.models.User
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * This class manages the sign in activity
 */
class SignInActivity : AppCompatActivity(), ProfileImageEmailDialog.EmailDialogInterface {

    // Instance variables
    private lateinit var mLayout : ConstraintLayout
    private lateinit var mTitle : TextView
    private lateinit var mImage : ImageView
    private lateinit var mEmail : EditText
    private lateinit var mPassword : EditText
    private lateinit var mConfPassword : EditText
    private lateinit var mPasswordBtn : CheckBox
    private lateinit var mConfPasswordBtn : CheckBox
    private lateinit var mSignIn : Button
    private lateinit var mDelete : Button
    private lateinit var mToolbar : Toolbar
    private lateinit var mUsername : EditText

    private var night : String = ""
    private var imageUri : Uri? = null
    private lateinit var user : User
    private lateinit var firebaseAuth : FirebaseAuth

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

        firebaseAuth = FirebaseAuth.getInstance()

        if (getIntent != null) {
            night = getIntent.getStringExtra("night mode").toString()
            user = getIntent.getSerializableExtra("user") as User

            if (getIntent.getStringExtra("image uri") != "") {
                imageUri = Uri.parse(getIntent.getStringExtra("image uri").toString())
            }
        }

        if (user.getUsername() != "") mUsername.setText(user.getUsername())

        if (user.getEmail() != "") mEmail.setText(user.getEmail())

        if (imageUri != null) mImage.setImageURI(imageUri)
        else mImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.generic_profile_image, null))

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        mImage.setOnClickListener {
            user = if (mEmail.text.isEmpty() && mUsername.text.isEmpty()) {
                User("", "", "")
            } else if (mEmail.text.isEmpty() && mUsername.text.isNotEmpty()) {
                User(mUsername.text.toString(), "", "")
            } else if (mEmail.text.isNotEmpty() && mUsername.text.isEmpty()) {
                User("", mEmail.text.toString(), "")
            } else {
                User(mUsername.text.toString(), mEmail.text.toString(), "")
            }

            if (mEmail.text.toString().isEmpty()) {
                val dialog = ProfileImageEmailDialog()
                dialog.show(supportFragmentManager, "email dialog")
            } else {
                val piIntent = Intent(this@SignInActivity, ProfileImageActivity::class.java)
                piIntent.putExtra("night mode", night)
                piIntent.putExtra("email", mEmail.text.toString())
                piIntent.putExtra("user", user)
                piIntent.putExtra("activity", "signIn")
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

        val emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
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
                userInfo["Password"] = md5Hashing(user.getPassword())

                firestore.collection("users").document(user.getEmail()).set(userInfo).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                            .addOnSuccessListener {
                                startActivity(Intent(this@SignInActivity, LoginActivity::class.java))
                                finish()
                            }
                    } else {
                        Toast.makeText(this@SignInActivity, resources.getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * This method encrypts a string with md5 hashing algorithm
     */
    private fun md5Hashing(str: String): String {
        var md5: MessageDigest? = null

        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        md5!!.update(str.toByteArray(), 0, str.length)
        return BigInteger(1, md5.digest()).toString(16)
    }

    /**
     * This method defines what to do when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()
        val backIntent = Intent(this@SignInActivity, LoginActivity::class.java)
        backIntent.putExtra("night mode", night)
        startActivity(backIntent)
        finish()
    }

    /**
     * This method defines what to do when the ok button on the dialog is pressed
     * @param email : String
     */
    override fun onContinueClicked(email: String) {
        val piIntent = Intent(this@SignInActivity, ProfileImageActivity::class.java)
        piIntent.putExtra("email", email)
        piIntent.putExtra("night mode", night)
        piIntent.putExtra("user", user)
        piIntent.putExtra("activity", "signIn")
        startActivity(piIntent)
        finish()
    }

    /**
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_dark_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mConfPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mConfPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPasswordBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mPasswordBtn.setButtonDrawable(R.color.colorWhite)
            mConfPasswordBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mConfPasswordBtn.setButtonDrawable(R.color.colorWhite)
            mSignIn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mDelete.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mTitle.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_light_mode, null)
            mEmail.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mEmail.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mConfPassword.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mConfPassword.setHintTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPasswordBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mPasswordBtn.setButtonDrawable(R.color.colorBlack)
            mConfPasswordBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mConfPasswordBtn.setButtonDrawable(R.color.colorBlack)
            mSignIn.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mDelete.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }
}