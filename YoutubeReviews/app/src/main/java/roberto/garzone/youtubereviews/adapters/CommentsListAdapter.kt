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
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.models.Comment

/**
 * This class manages the comments list adapter
 * @param ctx : Context
 * @param comments : ArrayList<Comment>
 */
class CommentsListAdapter(ctx : Context, comments : ArrayList<Comment>) : BaseAdapter() {

    // Instance variables
    private var mContext : Context
    private var mComments : ArrayList<Comment>

    /**
     * Constructor
     */
    init {
        this.mContext = ctx
        this.mComments = comments
    }

    /**
     * This method returns the list size
     * @return Int
     */
    override fun getCount(): Int { return this.mComments.size }

    /**
     * This method returns a specific item
     * @param p0 : Int
     * @return Any
     */
    override fun getItem(p0: Int): Any { return this.mComments[p0] }

    /**
     * This method returns the position of a specific item
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
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var convertView : View? = p1

        if (convertView != null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.comments_list_item, p2, false)
        }

        val mCreator : TextView = convertView!!.findViewById(R.id.comments_list_creator)
        val mText : TextView = convertView.findViewById(R.id.comments_list_text)

        mCreator.text = this.mComments[p0].getEmailCreator()
        mText.text = this.mComments[p0].getText()

        return convertView
    }
}