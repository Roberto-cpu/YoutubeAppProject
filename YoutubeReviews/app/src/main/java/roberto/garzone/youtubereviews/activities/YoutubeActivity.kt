package roberto.garzone.youtubereviews.activities

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.configurations.YoutubeConfiguration
import roberto.garzone.youtubereviews.models.Song

/**
 * This class manages the youtube video playing
 */
open class YoutubeActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    // Instance variables
    companion object {
        private const val YOUTUBE_REQUEST_CODE : Int = 100
    }

    private var mYoutube : YouTubePlayerView = TODO()

    private var songId : String = ""
    private var night : String = ""
    private var songs : ArrayList<Song> = ArrayList()
    private var song : Song
    private var getIntent : Intent

    /**
     * This method creates the activity's layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mYoutube = findViewById(R.id.youtube_view)

        getIntent = intent
    }

    /**
     * This method defines the activity behaviour when it starts
     */
    override fun onStart() {
        super.onStart()

        mYoutube.initialize(YoutubeConfiguration.YOUTUBE_API_KEY, this@YoutubeActivity)
    }

    /**
     * This method initializes the youtube interaction
     * @param p0 : YoutubePlayer.Provider?
     * @param p1 : YoutubePlayer?
     * @param p2 : Boolean
     */
    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        if (!p2) {
            if (getIntent != null) {
                songId = getIntent.getStringExtra("song").toString()
                songs = getIntent.getSerializableExtra("songs") as ArrayList<Song>
                night = getIntent.getStringExtra("night mode").toString()
            }

            var splitted = songId.split(" - ")

            for (s : Song in songs) {
                if (s.getName() == splitted[0] && song.getBand() == splitted[1]) {
                    song = s
                }
            }

            var idVideo : String = getYoutubeId(song.getYoutubeLink())
            p1!!.setShowFullscreenButton(false)
            p1!!.cueVideo(idVideo)
        }
    }

    /**
     * This method return the youtube id for the song
     * @param link : String
     */
    private fun getYoutubeId(link : String) : String {
        var split = link.split("/")
        return split[3]
    }

    /**
     * This method manages the activity behaviour if the initialization fail
     * @param p0 : YoutubePlayer.Provider?
     * @param p1 : YoutubeInitializationResult?
     */
    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        if (p1!!.isUserRecoverableError) {
            p1!!.getErrorDialog(this@YoutubeActivity, YOUTUBE_REQUEST_CODE).show()
        } else {
            var error : String = String.format(resources.getString(R.string.youtbe_player_error), p1!!.toString())
            Toast.makeText(this@YoutubeActivity, error, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * This method returns the youtube provider
     * @return YoutubePlayer.Provider
     */
    protected open fun getYoutubePlayerProvider() : YouTubePlayer.Provider { return mYoutube }

    /**
     * This method manages the gaining of the result of the activity
     * @param resultCode : Int
     * @param requestCode : Int
     * @param data : Intent?
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == YOUTUBE_REQUEST_CODE) {
            getYoutubePlayerProvider().initialize(YoutubeConfiguration.YOUTUBE_API_KEY, this@YoutubeActivity)
        }
    }
}