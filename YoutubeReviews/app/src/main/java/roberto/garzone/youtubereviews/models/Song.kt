package roberto.garzone.youtubereviews.models
/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

/**
 * This class manages the song instance's behaviour
 */
class Song(nn : String, ln : String, bd : String, gn : String, rt : String) : java.io.Serializable {

    // Instance variables
    private var name : String = ""
    private var link : String = ""
    private var band : String = ""
    private var genre : String = ""
    private var rating : String = ""
    private var reviews : ArrayList<Review> = ArrayList()

    /**
     * Constructor
     * @param nn : String
     * @param ln : String
     * @param db : String
     * @param gn : String
     * @param rt : String
     */
    init {
        this.name = nn
        this.link = ln
        this.band = bd
        this.genre = gn
        this.rating = rt
    }

    /**
     * This method returns the name of the song
     * @return String
     */
    fun getName() : String { return this.name }

    /**
     * This method returns the link for youtube
     * @return String
     */
    fun getYoutubeLink() : String { return this.link }

    /**
     * This method sets the youtube link
     * @param newLink : String
     */
    fun setYoutubeLink(newLink : String) { this.link = newLink }

    /**
     * This method returns the name of the band
     * @return String
     */
    fun getBand() : String { return this.band }

    /**
     * This method returns the genre of the song
     * @return String
     */
    fun getGenre() : String { return this.genre }

    /**
     * This method returns the current rating
     */
    fun getRating() : String { return this.rating }

    fun setRating(newRating : String) {
        val rtInt = this.rating.toInt()
        val newRtInt = newRating.toInt()
        val avg = (rtInt + newRtInt) / 2

        this.rating = avg.toString()
    }

    /**
     * This method returns the list of reviews for a song
     * @return ArrayList<Review>
     */
    fun getReviews() : ArrayList<Review> { return this.reviews }

    /**
     * This method sets the list of reviews
     * @param newReviewsList : ArrayList<Review>
     */
    fun setReviews(newReviewsList : ArrayList<Review>) { this.reviews = newReviewsList }

    /**
     * This method adds a new review
     * @param newReview : Review
     */
    fun addReview(newReview : Review) { this.reviews.add(newReview) }
}