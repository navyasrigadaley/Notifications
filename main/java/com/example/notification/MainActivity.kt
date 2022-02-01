package com.example.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import android.content.Intent
import android.app.PendingIntent
//import android.R

import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.content.BroadcastReceiver
import android.content.Context

//import android.R

import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Button


//import android.R


class MainActivity : AppCompatActivity() {

    private var mNotifyManager: NotificationManager? = null
    private val mReceiver: NotificationReceiver = NotificationReceiver()

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState The current state data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the notification channel.
        createNotificationChannel()

        // Register the broadcast receiver to receive the update action from
        // the notification.
        registerReceiver(mReceiver,
            IntentFilter(ACTION_UPDATE_NOTIFICATION))

        // Add onClick handlers to all the buttons.
       val button_notify = findViewById<Button>(R.id.notify)
        button_notify.setOnClickListener(View.OnClickListener { // Send the notification
            sendNotification()
        })
       val button_update = findViewById<Button>(R.id.update) as Button
        button_update!!.setOnClickListener { // Update the notification.
            updateNotification()
        }
       val button_cancel = findViewById<Button>(R.id.cancel) as Button
        button_cancel!!.setOnClickListener { // Cancel the notification.
            cancelNotification()
        }

        // Reset the button states. Enable only Notify button and disable
        // update and cancel buttons.
        setNotificationButtonState(true, false, false)
    }

    /**
     * Unregisters the receiver when the app is being destroyed.
     */
    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * OnClick method for the "Notify Me!" button.
     * Creates and delivers a simple notification.
     */
    fun sendNotification() {

        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this,
            NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)

        // Build the notification with all of the parameters using helper
        // method.
        val notifyBuilder = notificationBuilder

        // Add the action button using the pending intent.
        notifyBuilder.addAction(R.drawable.ic_update,
            getString(R.string.update), updatePendingIntent)

        // Deliver the notification.
        mNotifyManager!!.notify(NOTIFICATION_ID, notifyBuilder.build())

        // Enable the update and cancel buttons but disables the "Notify
        // Me!" button.
        setNotificationButtonState(false, true, true)
    }// Set up the pending intent that is delivered when the notification
    // is clicked.

    // Build the notification with all of the parameters.
    /**
     * Helper method that builds the notification.
     *
     * @return NotificationCompat.Builder: notification build with all the
     * parameters.
     */
    private val notificationBuilder: NotificationCompat.Builder
        private get() {

            // Set up the pending intent that is delivered when the notification
            // is clicked.
            val notificationIntent = Intent(this, MainActivity::class.java)
            val notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            // Build the notification with all of the parameters.
            return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_android)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        }

    /**
     * OnClick method for the "Update Me!" button. Updates the existing
     * notification to show a picture.
     */
    fun updateNotification() {

        // Load the drawable resource into the a bitmap image.
        val androidImage = BitmapFactory
            .decodeResource(resources, R.drawable.mascot_1)

        // Build the notification with all of the parameters using helper
        // method.
        val notifyBuilder = notificationBuilder

        // Update the notification style to BigPictureStyle.
        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidImage)
            .setBigContentTitle(getString(R.string.notification_updated)))

        // Deliver the notification.
        mNotifyManager!!.notify(NOTIFICATION_ID, notifyBuilder.build())

        // Disable the update button, leaving only the cancel button enabled.
        setNotificationButtonState(false, false, true)
    }

    /**
     * OnClick method for the "Cancel Me!" button. Cancels the notification.
     */
    fun cancelNotification() {
        // Cancel the notification.
        mNotifyManager!!.cancel(NOTIFICATION_ID)

        // Reset the buttons.
        setNotificationButtonState(true, false, false)
    }

    /**
     * Helper method to enable/disable the buttons.
     *
     * @param isNotifyEnabled, boolean: true if notify button enabled
     * @param isUpdateEnabled, boolean: true if update button enabled
     * @param isCancelEnabled, boolean: true if cancel button enabled
     */
    fun setNotificationButtonState(
        isNotifyEnabled: Boolean?,
        isUpdateEnabled: Boolean?,
        isCancelEnabled: Boolean?,
    ) {
        val button_notify = findViewById<Button>(R.id.notify)
        val button_update = findViewById<Button>(R.id.update)
        val button_cancel = findViewById<Button>(R.id.cancel)
        button_notify!!.isEnabled = isNotifyEnabled!!
        button_update!!.isEnabled = isUpdateEnabled!!
        button_cancel!!.isEnabled = isCancelEnabled!!
    }

    /**
     * The broadcast receiver class for notifications.
     * Responds to the update notification pending intent action.
     */
    inner class NotificationReceiver : BroadcastReceiver() {
        /**
         * Receives the incoming broadcasts and responds accordingly.
         *
         * @param context Context of the app when the broadcast is received.
         * @param intent The broadcast intent containing the action.
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            // Update the notification.
            updateNotification()
        }
    }

    companion object {
        // Constants for the notification actions buttons.
        private const val ACTION_UPDATE_NOTIFICATION =
            "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION"

        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

        // Notification ID.
        private const val NOTIFICATION_ID = 0
    }
}