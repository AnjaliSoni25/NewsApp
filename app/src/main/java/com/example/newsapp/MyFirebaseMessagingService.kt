package com.example.newsapp

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM message here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Add your custom notification handling code
        if (remoteMessage.notification != null) {
            // Since the notification is received directly
            // from FCM, the title and the body can be
            // fetched directly as below.
            remoteMessage.notification!!.title?.let {
                remoteMessage.notification!!.body?.let { it1 ->
                    showNotification(it,it1)

                }
            }
        }
    }

    // Method to display the notifications
    private fun showNotification(title: String, message: String) {
        val channelId = "NewsAppChannelId"
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

       // Create a PendingIntent to wrap the notificationIntent for the whole notification
        val contentIntent = getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(contentIntent)
            .setStyle(NotificationCompat.BigTextStyle())
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(1000, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

    }




}


