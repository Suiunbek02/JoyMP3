package com.example.joymp3.ui.service.manager

import android.content.Context
import android.content.Intent
import com.example.joymp3.ui.service.MusicPlayerService
import com.example.joymp3.ui.service.MusicPlayerService.Companion.ACTION_PAUSE
import com.example.joymp3.ui.service.MusicPlayerService.Companion.ACTION_PLAY
import com.example.joymp3.ui.service.MusicPlayerService.Companion.ACTION_STOP
import com.example.joymp3.ui.service.MusicPlayerService.Companion.EXTRA_TRACK_URI
//
//class MusicPlayerManagerSer(private val context: Context) {
//
////    companion object {
////        const val ACTION_PLAY = "com.example.joymp3.ACTION_PLAY"
////        const val ACTION_PAUSE = "com.example.joymp3.ACTION_PAUSE"
////        const val ACTION_STOP = "com.example.joymp3.ACTION_STOP"
////        const val EXTRA_TRACK_URI = "com.example.joymp3.EXTRA_TRACK_URI"
////    }
//
//    fun playMusic(trackUri: String) {
//        val intent = Intent(context, MusicPlayerService::class.java)
//        intent.action = ACTION_PLAY
//        intent.putExtra(EXTRA_TRACK_URI, trackUri)
//        context.startService(intent)
//    }
//
//    fun pauseMusic() {
//        val intent = Intent(context, MusicPlayerService::class.java)
//        intent.action = ACTION_PAUSE
//        context.startService(intent)
//    }
//
//    fun stopMusic() {
//        val intent = Intent(context, MusicPlayerService::class.java)
//        intent.action = ACTION_STOP
//        context.startService(intent)
//    }
//
//}