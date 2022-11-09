package roberto.garzone.youtubereviews.services

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.activities.SplashScreenActivity
import java.util.Timer
import java.util.TimerTask

/**
 * This class manages the sending of notification to the user device
 */
class NotificationService : Service() {

    // Instance variables
    companion object {
        const val NOTIFICATION_CHANNEL_ID : String = "101"
        const val default_notification_channel_id : String = "default"
    }

    private var timer : Timer? = null
    private var millisec : Long = 7 * 24 * 360 * 1000
    private var handler : Handler = Handler()

    /**
     * This method returns an object IBinder to associate the service to an activity
     * @param p0 : Intent
     * @return IBinder
     */
    override fun onBind(p0: Intent?): IBinder? { return null }

    /**
     * This method starts the service
     * @param intent : Intent
     * @param flags : Int
     * @param startId : Int
     * @return Int
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startTimer()
        return START_STICKY
    }

    /**
     * This method destroys the service
     */
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    /**
     * This method starts the timer that triggers the service
     */
    private fun startTimer() {
        timer = Timer()

        timer!!.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        createNotification()
                    })
                }
            }, millisec)
    }

    /**
     * This method stops the timer
     */
    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    /**
     * This method defines the notification structure
     */
    private fun createNotification() {
        val intent = Intent(applicationContext, SplashScreenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@NotificationService, 0, intent, 0)
        var manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(applicationContext, default_notification_channel_id)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        builder.setContentTitle(resources.getString(R.string.channel_2_name))
            .setContentText(resources.getString(R.string.channel_2_message))
            .setSmallIcon(R.drawable.app_logo)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, resources.getString(R.string.channel_2_name), NotificationManager.IMPORTANCE_DEFAULT)

            if (manager != null) {
                manager.createNotificationChannel(channel)
            }

            manager.notify(System.currentTimeMillis() as Int, builder.build())
        }
    }
}