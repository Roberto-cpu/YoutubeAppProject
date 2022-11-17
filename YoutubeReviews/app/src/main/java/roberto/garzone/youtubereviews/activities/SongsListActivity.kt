package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
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
import roberto.garzone.youtubereviews.models.Song
import roberto.garzone.youtubereviews.services.NotificationService

/**
 * This class manages the songs list activity
 */
class SongsListActivity : AppCompatActivity() {

    // Instance variables
    private lateinit var mLayout : ConstraintLayout 
    private lateinit var mTitle : TextView 
    private lateinit var mSongs : ListView 
    private lateinit var mToolbar : Toolbar 
    private lateinit var mSpinner : Spinner 
    private lateinit var mSettings : Button 
    private lateinit var mMenu : Menu 

    private lateinit var songs : ArrayList<Song>
    private lateinit var names : ArrayList<String>
    private lateinit var genres : ArrayList<String>
    private lateinit var listAdapter : ArrayAdapter<Any>
    private lateinit var spinnerAdapter : ArrayAdapter<Any>
    private var night : String = ""
    private var count : Int = 0
    private lateinit var auth : FirebaseAuth
    private lateinit var currUser : FirebaseUser
    private lateinit var getIntent : Intent

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.songs_list_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.songs_list_layout)
        mToolbar = findViewById(R.id.songs_list_toolbar)
        mTitle = findViewById(R.id.songs_list_title)
        mSettings = findViewById(R.id.songs_list_settings)

        getIntent = intent

        night = getIntent.getStringExtra("night mode").toString()

        mSettings.setOnClickListener {
            val settingsIntent = Intent(this@SongsListActivity, SettingsActivity::class.java)
            settingsIntent.putExtra("night mode", night)

            startActivity(settingsIntent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        currUser = auth.currentUser!!

        songs = ArrayList()
        names = ArrayList()
        genres = ArrayList()

        genres.add(resources.getString(R.string.songs_list_all))

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""
    }

    /**
     * This method defines what to do when tha activity starts
     */
    override fun onStart() {
        super.onStart()

        stopService(Intent(this@SongsListActivity, NotificationService::class.java))
        readSongsFromFirestore()
        darkMode()
    }

    /**
     * This method reads the songs from firebase firestore
     */
    private fun readSongsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("songs").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (sn : QueryDocumentSnapshot in it.result) {
                    val song = Song(sn["Name"].toString(), sn["Link"].toString(), sn["Band"].toString(), sn["Genre"].toString(), sn["Rating"].toString())

                    songs.add(song)
                    if (!genres.contains(song.getGenre().uppercase())) {
                        genres.add(song.getGenre().uppercase())
                    }
                }

                mSpinner = findViewById(R.id.songs_list_spinner)
                mSongs = findViewById(R.id.songs_list)

                spinnerAdapter = ArrayAdapter(this@SongsListActivity, android.R.layout.simple_spinner_item, genres as List<Any>)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mSpinner.adapter = spinnerAdapter
                mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        val genre : String = p0!!.getItemAtPosition(p2).toString()

                        names.clear()
                        for (s : Song in songs) {
                            if (genre == resources.getString(R.string.songs_list_all)) {
                                val name = "${s.getName()} - ${s.getBand()}"
                                names.add(name)
                            } else if (genre == s.getGenre().uppercase()) {
                                val name = "${s.getName()} - ${s.getBand()}"
                                names.add(name)
                            }
                        }

                        listAdapter = ArrayAdapter(this@SongsListActivity, android.R.layout.simple_list_item_1, names as List<Any>)
                        mSongs.adapter = listAdapter
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                }
            }

            mSongs.setOnItemClickListener { _, _, i, _ ->
                val songIntent = Intent(this@SongsListActivity, ReviewsListActivity::class.java)

                songIntent.putExtra("song", mSongs.getItemAtPosition(i).toString())
                songIntent.putExtra("songs", songs)
                songIntent.putExtra("night mode", night)

                startActivity(songIntent)
                finish()
            }
        }
    }

    /**
     * This method creates an option menu
     * @param menu : Menu
     * @return Boolean
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu!!
        menuInflater.inflate(R.menu.tracks_list_option_menu, mMenu)
        return true
    }

    /**
     * This method sets the visibility of menu items
     * @param menu : Menu
     * @return Boolean
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val reviewItem : MenuItem =  menu!!.findItem(R.id.tracks_list_new_review)
        val logoutItem : MenuItem = menu.findItem(R.id.tracks_list_logout)

        if (currUser.isAnonymous) {
            reviewItem.isVisible = false
            logoutItem.title = resources.getString(R.string.tracks_list_option_menu_back)
        }

        return true
    }

    /**
     * This method defines what to do when a menu item is selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tracks_list_new_review -> {
                val newReviewIntent = Intent(this@SongsListActivity, NewReviewActivity::class.java)
                newReviewIntent.putExtra("night mode", night)

                startActivity(newReviewIntent)
                finish()
                return true
            }
            R.id.tracks_list_logout -> {
                auth.signOut()

                startActivity(Intent(this@SongsListActivity, LoginActivity::class.java))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method defines what to do when back button is pressed
     */
    override fun onBackPressed() {
        count++

        if (count == 1) {
            Toast.makeText(this@SongsListActivity, resources.getString(R.string.songs_list_back_pressed_message), Toast.LENGTH_SHORT).show()
            val handler = Handler()

            handler.postDelayed({
                count = 0
            }, 2000)
        } else if (count == 2) {
            auth.signOut()
            super.onBackPressed()

            startActivity(Intent(this@SongsListActivity, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * This method defines the activity's behaviour when it is destroyed.
     * In particular, starts, with the delay of a day, a service that sends a notification after a week of app's inactivity
     */
    override fun onDestroy() {
        super.onDestroy()
        val handler = Handler()

        handler.postDelayed( {
            startService(Intent(this@SongsListActivity, NotificationService::class.java))
        }, 86400000)
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
            mSpinner.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mSongs.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mTitle.setTextColor(resources.getColor(R.color.colorRed))
            mTitle.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mSpinner.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mSongs.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
        }
    }
}