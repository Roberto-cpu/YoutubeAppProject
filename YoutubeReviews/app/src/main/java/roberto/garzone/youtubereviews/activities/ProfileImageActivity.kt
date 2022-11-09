package roberto.garzone.youtubereviews.activities

/**
 * @authors: Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date: 05/11/2022
 */

import android.content.Intent
import android.graphics.Bitmap
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import roberto.garzone.youtubereviews.BuildConfig
import roberto.garzone.youtubereviews.R
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * This class manages the activity functionalities
 */
class ProfileImageActivity : AppCompatActivity() {

    // Instance variables
    private var mToolbar : Toolbar = TODO()
    private var mLayout : ConstraintLayout = TODO()
    private var mBack : Button = TODO()
    private var mTitle : TextView = TODO()
    private var mImage : ImageView = TODO()
    private var mTake : Button = TODO()
    private var mGallery : Button = TODO()
    private var mSave : Button = TODO()

    companion object {
        private const val START_CAMERA = 0
        private const val SELECT_PICTURE = 200
    }

    private var outputUri : Uri
    private var night : String = ""
    private var email : String = ""
    private var imageFileName : String = ""
    private var photoFile : File?
    private var storage : FirebaseStorage
    private var mStorageRef : StorageReference

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

        if (getIntent != null) {
            night = getIntent.getStringExtra("night mode").toString()
            email = getIntent.getStringExtra("email").toString()
        }

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        mTake.setOnClickListener {
            val cameraIntent : Intent = Intent()
            cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            photoFile = null
            try {
                photoFile = createImageFile()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            outputUri = FileProvider.getUriForFile(this@ProfileImageActivity, "${BuildConfig.APPLICATION_ID}.provider", photoFile!!)

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == START_CAMERA && resultCode == RESULT_OK) {
            try {
                val imageBitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, outputUri)
                mImage.setImageBitmap(imageBitmap)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            outputUri = data!!.data!!
            try {
                val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, outputUri)
                mImage.setImageBitmap(bitmap)
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
        return File("${externalDir.toString()}/${imageFileName}")
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
        startActivity(backIntent)
        finish()
    }

    /**
     * This method sets the light/dark mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mTake.setTextColor(resources.getColor(R.color.colorWhite))
            mGallery.setTextColor(resources.getColor(R.color.colorWhite))
            mSave.setTextColor(resources.getColor(R.color.colorWhite))
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mTake.setTextColor(resources.getColor(R.color.colorBlack))
            mGallery.setTextColor(resources.getColor(R.color.colorBlack))
            mSave.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }
}