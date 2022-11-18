package roberto.garzone.youtubereviews.activities

/**
 * @authors: Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date: 05/11/2022
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import roberto.garzone.youtubereviews.BuildConfig
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.models.User
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * This class manages the activity functionalities
 */
class ProfileImageActivity : AppCompatActivity() {

    // Instance variables
    private lateinit var mToolbar : Toolbar 
    private lateinit var mLayout : ConstraintLayout 
    private lateinit var mBack : Button 
    private lateinit var mTitle : TextView 
    private lateinit var mImage : ImageView 
    private lateinit var mTake : Button 
    private lateinit var mGallery : Button 
    private lateinit var mSave : Button 

    companion object {
        private const val START_CAMERA = 0
        private const val SELECT_PICTURE = 200
    }

    private lateinit var outputUri : Uri
    private var night : String = ""
    private var email : String = ""
    private var imageFileName : String = ""
    private lateinit var photoFile : File
    private lateinit var storage : FirebaseStorage
    private lateinit var mStorageRef : StorageReference
    private lateinit var user : User

    /**
     * This method defines the activity actions at its start
     */
    override fun onStart() {
        super.onStart()
        darkMode()
    }

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_image_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mToolbar = findViewById(R.id.pi_toolbar)
        mLayout = findViewById(R.id.pi_layout)
        mBack = findViewById(R.id.pi_back_btn)
        mTitle = findViewById(R.id.pi_title)
        mImage = findViewById(R.id.pi_image)
        mTake = findViewById(R.id.pi_take_photo)
        mGallery = findViewById(R.id.pi_gallery)
        mSave = findViewById(R.id.pi_upload)

        storage = FirebaseStorage.getInstance()
        mStorageRef = storage.reference

        val getIntent : Intent = intent

        night = getIntent.getStringExtra("night mode").toString()
        email = getIntent.getStringExtra("email").toString()
        user = (getIntent.getSerializableExtra("user") as? User)!!

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        mTake.setOnClickListener {
            val cameraIntent = Intent()
            cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            //photoFile = null
            try {
                photoFile = createImageFile()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            outputUri = FileProvider.getUriForFile(this@ProfileImageActivity, "${BuildConfig.APPLICATION_ID}.provider", photoFile)

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            cameraIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(cameraIntent, START_CAMERA)
        }

        mGallery.setOnClickListener {
            chooseImageFromGallery()
        }

        mSave.setOnClickListener {
            uploadImageOnStorage()
        }
    }

    /**
     * This method returns the photo
     * @param requestCode : Int
     * @param resultCode : Int
     * @param data : Intent?
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == START_CAMERA && resultCode == RESULT_OK) {
            try {
                mImage.setImageURI(outputUri)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            outputUri = data!!.data!!
            try {
                mImage.setImageURI(outputUri)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * This method creates a file name for the photo
     * @return File
     */
    private fun createImageFile() : File {
        imageFileName = "IMAGE_${email}.jpg"
        val externalDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File("${externalDir}/${imageFileName}")
    }

    /**
     * This method allows the user to upload an image from the gallery
     */
    private fun chooseImageFromGallery() {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE)
    }

    /**
     * This method uploads the image uploaded into the Image View to firebase storage
     */
    private fun uploadImageOnStorage() {
        if (outputUri != null) {
            val reference : StorageReference = mStorageRef.child("images/${email}/${UUID.randomUUID()}")
            reference.putFile(outputUri).addOnSuccessListener {
                Toast.makeText(this@ProfileImageActivity, R.string.profile_image_upload_success, Toast.LENGTH_LONG).show()

                val backIntent = Intent(this@ProfileImageActivity, SignInActivity::class.java)
                backIntent.putExtra("image uri", outputUri.toString())
                backIntent.putExtra("night mode", night)
                backIntent.putExtra("user", user)
                startActivity(backIntent)
                finish()
            }
        }
    }

    /**
     * This method defines the activity behavior when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        val backIntent = Intent(this@ProfileImageActivity, SignInActivity::class.java)
        backIntent.putExtra("image uri", "")
        backIntent.putExtra("night mode", night)
        backIntent.putExtra("user", user)
        startActivity(backIntent)
        finish()
    }

    /**
     * This method sets the light/dark mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mTake.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mGallery.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
            mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mTake.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mGallery.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mSave.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }
}