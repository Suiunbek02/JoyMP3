package com.example.joymp3.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.joymp3.R
import com.example.joymp3.ui.activity.MainActivity
import com.example.joymp3.ui.fragments.song.SongListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.splash_screen)

        supportActionBar?.hide()
        val handler: Handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}