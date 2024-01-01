package com.example.joymp3.ui.service

//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.media.AudioManager
//import android.media.MediaPlayer
//import android.os.Binder
//import android.os.Build
//import android.os.IBinder
//import androidx.core.app.NotificationCompat
//import com.example.joymp3.R
//import com.example.joymp3.ui.fragments.MediaPlayerListener
//import com.example.joymp3.ui.fragments.MusicPlayerManagerSer
//import com.example.joymp3.ui.fragments.interfac.MusicPlayerManagerProvider
//import com.example.joymp3.ui.fragments.song.SongListFragment
//import com.example.joymp3.ui.service.focushelper.MusicFocusHelper
//import com.example.joymp3.ui.splash.SplashScreen
//
//class MusicPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayerListener {
//
//    private lateinit var mediaPlayer: MediaPlayer
//    private lateinit var musicFocusHelper: MusicFocusHelper
//    private var mediaPlayerListener: MediaPlayerListener? = null
//    private val musicPlayerManager: MusicPlayerManagerSer = MusicPlayerManagerSer()
//    private val NOTIFICATION_ID = 1
//
//    companion object {
//        const val ACTION_PLAY = "com.example.joymp3.ACTION_PLAY"
//        const val ACTION_PAUSE = "com.example.joymp3.ACTION_PAUSE"
//        const val ACTION_STOP = "com.example.joymp3.ACTION_STOP"
//        const val EXTRA_TRACK_URI = "com.example.joymp3.EXTRA_TRACK_URI"
//    }
//
//    inner class MusicPlayerBinder : Binder() {
//        fun getService(): MusicPlayerService {
//            return this@MusicPlayerService
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return MusicPlayerBinder()
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        mediaPlayer = MediaPlayer()
//        mediaPlayer.setOnCompletionListener(this)
//        musicFocusHelper = MusicFocusHelper(this, mediaPlayer)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val action = intent?.action
//
//        when (action) {
//            MusicPlayerService.ACTION_PLAY -> {
//                val trackUri = intent?.getStringExtra(MusicPlayerService.EXTRA_TRACK_URI)
//                playMusic(trackUri)
//            }
//            MusicPlayerService.ACTION_PAUSE -> pauseMusic()
//            MusicPlayerService.ACTION_STOP -> stopMusic()
//            // Другие действия...
//        }
//        return START_NOT_STICKY
//    }
//
//    // Добавим новый метод для установки слушателя
//    fun setMediaPlayerListener(listener: MediaPlayerListener) {
//        mediaPlayerListener = listener
//    }
//
//    private fun playMusic(trackUri: String?) {
//        if (trackUri != null) {
//            mediaPlayer.reset()
//            mediaPlayer.setDataSource(trackUri)
//            mediaPlayer.prepare()
//            musicFocusHelper.requestFocus()
//            mediaPlayer.start()
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForeground(NOTIFICATION_ID, buildNotification())
//            }
//        }
//    }
//
//    private fun buildNotification(): Notification {
////         Создаем канал уведомлений для Android 8.0 (Oreo) и выше
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "music_player_channel"
//            val channelName = "Music Player"
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val channel = NotificationChannel(channelId, channelName, importance)
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Создаем уведомление
//        val notificationIntent = Intent(this, SplashScreen::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val playIntent = Intent(this, MusicPlayerService::class.java).apply {
//            action = ACTION_PLAY
//        }
//        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val pauseIntent = Intent(this, MusicPlayerService::class.java).apply {
//            action = ACTION_PAUSE
//        }
//        val pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        return NotificationCompat.Builder(this, "music_player_channel")
//            .setContentTitle("Music Player")
//            .setContentText("Now Playing: ....")
//            .setSmallIcon(R.drawable.img_1)
//            .setContentIntent(pendingIntent)
//            .addAction(R.drawable.play, "Play", playPendingIntent)  // Добавьте кнопку Play
//            .addAction(R.drawable.pause, "Pause", pausePendingIntent)  // Добавьте кнопку Pause
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setOngoing(true)
//            .build()
//    }
//
//    private fun pauseMusic() {
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.pause()
//            musicFocusHelper.abandonFocus()
//        }
//    }
//
//    private fun stopMusic() {
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.stop()
//            musicFocusHelper.abandonFocus()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                stopForeground(true)
//            }
//        }
//    }
//
//    override fun onCompletion(p0: MediaPlayer?) {
//        mediaPlayerListener?.onSongCompletion()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release()
//    }
//
//    override fun onSongCompletion() {
//        TODO("Not yet implemented")
//    }
//
//    override fun getMusicPlayerManager(): MusicPlayerManagerSer {
//        return musicPlayerManager
//    }



