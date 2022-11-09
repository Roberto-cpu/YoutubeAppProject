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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var mLayout : ConstraintLayout = TODO()
    private var mToolbar : Toolbar = TODO()
    private var mBack : Button = TODO()
    private var mName : TextView = TODO()
    private var mList : ListView = TODO()
    private var mMenu : Menu = TODO()

    private var review : Review? = null
    private var name : String = ""
    private var night : String = ""
    private var song : Song? = null
    private var adapter : ReviewsListAdapter
    private var songs : ArrayList<Song>

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

        val getIntent : Intent = intent

        setSupportActionBar(this.mToolbar)
        supportActionBar!!.title = ""

        if (getIntent != null) {
            name = getIntent.getStringExtra("song").toString()
            songs = getIntent.getSerializableExtra("songs") as ArrayList<Song>
            night = getIntent.getStringExtra("night mode").toString()
        }
    }

    /**
     * This method defines what to do when the activity starts
     */
    override fun onStart() {
        super.onStart()

        darkMode()
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
            mName.text = song!!.getName()

            val firestore1 = FirebaseFirestore.getInstance()

            firestore1.collection("reviews").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (sn : QueryDocumentSnapshot in it.result) {
                        val review = Review(sn.id, sn.get("Email").toString(), sn.get("Title").toString(), sn.get("Text").toString(), sn.get("Song").toString())

                        if (name == review.getSongReference()) {
                            song!!.addReview(review)
                        }
                    }

                    mList = findViewById(R.id.reviews_list)
                    adapter = ReviewsListAdapter(this@ReviewsListActivity, song!!.getReviews())

                    if (night == "checked") {
                        mList.setBackgroundColor(resources.getColor(R.color.colorWhite))
                    }

                    mList.adapter = adapter
                    mList.setOnItemClickListener { _, _, i, _ ->
                        this.review = song!!.getReviews()[i]
                        val reviewIntent = Intent(this@ReviewsListActivity, ReviewActivity::class.java)

                        reviewIntent.putExtra("song name", song!!.getName())
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
        if (item.itemId == R.id.reviews_list_item) {

            val youtubeIntent = Intent(this@ReviewsListActivity, YoutubeActivity::class.java)

            youtubeIntent.putExtra("song", name)
            youtubeIntent.putExtra("songs", songs)
            youtubeIntent.putExtra("night mode", night)

            startActivity(youtubeIntent)
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
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
     * This method manages the dark/light mode
     */
    private fun darkMode() {
        if (night == "checked") {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorViolet))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorBlack))
            mName.setTextColor(resources.getColor(R.color.colorRed))
            mName.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mList.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mName.setTextColor(resources.getColor(R.color.colorRed))
            mName.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mList.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
        }
    }
}