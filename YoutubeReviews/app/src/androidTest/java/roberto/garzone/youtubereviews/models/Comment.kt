package roberto.garzone.youtubereviews.models

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */


/**
 * This class manages the comment instance's behaviour
 * @param email : String
 * @param text : String
 * @param revRef : String
 */
class Comment(email : String, text : String, revRef : String) : java.io.Serializable {

    // Instance variables
    private var mEmail : String = ""
    private var mText : String = ""
    private var mRevRef : String = ""

    /**
     * Constructor
     */
    init {
        this.mEmail = email
        this.mText = text
        this.mRevRef = revRef
    }

    /**
     * This method returns the creator's email
     * @return String
     */
    fun getEmailCreator() : String { return this.mEmail }

    /**
     * This method returns the text
     * @return String
     */
    fun getText() : String { return this.mText }

    /**
     * This method sets the comment's text
     * @param text : String
     */
    fun setText(text : String) { this.mText = text }

    /**
     * This method returns the review id
     * @return String
     */
    fun getReviewReference() : String { return this.mRevRef }
}