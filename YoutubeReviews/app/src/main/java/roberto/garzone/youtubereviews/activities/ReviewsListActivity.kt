package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.adapters.ReviewsListAdapter
import roberto.garzone.youtubereviews.models.Review
import roberto.garzone.youtubereviews.models.Song

/**
 * This class manages the reviews list activity functionalities
 */
class ReviewsListActivity : AppCompatActivity() {

    // Instance variables
    private lateinit var mLayout : ConstraintLayout
    private lateinit var mToolbar : Toolbar
    private lateinit var mBack : Button
    private lateinit var mName : TextView
    private lateinit var mList : ListView
    private lateinit var mMenu : Menu
    private lateinit var mRating : RatingBar
    private lateinit var mRatingText : TextView

    private lateinit var review : Review
    private var name : String = ""
    private var night : String = ""
    private lateinit var song : Song
    private lateinit var adapter : ReviewsListAdapter
    private lateinit var songs : ArrayList<Song>

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reviews_list_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.reviews_list_layout)
        mToolbar = findViewById(R.id.reviews_list_toolbar)
        mBack = findViewById(R.id.reviews_list_back_button)
        mName = findViewById(R.id.reviews_list_song_name)
        mRating = findViewById(R.id.reviews_list_rating)
        mRatingText = findViewById(R.id.reviews_list_rating_text)

        val getIntent : Intent = intent

        setSupportActionBar(this.mToolbar)
        supportActionBar!!.title = ""

        name = getIntent.getStringExtra("song").toString()
        songs = getIntent.getSerializableExtra("songs") as ArrayList<Song>
        night = getIntent.getStringExtra("night mode").toString()
    }

    /**
     * This method defines what to do when the activity starts
     */
    override fun onStart() {
        super.onStart()

        darkMode()
        mRating.isEnabled = false
        readSongFromFirestore()
    }

    /**
     * This method reads the song information from firebase firestore
     */
    private fun readSongFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val docReference = firestore.collection("songs").document(name)

        docReference.get().addOnSuccessListener {
            song = Song(it.get("Name").toString(), it.get("Link").toString(), it.get("Band").toString(), it.get("Genre").toString())
            mName.text = song.getName()

            val firestore1 = FirebaseFirestore.getInstance()

            firestore1.collection("reviews").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    var sum = 0
                    var count = 0
                    for (sn : QueryDocumentSnapshot in it.result) {
                        val review = Review(sn.id, sn.get("Email").toString(), sn.get("Title").toString(), sn.get("Text").toString(), sn.get("Song").toString(), sn.get("Rating").toString())

                        if (name == review.getSongReference()) {
                            song.addReview(review)

                            sum += review.getRating().toInt()
                            count++
                            ratingAverage(sum, count)
                        }
                    }

                    mList = findViewById(R.id.reviews_list)
                    adapter = ReviewsListAdapter(this@ReviewsListActivity, song.getReviews())

                    if (night == "checked") {
                        mList.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
                    }

                    mList.adapter = adapter
                    mList.setOnItemClickListener { _, _, i, _ ->
                        this.review = song.getReviews()[i]
                        val reviewIntent = Intent(this@ReviewsListActivity, ReviewActivity::class.java)

                        reviewIntent.putExtra("song name", song.getName())
                        reviewIntent.putExtra("review", this.review)
                        reviewIntent.putExtra("songs", songs)
                        reviewIntent.putExtra("night mode", night)

                        startActivity(reviewIntent)
                        finish()
                    }
                }
            }
        }
    }

    /**
     * This method creates a menu
     * @param menu : Menu
     * @return Boolean
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            mMenu = menu
        }

        menuInflater.inflate(R.menu.reviews_list_option_menu, mMenu)
        return true
    }

    /**
     * This method defines what to do if a menu item is selected
     * @param item : MenuItem
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reviews_list_item_yt -> {

                val youtubeIntent = Intent(this@ReviewsListActivity, YoutubeActivity::class.java)

                youtubeIntent.putExtra("song", name)
                youtubeIntent.putExtra("songs", songs)
                youtubeIntent.putExtra("night mode", night)

                startActivity(youtubeIntent)
                finish()
                true
            }
            R.id.reviews_list_item_graph -> {

                val graphIntent = Intent(this@ReviewsListActivity, FeedbackGraphActivity::class.java)

                graphIntent.putExtra("song", song)
                graphIntent.putExtra("songs", songs)
                graphIntent.putExtra("night mode", night)

                startActivity(graphIntent)
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * This method defines what to do when the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        val backIntent = Intent(this@ReviewsListActivity, SongsListActivity::class.java)
        backIntent.putExtra("night mode", night)

        startActivity(backIntent)
        finish()
    }

    /**
     * This method calculates the rating's average and sets the rating bar value
     * @param sum : Int
     * @param count : Int
     */
    private fun ratingAverage(sum : Int, count : Int) {
        val avg : Int = sum / count

        mRatingText.text = avg.toString()
        mRating.rating = avg.toFloat()
    }

    /**
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
            mName.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mName.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_dark_mode, null)
            mList.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_dark_mode, null)
            mRatingText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        } else {
            mToolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGray, null))
            mLayout.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCoolMint, null))
            mName.setTextColor(ResourcesCompat.getColor(resources, R.color.colorRed, null))
            mName.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_light_mode, null)
            mList.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_border_light_mode, null)
            mRatingText.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
        }
    }
}