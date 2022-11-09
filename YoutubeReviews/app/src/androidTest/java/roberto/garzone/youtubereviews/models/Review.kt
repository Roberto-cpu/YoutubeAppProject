package roberto.garzone.youtubereviews.models

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

/**
 * This class manages review instance's behaviour
 * @param id : String
 * @param creator : String
 * @param title : String
 * @param text : String
 * @param songRef : String
 */
class Review(id : String, creator : String, title : String, text : String, songRef : String) : java.io.Serializable {

    // Instance variables
    private var mId : String = ""
    private var mCreatorEmail : String = ""
    private var mTitle : String = ""
    private var mText : String = ""
    private var mSongReference : String = ""
    private var mComments : ArrayList<Comment>? = null

    /**
     * Constructor
     */
    init {
        this.mId = id
        this.mCreatorEmail = creator
        this.mTitle = title
        this.mText = text
        this.mSongReference = songRef
        this.mComments = ArrayList()
    }

    /**
     * This method returns the review's id
     * @return String
     */
    fun getId() : String { return this.mId }

    /**
     * This method sets the review's id
     */
    fun setId(id : String) { this.mId = id }

    /**
     * This method returns the review's creator
     * @return String
     */
    fun getCreator() : String { return this.mCreatorEmail }

    /**
     * This method returns the title of the review
     * @return String
     */
    fun getTitle() : String { return this.mTitle }

    /**
     * This method returns the review's text
     * @return String
     */
    fun getText() : String { return this.mText }

    /**
     * This method sets the review's text
     * @param value : String
     */
    fun setText(value : String) { this.mText = value }

    /**
     * This method returns the review's song reference
     * @return String
     */
    fun getSongReference() : String { return this.mSongReference }

    /**
     * This method returns the list of comments
     * @return ArrayList<Comment>
     */
    fun getCommentsList() : ArrayList<Comment> { return this.mComments!! }

    /**
     * This method sets the list of comments
     * @param comments : ArrayList<Comment>
     */
    fun setCommentsList(comments : ArrayList<Comment>) { this.mComments = comments }

    /**
     * This methods adds a new comment
     * @param comment : Comment
     */
    fun addComment(comment : Comment) { this.mComments!!.add(comment) }

    /**
     * This methods deletes a comment
     * @param position : Int
     */
    fun deleteComment(position : Int) { this.mComments!!.removeAt(position) }
}