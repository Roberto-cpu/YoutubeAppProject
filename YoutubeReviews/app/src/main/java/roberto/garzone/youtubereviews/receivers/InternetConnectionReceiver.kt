package roberto.garzone.youtubereviews.receivers

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * This class manages the receiving of the connection status
 */
class InternetConnectionReceiver : BroadcastReceiver() {

    // Instance variables
    private lateinit var listener : InternetConnectionReceiverInterface

    /**
     * Constructor
     */
    init {}

    companion object {
        /**
         * This method checks if the device is connected or not
         */
        fun isConnected(ctx: Context): Boolean {
            val connectManager: ConnectivityManager =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @Suppress("DEPRECATION") val networkInfo: NetworkInfo? = connectManager.activeNetworkInfo

            @Suppress("DEPRECATION")
            return (networkInfo != null && networkInfo.isConnected)
        }
    }

    /**
     * This method defines the action after information receiving
     */
    override fun onReceive(p0: Context?, p1: Intent?) {
        val connectManager : ConnectivityManager = p0!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION") val networkInfo : NetworkInfo? = connectManager.activeNetworkInfo
        @Suppress("DEPRECATION") val isConnected : Boolean = networkInfo != null && networkInfo.isConnectedOrConnecting

        if (listener != null) {
            try {
                listener.onNetworkConnectionChanged(isConnected)
            } catch (e : java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * This interface defines the function of the receivers
     */
    interface InternetConnectionReceiverInterface {
        fun onNetworkConnectionChanged(isConnected : Boolean)
    }
}