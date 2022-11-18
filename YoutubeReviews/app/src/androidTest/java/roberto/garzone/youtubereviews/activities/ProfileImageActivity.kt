package roberto.garzone.youtubereviews.activities

/**
 * @authors: Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date: 01/10/2022
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
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import roberto.garzone.youtubereviews.BuildConfig
import roberto.garzone.youtubereviews.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class manages the profile image activity functionalities
 */
class ProfileImageActivity : AppCompatActivity() {

    // Instance variables
    private var mToolbar : Toolbar = TODO()
    private var mBack : Button = TODO()
    private var mLayout : ConstraintLayout = TODO()
    private var mTitle : TextView = TODO()
    private var mImage : ImageView = TODO()
    private var mTake : Button = TODO()
    private var mGallery : Button = TODO()
    private var mSave : Button = TODO()

    companion object {
        private const val ACTIVITY_START_CAMERA : Int = 0
        private const val ACTIVITY_GALLERY : Int = 100
    }

    private var outputUri : Uri
    private var night : String = ""
    private var imageFileName : String = ""
    private var email : String = ""
    private var storage : FirebaseStorage
    private var storageRef : StorageReference

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
        storageRef = storage.reference

        val getIntent : Intent = intent

        if (getIntent != null) {
            night = getIntent.getStringExtra("night mode").toString()
            email = getIntent.getStringExtra("email").toString()
        }

        setSupportActionBar(this.mToolbar)
        supportActionBar!!.title = ""

        mTake.setOnClickListener {
            val cameraIntent = Intent()
            cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            var photoFile : File? = null
            try {
                photoFile = createImageFile()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            outputUri = FileProvider.getUriForFile(this@ProfileImageActivity, "${BuildConfig.APPLICATION_ID}.provider",
            photoFile!!)

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            cameraIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(cameraIntent, ACTIVITY_START_CAMERA)
        }

        mGallery.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, resources.getString(R.string.profile_image_gallery_text)), ACTIVITY_GALLERY)
        }

        mSave.setOnClickListener {
            uploadImage()
        }
    }

    /**
     * This method defines what the activity must doing at the start
     */
    override fun onStart() {
        super.onStart()
        darkMode()
    }

    /**
     * This method returns the result of an actvity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_START_CAMERA && resultCode == RESULT_OK) {
            try {
                outputUri = data?.data!!
                mImage.setImageURI(outputUri)
                galleryAddPic()
            } catch (e : IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == ACTIVITY_GALLERY && resultCode == RESULT_OK) {
            outputUri = data?.data!!
            mImage.setImageURI(outputUri)
        }
    }

    /**
     * This method creates the file name to save the image on the gallery
     * @return File
     */
    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageFileName = "IMAGE_${timeStamp}.jpg"
        val externalDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File("${externalDir}/${imageFileName}")
    }

    /**
     * This method saves the image taken to the gallery
     */
    private fun galleryAddPic() {
        val mediaScanIntent : Intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = outputUri
        sendBroadcast(mediaScanIntent)
    }

    /**
     * This method uploads the image to firebase storage
     */
    private fun uploadImage() {
        if (outputUri != null) {
            val ref = storageRef.child("images/$email/${UUID.randomUUID()}").putFile(outputUri)
            ref.addOnSuccessListener {
                    Toast.makeText(this@ProfileImageActivity, R.string.profile_image_upload_success, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@ProfileImageActivity, R.string.profile_image_upload_failed, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@ProfileImageActivity, R.string.profile_image_image_miss, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This method sets the black/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mTitle.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mTake.setTextColor(resources.getColor(R.color.colorWhite))
            mGallery.setTextColor(resources.getColor(R.color.colorWhite))
            mSave.setTextColor(resources.getColor(R.color.colorWhite))
        } else {
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mTitle.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mTake.setTextColor(resources.getColor(R.color.colorBlack))
            mGallery.setTextColor(resources.getColor(R.color.colorBlack))
            mSave.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }
}