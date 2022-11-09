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
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.adapters.CommentsListAdapter
import roberto.garzone.youtubereviews.dialogs.AddCommentDialog
import roberto.garzone.youtubereviews.models.Comment
import roberto.garzone.youtubereviews.models.Review
import roberto.garzone.youtubereviews.models.Song

/**
 * This class manages the review activity
 */
class ReviewActivity : AppCompatActivity(), AddCommentDialog.AddCommentDialogInterface {

    // Instance variables
    private var mLayout : LinearLayout = TODO()
    private var mTitle : TextView = TODO()
    private var mCreator : TextView = TODO()
    private var mText : TextView = TODO()
    private var mToolbar : Toolbar = TODO()
    private var mBack : Button = TODO()
    private var mMenu : Menu = TODO()
    private var mComments : ListView = TODO()
    private var mView : TextView = TODO()

    private var songs : ArrayList<Song>
    private var name : String = ""
    private var night : String = ""
    private var review : Review
    private var adapter : CommentsListAdapter
    private var auth : FirebaseAuth
    private var currUser : FirebaseUser

    /**
     * This method creates the activity layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.review_layout)
        mToolbar = findViewById(R.id.review_toolbar)
        mView = findViewById(R.id.review_page_title)
        mTitle = findViewById(R.id.text_title)
        mCreator = findViewById(R.id.text_creator)
        mText = findViewById(R.id.text_message)
        mBack = findViewById(R.id.review_back_button)

        var getIntent : Intent = intent

        if (getIntent != null) {
            songs = getIntent.getSerializableExtra("songs") as ArrayList<Song>
            name = getIntent.getStringExtra("song name").toString()
            review = getIntent.getSerializableExtra("review") as Review
            night = getIntent.getStringExtra("night mode").toString()

        }

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        mTitle.text = review.getTitle()
        mCreator.text = review.getCreator()
        mText.text = review.getText()

        auth = FirebaseAuth.getInstance()
        currUser = auth.currentUser!!

        mBack.setOnClickListener {
            var backIntent = Intent(this@ReviewActivity, ReviewsListActivity::class.java)

            backIntent.putExtra("songs", songs)
            backIntent.putExtra("song", name)
            backIntent.putExtra("night mode", night)

            startActivity(backIntent)
            finish()
        }
    }

    /**
     * This method defines what to do when the activity starts
     */
    override fun onStart() {
        super.onStart()

        darkMode()
        readCommentsFromFirestore()
    }

    /**
     * This method reads the comments saved into firebase firestore
     */
    private fun readCommentsFromFirestore() {
        var firestore = FirebaseFirestore.getInstance()

        firestore.collection("comments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                review.getCommentsList().clear()

                for (sn : QueryDocumentSnapshot in it.result) {
                    var comment = Comment(sn["Email"].toString(), sn["Text"].toString(), sn["Review"].toString())

                    if (comment.getReviewReference() == review.getId()) {
                        review.addComment(comment)
                    }
                }

                mComments = findViewById(R.id.comments_list)
                adapter = CommentsListAdapter(this@ReviewActivity, review.getCommentsList())

                if (night == "checked") {
                    mComments.setBackgroundColor(resources.getColor(R.color.colorWhite))
                }

                mComments.adapter = adapter
                mComments.isFastScrollEnabled = true
            }
        }
    }

    /**
     * This method creates a menu
     * @param menu : Menu
     * @return Boolean
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu!!
        menuInflater.inflate(R.menu.review_option_menu, mMenu)
        return true
    }

    /**
     * This method prepares the menu for its visualization
     * @param menu : Menu
     * @return Boolean
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var item : MenuItem = menu!!.findItem(R.id.add_comment_item)
        if (currUser.isAnonymous) { item.isVisible = false }
        return true
    }

    /**
     * This method defines what to do if the menu item is selected
     * @param item : MenuItem
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_comment_item) {
            var dialog : AddCommentDialog = AddCommentDialog()
            dialog.show(supportFragmentManager, "add comment")
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method implements the interface's method
     */
    override fun onAllowClicked(text: String) {
        var comment : Comment = Comment(currUser.email!!, text, review.getId())
        var firestore = FirebaseFirestore.getInstance()
        var docReference = firestore.collection("comments").document()
        var commentInfo = HashMap<String, String>()

        commentInfo["Email"] = comment.getEmailCreator()
        commentInfo["Text"] = comment.getText()
        commentInfo["Review"] = comment.getReviewReference()

        docReference.set(commentInfo)
        readCommentsFromFirestore()
    }

    /**
     * This method defines what to do if the back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        var backIntent = Intent(this@ReviewActivity, ReviewsListActivity::class.java)

        backIntent.putExtra("songs", songs)
        backIntent.putExtra("song", name)
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
            mView.setTextColor(resources.getColor(R.color.colorRed))
            mView.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
            mTitle.setTextColor(resources.getColor(R.color.colorWhite))
            mCreator.setTextColor(resources.getColor(R.color.colorWhite))
            mText.setTextColor(resources.getColor(R.color.colorWhite))
            mComments.background = resources.getDrawable(R.drawable.text_view_border_dark_mode)
        } else {
            mToolbar.setBackgroundColor(resources.getColor(R.color.colorLightGray))
            mLayout.setBackgroundColor(resources.getColor(R.color.colorCoolMint))
            mView.setTextColor(resources.getColor(R.color.colorRed))
            mView.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
            mTitle.setTextColor(resources.getColor(R.color.colorBlack))
            mCreator.setTextColor(resources.getColor(R.color.colorBlack))
            mText.setTextColor(resources.getColor(R.color.colorBlack))
            mComments.background = resources.getDrawable(R.drawable.text_view_border_light_mode)
        }
    }
}