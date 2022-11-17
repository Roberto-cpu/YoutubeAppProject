package roberto.garzone.youtubereviews.models

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

/**
 * This class manages the user instance's behaviour
 * @param username : String
 * @param email : String
 * @param pwd : String
 */
class User(username : String, email : String, pwd : String) : java.io.Serializable {

    // Instance variables
    private var mUsername : String = ""
    private var mEmail : String = ""
    private var mPassword : String = ""

    /**
     * Constructor
     */
    init {
        this.mUsername = username
        this.mEmail = email
        this.mPassword = pwd
    }

    /**
     * This method returns the username
     */
    fun getUsername() : String { return this.mUsername }

    /**
     * This method sets the usenrame
     */
    fun setUsername(username : String) { this.mUsername = username }

    /**
     * This method returns the email
     */
    fun getEmail() : String { return this.mEmail }

    /**
     * This method sets the email
     */
    fun setEmail(email : String) { this.mEmail = email }

    /**
     * This method returns the password
     */
    fun getPassword() : String { return this.mPassword }

    /**
     * This method sets the password
     */
    fun setPassword(pwd : String) { this.mPassword = pwd}
}