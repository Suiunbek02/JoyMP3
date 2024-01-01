package com.example.joymp3.ui.service.focushelper

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

class MusicFocusHelper(private val context: Context, private val mediaPlayer: MediaPlayer) :
    AudioManager.OnAudioFocusChangeListener {

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun  requestFocus(): Boolean {
        val result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun  abandonFocus() {
        audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focus: Int) {
        when(focus){
            AudioManager.AUDIOFOCUS_GAIN ->{
                if (!mediaPlayer.isPlaying){
                    mediaPlayer.start()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS ->{
                if (mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                }
                mediaPlayer.release()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->{
                if (mediaPlayer.isPlaying){
                    mediaPlayer.setVolume(0.2f, 0.2f)
                }
            }
        }
    }
}