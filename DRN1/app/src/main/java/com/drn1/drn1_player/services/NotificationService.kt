package com.drn1.drn1_player.services

import android.app.*
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.drn1.drn1_player.DataHolder
import com.drn1.drn1_player.DataHolder.current
import com.drn1.drn1_player.DataHolder.get_Artist
import com.drn1.drn1_player.DataHolder.get_Media
import com.drn1.drn1_player.DataHolder.get_MediaPlayerImage
import com.drn1.drn1_player.DataHolder.get_Media_Type
import com.drn1.drn1_player.DataHolder.get_Song
import com.drn1.drn1_player.MainActivity
import com.drn1.drn1_player.R
import com.drn1.drn1_player.services.NotificationService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class NotificationService : Service() {

    override fun onCreate() {
        super.onCreate()
        player = SimpleExoPlayer.Builder(this).build()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        player!!.stop()
        player = null
        val localBroadcastManager = LocalBroadcastManager
            .getInstance(this@NotificationService)
        localBroadcastManager.sendBroadcast(
            Intent(
                "com.durga.action.close"
            )
        )
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.action == Constants.ACTION.STARTFOREGROUND_ACTION) {
        } else if (intent.action == Constants.ACTION.PREV_ACTION) {
            if (player != null) {
                if (player!!.isPlaying) {
                    bigViews!!.setImageViewResource(
                        R.id.status_bar_play,
                        R.drawable.exo_controls_play
                    )
                    player!!.pause()
                } else {
                    bigViews!!.setImageViewResource(R.id.status_bar_play, R.drawable.exo_icon_pause)
                    player!!.play()
                }

                mNotificationManager!!.notify(
                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    mBuilder!!.build()
                )
            }
        } else if (intent.action == Constants.ACTION.PLAY_ACTION) {
            if(DataHolder.current != get_Media()){
                println("I ran Media Player"+ get_Media() + "Current is set to "+ DataHolder.current)
            //if (!get_Song().contains("With Binance.com")) {
                player!!.removeMediaItem(0)
                val mediaItem = MediaItem.fromUri(get_Media())
                val currentTrack = get_Media()
                player!!.setMediaItem(mediaItem)
                player!!.prepare()
                player!!.play()
                DataHolder.current = get_Media()

           }
            showNotification()
        } else if (intent.action == Constants.ACTION.NEXT_ACTION) {
        } else if (intent.action ==
            Constants.ACTION.STOPFOREGROUND_ACTION
        ) {
            player!!.stop()
            player = null
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun showNotification() {
        bigViews = RemoteViews(
            packageName,
            R.layout.status_bar_expanded
        )
        Glide.with(this)
            .asBitmap()
            .load(get_MediaPlayerImage())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    updateNotification(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
        val notificationIntent = Intent(this@NotificationService, MainActivity::class.java)
        notificationIntent.action = Constants.ACTION.MAIN_ACTION
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this@NotificationService, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        bigViews!!.setTextViewText(R.id.status_bar_track_name, get_Song())
        bigViews!!.setTextViewText(R.id.status_bar_artist_name, get_Artist())
        bigViews!!.setTextViewText(R.id.status_bar_album_name, get_Media_Type().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        bigViews!!.setImageViewResource(R.id.status_bar_play, R.drawable.exo_icon_pause)
        val notificationClose = Intent(this@NotificationService, NotificationService::class.java)
        notificationClose.action = Constants.ACTION.STOPFOREGROUND_ACTION
        val notificationCloseIntent = PendingIntent.getService(
            this@NotificationService, 0,
            notificationClose, PendingIntent.FLAG_IMMUTABLE
        )
        bigViews!!.setOnClickPendingIntent(R.id.btn_close, notificationCloseIntent)
        val notificationPlay = Intent(this@NotificationService, NotificationService::class.java)
        notificationPlay.action = Constants.ACTION.PREV_ACTION
        val notificationPlayIntent = PendingIntent.getService(
            this@NotificationService, 0,
            notificationPlay, PendingIntent.FLAG_IMMUTABLE
        )
        bigViews!!.setOnClickPendingIntent(R.id.status_bar_play, notificationPlayIntent)
        mBuilder = NotificationCompat.Builder(
            applicationContext, "notify_001"
        )

        val bigText = NotificationCompat.BigTextStyle()
        mBuilder!!.setCustomContentView(bigViews)
        mBuilder!!.setCustomBigContentView(bigViews)
        mBuilder!!.setContentIntent(pendingIntent)
        mBuilder!!.setSmallIcon(R.mipmap.drn1logo)
        mBuilder!!.setContentTitle(get_Song())
        mBuilder!!.setContentText(get_Artist())
        mBuilder!!.priority = Notification.PRIORITY_LOW
        //mBuilder!!.setStyle(mediaStyle)
        mBuilder!!.setStyle(bigText)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notify_001"
            val channel = NotificationChannel(
                channelId,
                "Now Playing",
                IMPORTANCE_LOW

            )

            mNotificationManager!!.createNotificationChannel(channel)
            mBuilder!!.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            mBuilder!!.setChannelId(channelId)
            mBuilder!!.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.drn1logo))
            mBuilder!!.setSound(null)
        }
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder!!.build())
    }

    // use this method to update the Notification's UI
    private fun updateNotification(bitmap: Bitmap) {
        val api = Build.VERSION.SDK_INT
        // update the icon
        bigViews!!.setImageViewBitmap(R.id.status_bar_album_art, bitmap)

        // update the notification
        if (api < Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager!!.notify(
                Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                mBuilder!!.build()
            )
        } else if (api >= Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager!!.notify(
                Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                mBuilder!!.build()
            )
        }
    }

    companion object {
        private const val CHANNEL_ID = "MyChannelId"
        var mNotificationManager: NotificationManager? = null
        var player: SimpleExoPlayer? = null
        var bigViews: RemoteViews? = null
        var mBuilder: NotificationCompat.Builder? = null
    }
}