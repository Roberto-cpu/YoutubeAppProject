package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.dialogs.AddReviewDialog
import roberto.garzone.youtubereviews.models.Review
import roberto.garzone.youtubereviews.models.Song

/**
 * This class manages the new review activity's functionalities
 */
class NewReviewActivity : AppCompatActivity(), AddReviewDialog.AddReviewDialogInterface {

    // Instance variables
    private lateinit var mLayout : ConstraintLayout
    private lateinit var mToolbar : Toolbar
    private lateinit var mView : TextView
    private lateinit var mSave : Button
    private lateinit var mBack : Button
    private lateinit var mTitle : EditText
    private lateinit var mText : EditText
    private lateinit var mSongName : EditText
    private lateinit var mLink : EditText
    private lateinit var mBand : EditText
    private lateinit var mGenre : EditText

    private var night : String = ""
    private lateinit var auth : FirebaseAuth
    private lateinit var currUser : FirebaseUser

    /**
     * This method creates the activity layout
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_review_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.new_review_layout)
        mToolbar = findViewById(R.id.new_review_toolbar)
        mView = findViewById(R.id.new_review_view)
        mSave = findViewById(R.id.new_review_save)
        mBack = findViewById(R.id.new_review_back_button)
        mTitle = findViewById(R.id.new_review_title)
        mText = findViewById(R.id.new_review_text)
        mSongName = findViewById(R.id.new_review_song_name)
        mLink = findViewById(R.id.new_review_link)
        mBand = findViewById(R.id.new_review_band_name)
        mGenre = findViewById(R.id.new_review_genre)

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        val getIntent = intent
        night = getIntent.getStringExtra("night mode").toString()

        auth = FirebaseAuth.getInstance()
        currUser = auth.currentUser!!

        mBack.setOnClickListener {
            val backIntent = Intent(this@NewReviewActivity, SongsListActivity::class.java)
            backIntent.putExtra("night mode", night)

            startActivity(backIntent)
            finish()
        }

        mSave.setOnClickListener {
            saveReview()
        }
    }

    /**
     * This method manages the activity's behaviour when it starts
     */
    override fun onStart() {
        super.onStart()

        darkMode()
    }

    /**
     * This method saves the review into the database
     */
    private fun saveReview() {
        val revTitle : String = mTitle.text.toString()
        val revText : String = mText.text.toString()
        val songName : String = mSongName.text.toString()
        val link : String = mLink.text.toString()
        val band : String = mBand.text.toString()
        val genre : String = mGenre.text.toString()

        when {
            revTitle.isEmpty() -> mTitle.error = resources.getString(R.string.new_review_title_error)
            revText.isEmpty() -> mText.error = resources.getString(R.string.new_review_text_error)
            songName.isEmpty() -> mSongName.error = resources.getString(R.string.new_review_name_song_error)
            link.isEmpty() -> mLink.error = resources.getString(R.string.new_review_song_link_error)
            band.isEmpty() -> mBand.error = resources.getString(R.string.new_review_band_name_error)
            genre.isEmpty() -> mGenre.error = resources.getString(R.string.new_review_genre_error)
            else -> {
                val firestore = FirebaseFirestore.getInstance()
                val songCollection = firestore.collection("songs")
                val reviewCollection = firestore.collection("reviews")

                songCollection.get().addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        val songId = "${mSongName.text} - ${mBand.text}"
                        var check = false

                        for (snapshot : QueryDocumentSnapshot in it.result) {
                            if (snapshot.id == songId) {
                                check = true
                                break
                            }
                        }

                        if (!check) {
                            val song = Song(songName, link, band, genre)
                            val songInfo = HashMap<String, String>()

                            songInfo["Name"] = song.getName()
                            songInfo["Link"] = song.getYoutubeLink()
                            songInfo["Band"] = song.getBand()
                            songInfo["Genre"] = song.getGenre()

                            songCollection.document(songId).set(songInfo).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this@NewReviewActivity, resources.getString(R.string.new_review_save_song), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        val review = Review("", currUser.email.toString(), revTitle, revText, songName)
                        val reviewInfo = HashMap<String, String>()

                        reviewInfo["Title"] = review.getTitle()
                        reviewInfo["Email"] = review.getCreator()
                        reviewInfo["Text"] = review.getText()
                        reviewInfo["Song"] = review.getSongReference()

                        reviewCollection.document().set(reviewInfo).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this@NewReviewActivity, resources.getString(R.string.new_review_save_review), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                val dialog = AddReviewDialog()
                dialog.show(supportFragmentManager, "new review")
            }
        }
    }

    /**
     * This method defines what to do after click on dialog's OK button
     */
    override fun onOkClicked() {
        val intent = Intent(this@NewReviewActivity, SongsListActivity::class.java)
        intent.putExtra("night mode", night)

        startActivity(intent)
        finish()
    }

    /**
     * This method defines what to do when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        val backIntent = Intent(this@NewReviewActivity, SongsListActivity::class.java)
        backIntent.putExtra("night mode", night)

        startActivity(backIntent)
        finish()
    }

    /**
     * This method sets the dark/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mView.setTextColor(resources.getColor(R.color.colorRed))
            mView.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mTitle.setTextColor(resources.getColor(R.color.colorWhite))
            mTitle.setHintTextColor(resources.getColor(R.color.colorWhite))
            mText.setTextColor(resources.getColor(R.color.colorWhite))
            mText.setHintTextColor(resources.getColor(R.color.colorWhite))
            mSongName.setTextColor(resources.getColor(R.color.colorWhite))
            mSongName.setHintTextColor(resources.getColor(R.color.colorWhite))
            mBand.setTextColor(resources.getColor(R.color.colorWhite))
            mBand.setHintTextColor(resources.getColor(R.color.colorWhite))
            mGenre.setTextColor(resources.getColor(R.color.colorWhite))
            mGenre.setHintTextColor(resources.getColor(R.color.colorWhite))
            mLink.setTextColor(resources.getColor(R.color.colorWhite))
            mLink.setHintTextColor(resources.getColor(R.color.colorWhite))
            mSave.setTextColor(resources.getColor(R.color.colorWhite))
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mView.setTextColor(resources.getColor(R.color.colorRed))
            mView.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mTitle.setTextColor(resources.getColor(R.color.colorBlack))
            mTitle.setHintTextColor(resources.getColor(R.color.colorBlack))
            mText.setTextColor(resources.getColor(R.color.colorBlack))
            mText.setHintTextColor(resources.getColor(R.color.colorBlack))
            mSongName.setTextColor(resources.getColor(R.color.colorBlack))
            mSongName.setHintTextColor(resources.getColor(R.color.colorBlack))
            mBand.setTextColor(resources.getColor(R.color.colorBlack))
            mBand.setHintTextColor(resources.getColor(R.color.colorBlack))
            mGenre.setTextColor(resources.getColor(R.color.colorBlack))
            mGenre.setHintTextColor(resources.getColor(R.color.colorBlack))
            mLink.setTextColor(resources.getColor(R.color.colorBlack))
            mLink.setHintTextColor(resources.getColor(R.color.colorBlack))
            mSave.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }
}