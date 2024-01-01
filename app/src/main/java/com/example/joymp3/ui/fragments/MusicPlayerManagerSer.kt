package com.example.joymp3.ui.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.example.joymp3.App
import com.example.joymp3.R
import com.example.joymp3.data.model.SongModel
import com.example.joymp3.ui.fragments.song.SongListFragment
import com.example.joymp3.ui.service.MusicPlayerService

open class MusicPlayerManagerSer : MediaPlayer.OnCompletionListener {

    private var isPaused: Boolean = false
    private val context = App.instance
    private var songName = ""
    private var mediaPlayerListener: MediaPlayerListener? = null
    private var mediaPlayer: MediaPlayer? = null

//    inner class MusicPlayerBinder : Binder() {
//        fun getService(): MusicPlayerManagerSer {
//            return this@MusicPlayerManagerSer
//        }
//    }

    fun playSong(
        song: SongModel,
        playPause: ImageView,
        tv: TextView,
        artist: TextView,
        albumArt: ImageView
    ) {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(song.data)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } else {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song.data)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

        }
        isPaused = false
        songName = song.title!!
        artist.text = song.artist!!

        mediaPlayer!!.setOnCompletionListener(this)
        val uri = Uri.parse(song.image)
        albumArt.setImageURI(uri)
        updatePlayPauseButtonUi(playPause)
        animateSongNameScroll(tv)
    }

    fun pauseSong(playPause: ImageView) {

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
        }
        updatePlayPauseButtonUi(playPause)
    }

    fun resumeSong(playPause: ImageView) {

        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
        updatePlayPauseButtonUi(playPause)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun seekTo(value: Long) {
        mediaPlayer?.seekTo(value.toInt())
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    fun setValume(vol1: Float, vol2: Float) {
        mediaPlayer?.setVolume(vol1, vol2)
    }

    fun setMediaPlayerListener(listener: MediaPlayerListener) {
        mediaPlayerListener = listener
    }

    override fun onCompletion(p0: MediaPlayer?) {
        mediaPlayerListener?.onSongCompletion()
    }

    private fun updatePlayPauseButtonUi(playPause: ImageView) {
        playPause.setImageResource(
            if (isPlaying())
                R.drawable.pause else R.drawable.play
        )
    }

    private fun animateSongNameScroll(tv: TextView) {

        tv.text = songName

        val textWidth = tv.paint.measureText(tv.text.toString())
        val screenWith = context.resources.displayMetrics.widthPixels.toFloat()
        val translateAnimation = TranslateAnimation(screenWith, -textWidth, 0f, 0f)
        translateAnimation.duration = (textWidth / screenWith * 10000).toLong()
        translateAnimation.repeatCount = Animation.INFINITE
        translateAnimation.interpolator = LinearInterpolator()
        tv.startAnimation(translateAnimation)
    }
//
//    override fun onSongCompletion() {
//        TODO("Not yet implemented")
//    }
}

interface MediaPlayerListener {
    fun onSongCompletion()
}