package com.hsu.mapapp.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hsu.mapapp.R
import com.hsu.mapapp.SplashActivity
import com.hsu.mapapp.databinding.ActivityNotisetBinding
import com.hsu.mapapp.databinding.ActivityProfileBinding

class NotisetActivity : AppCompatActivity() {
    private lateinit var notisetBinding: ActivityNotisetBinding
    private val CHANNEL_ID = "fillin map"
    private val notificationid = 101
    private var vibe = 0
    private var sound = 0

    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("알림 설정")
        super.onCreate(savedInstanceState)
        notisetBinding = ActivityNotisetBinding.inflate(layoutInflater)
        setContentView(notisetBinding.root)

        createNotificationChannel()

        notisetBinding.send.setOnClickListener {
            sendNotification()
        }

        notisetBinding.notisetVibration.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch) {
                vibe = 1;
            }
            else {
                vibe = 0;
            }
        }

        notisetBinding.notisetSound.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch) {
                sound = 1;
            }
            else {
                sound = 0;
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificationi Title"
            val descriptionText = "Notification Description"
            val importance:Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel:NotificationChannel = NotificationChannel(CHANNEL_ID,name, importance).apply {
                description= descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val intent: Intent = Intent(this,SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.base_map)
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.base_map)
            .setContentTitle("Example Title")
            .setContentText("Example Description")
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if(vibe == 1) {
            builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        }
        with(NotificationManagerCompat.from(this)) {
            notify(notificationid, builder.build())
        }
    }
}