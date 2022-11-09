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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import roberto.garzone.youtubereviews.BuildConfig
import roberto.garzone.youtubereviews.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class manages the profile image activity functionalities
 */
class ProfileImage : AppCompatActivity() {

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
    }

    private var outputUri : Uri
    private var night : String = ""
    private var imageFileName : String = ""

    /**
     * This method creates the activity layout
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

        val getIntent : Intent = intent

        if (getIntent != null) {
            night = getIntent.getStringExtra("night mode").toString()
        }

        setSupportActionBar(this.mToolbar)
        supportActionBar!!.title = ""

        mTake.setOnClickListener {
            var cameraIntent : Intent = Intent()
            cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            var photoFile : File? = null
            try {
                photoFile = createImageFile()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            outputUri = FileProvider.getUriForFile(this@ProfileImage, "${BuildConfig.APPLICATION_ID}.provider",
            photoFile!!)

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            cameraIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(cameraIntent, ACTIVITY_START_CAMERA)
        }
    }

    override fun onStart() {
        super.onStart()
        darkMode()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_START_CAMERA && resultCode == RESULT_OK) {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, outputUri)
                mImage.setImageBitmap(imageBitmap)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * This method creates the file name to save the image on the gallery
     */
    private fun createImageFile() : File {
        imageFileName = "IMAGE_${timeStamp}.jpg"
        val externalDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File("${externalDir.toString()}/${fileName}")
    }

    private fun darkMode() {

    }
}