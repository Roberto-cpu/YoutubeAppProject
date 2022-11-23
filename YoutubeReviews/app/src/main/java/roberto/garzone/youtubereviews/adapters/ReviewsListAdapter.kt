package roberto.garzone.youtubereviews.adapters

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import roberto.garzone.youtubereviews.models.Comment
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.models.Review

/**
 * This class manages the adapter behaviour
 * @param ctx : Context
 * @param reviews : ArrayList<Review>
 */
class ReviewsListAdapter(ctx : Context, reviews : ArrayList<Review>) : BaseAdapter() {

    // Instance variables
    private var mContext : Context
    private var mReviews : ArrayList<Review>

    /**
     * Constructor
     */
    init {
        this.mContext = ctx
        this.mReviews = reviews
    }

    /**
     * This method returns the list size
     * @return Int
     */
    override fun getCount(): Int { return this.mReviews.size }

    /**
     * This method returns an item from a specific position
     * @param p0 : Int
     * @return Any
     */
    override fun getItem(p0: Int): Any { return this.mReviews[p0]
    }

    /**
     * This method return the position of an item
     * @param p0 : Int
     * @return Long
     */
    override fun getItemId(p0: Int): Long { return p0.toLong() }

    /**
     * This method creates the adapter layout
     * @param p0 : Int
     * @param p1 : View
     * @param p2 : ViewGroup
     * @return View
     */
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {

        var convertView : View? = p1
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.reviews_list_item, p2, false)
        }

        val mTitle : TextView = convertView!!.findViewById(R.id.review_title)
        val mCreator : TextView = convertView.findViewById(R.id.review_creator)
        val mText : TextView = convertView.findViewById(R.id.review_message)
        val mComments : TextView = convertView.findViewById(R.id.review_number_comments)
        val db = FirebaseFirestore.getInstance()

        mTitle.text = this.mReviews[p0].getTitle()
        mCreator.text = this.mReviews[p0].getCreator()
        mText.text = this.mReviews[p0].getText()
        mComments.text = "${this.mContext.resources.getString(R.string.reviews_list_number_comments)} -"

        Thread {
            db.collection("comments").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    var count = 0
                    for (snapshot: QueryDocumentSnapshot in it.result) {
                        val comment = Comment(
                            snapshot.get("Email").toString(), snapshot.get("Text").toString(),
                            snapshot.get("Review").toString()
                        )

                        if (comment.getReviewReference() == this.mReviews[p0].getId()) {
                            count++
                        }
                    }

                    mComments.text = "${this.mContext.resources.getString(R.string.reviews_list_number_comments)} $count"
                }
                else {
                    mComments.text = "0"
                }
            }
        }

        return convertView
    }
}