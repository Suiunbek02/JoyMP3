package com.example.joymp3.ui.fragments.song

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.joymp3.App
import com.example.joymp3.R
import com.example.joymp3.databinding.FragmentSongListBinding
import com.example.joymp3.ui.adapters.SongAdapter
import com.example.joymp3.data.model.SongModel
import com.example.joymp3.repository.SongRepository
import com.example.joymp3.ui.adapters.onSongItemClicked
import com.example.joymp3.ui.fragments.MediaPlayerListener
import com.example.joymp3.ui.fragments.MusicPlayerManagerSer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Suppress("DEPRECATION")
class SongListFragment : Fragment(R.layout.fragment_song_list), onSongItemClicked,
    MediaPlayerListener {

    private lateinit var binding: FragmentSongListBinding
    private lateinit var viewModel: SongViewModel
    private val REQUEST_PERMISSION_CODE = 123

    private lateinit var adapter: SongAdapter

    private var currentSongPosition: Int = -1

    private var musicPlayerManager: MusicPlayerManagerSer? = null
//    private var musicService: MusicPlayerService? = null

    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private lateinit var audioFocRequest: AudioFocusRequest
    private lateinit var musicPlayerServiceIntent: Intent

    private var maxVolume: Int = 0

    private var currentVolumeLevel: Int = 0

    private lateinit var playPauseEx: ImageView
    private lateinit var nextEx: ImageView
    private lateinit var previousEx: ImageView

    private lateinit var playPauseCom: ImageView
    private lateinit var nextCom: ImageView
    private lateinit var previousCom: ImageView

    private lateinit var songseekBar: SeekBar
    private lateinit var volumeSeekBar: SeekBar

    private lateinit var progressForwadTv: TextView
    private lateinit var songRemainingTv: TextView
    private lateinit var songTitle: TextView
    private lateinit var songTitleCom: TextView

    private lateinit var songArtistName: TextView

    private lateinit var albumCoverImageView: ImageView


    private lateinit var slideDownAnimation: Animation
    private lateinit var slideUpAnimation: Animation


    private var durationMillis: Long? = null

    interface MusicPlayerServiceConnection {
        fun getMusicPlayerManager(): MusicPlayerManagerSer
    }

//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as? MusicPlayerService.MusicPlayerBinder
//            musicService = binder?.getService()
//            musicService!!.setMediaPlayerListener(this@SongListFragment)
//            updateUI()
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            musicPlayerManager = null
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_list, container, false)

        val rv = binding.rvSongList
        adapter = SongAdapter()
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        val repository = SongRepository()
        viewModel = ViewModelProvider(this, SongViewModelFactory(repository)).get(SongViewModel::class.java)

        //----
//        musicPlayerServiceIntent = Intent(requireContext(), MusicPlayerService::class.java)

        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //----

        viewModel.fetchAudio(requireContext())
        viewModel.audioK.observe(viewLifecycleOwner, Observer { audio->
            adapter.setList(audio)
        })

        requestAudioFoc()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyFragment", "onViewCreated")

        try {
            initUI()
            Log.d("MyFragment", "initUI success")
            initAnimations()
            Log.d("MyFragment", "initAnimations success")
        } catch (e: Exception) {
            Log.e("MyFragment", "Error during initUI or initAnimations", e)
        }

//        val intent = Intent(requireContext(), MusicPlayerService::class.java)
//        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    private fun initAnimations() {
        Log.d("MyFragment", "initAnimations")
        slideDownAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        Log.d("MyFragment", "initAnimations success")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        Log.d("MyFragment", "initUI success")
        musicPlayerManager = (requireActivity().application as App).musicPlayerManager

        musicPlayerManager!!.setMediaPlayerListener(this)

        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        playPauseEx = binding.expandedPlayerLayout.findViewById(R.id.playPauseButton)
        nextEx = binding.expandedPlayerLayout.findViewById(R.id.nextButton)
        previousEx = binding.expandedPlayerLayout.findViewById(R.id.previousButton)

        playPauseCom = binding.compactPlayerLayout.findViewById(R.id.playPauseButtonMinimized)
        nextCom = binding.compactPlayerLayout.findViewById(R.id.nextButtonMinimized)
        previousCom = binding.compactPlayerLayout.findViewById(R.id.previousButtonMinimized)

        songseekBar = binding.expandedPlayerLayout.findViewById(R.id.linearProgressBar)
        volumeSeekBar = binding.expandedPlayerLayout.findViewById(R.id.volumeBar)

        progressForwadTv = binding.expandedPlayerLayout.findViewById(R.id.startTextView)
        songRemainingTv = binding.expandedPlayerLayout.findViewById(R.id.endTextView)

        songTitle = binding.expandedPlayerLayout.findViewById(R.id.songTitleTextView)
        songTitleCom = binding.compactPlayerLayout.findViewById(R.id.songTitleTextViewMinimized)
        songArtistName  =  binding.expandedPlayerLayout.findViewById(R.id.songArtistTextView)

        albumCoverImageView  = binding.expandedPlayerLayout.findViewById(R.id.albumCoverImageView)

        requestPermissions()

        adapter = SongAdapter()
        adapter.setClickListener(this)

        val upBtn = binding.compactPlayerLayout.findViewById<ImageView>(R.id.upBtn)
        val backButton = binding.expandedPlayerLayout.findViewById<ImageView>(R.id.downCollapase)

        upBtn.setOnClickListener {
            binding.expandedPlayerLayout.startAnimation(slideUpAnimation)
            binding.compactPlayerLayout.visibility = View.GONE
            binding.expandedPlayerLayout.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            binding.expandedPlayerLayout.startAnimation(slideDownAnimation)

            binding.compactPlayerLayout.visibility = View.VISIBLE
            binding.expandedPlayerLayout.visibility = View.GONE
        }

        songseekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayerManager!!.seekTo(progress.toLong())
                    updateStartTimeTextView()
                    updateEndTimeTextView()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}


        })

        startUpdatingSeekBarProgress()


        playPauseEx.setOnClickListener {
            handlePlayPauseClick(playPauseEx)
        }

        playPauseCom.setOnClickListener {
            handlePlayPauseClick(playPauseCom)
        }

        previousEx.setOnClickListener {
            handlePreviousClick()
        }

        previousCom.setOnClickListener {
            handlePreviousClick()
        }

        nextEx.setOnClickListener {
            handleNextClick()
        }

        nextCom.setOnClickListener {
            handleNextClick()
        }

        volumeSeekBar.max = maxVolume
        currentVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currentVolumeLevel

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentVolumeLevel = progress
                setVolume(currentVolumeLevel)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        Log.d("MyFragment", "initUI success")

    }

    //----------------------------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFoc(): Boolean {
        if (!::audioFocRequest.isInitialized) {
            val requestBuilder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(AudioAttributes.Builder().apply {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                }.build())
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener, handler)
            }
            audioFocRequest = requestBuilder.build()
        }

        val result = audioManager.requestAudioFocus(audioFocRequest)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun releaseAudioFoc() {
        audioFocRequest?.let { request ->
            audioManager.abandonAudioFocusRequest(request)
        }
    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStop() {
//        super.onStop()
//        musicPlayerManager.releaseMediaPlayer()
//        releaseAudioFoc()
//    }

    //-------------------------------------------------------------------------------

    override fun onSongClicked(position: Int, song: SongModel) {
        Log.d("MyFragment", "Song clicked at position: $position")
        try {

            currentSongPosition = position
            musicPlayerManager?.playSong(
                song,
                playPauseEx,
                songTitle,
                songArtistName,
                albumCoverImageView
            )
            songseekBar.max = musicPlayerManager?.getDuration()!!.toInt()
            binding.compactPlayerLayout.visibility = View.GONE
            binding.expandedPlayerLayout.startAnimation(slideUpAnimation)

            binding.expandedPlayerLayout.visibility = View.VISIBLE
            updateUI()
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("MyFragment", "Error during onSongClicked", e)
        }

        // Запускаем MusicPlayerService с новым треком
//        musicPlayerServiceIntent.action = MusicPlayerService.ACTION_PLAY
//        musicPlayerServiceIntent.putExtra(MusicPlayerService.EXTRA_TRACK_URI, song.trackUri)
        requireContext().startService(musicPlayerServiceIntent)

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_PERMISSION_CODE)
        } else {
            // Permissions are granted prior to API level 23
            // Proceed with your code
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.showtheList(requireContext()).observe(viewLifecycleOwner, Observer {
                    adapter.setList(it!!)
                    binding.rvSongList.adapter = adapter
                })

                viewModel.fetchAudio(requireContext())


            } else {
                // Permission denied, handle accordingly (e.g., show a message or take appropriate action)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus(): Boolean {
        return audioFocusRequest?.let { request ->
            val focusRequest = request
            // Additional logic here if needed
            true // Return a Boolean value indicating success
        } ?: run {
            val requestBuilder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(AudioAttributes.Builder().apply {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                }.build())
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener, handler)
            }
            audioFocusRequest = requestBuilder.build()

            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun releaseAudioFocus() {
        audioFocusRequest?.let { request ->
            audioManager.abandonAudioFocusRequest(request)
            audioFocusRequest = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (musicPlayerManager!!.isPlaying()) {
                    musicPlayerManager!!.pauseSong(playPauseEx)
                    playPauseEx.setImageResource(R.drawable.play)
                    releaseAudioFocus()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (musicPlayerManager!!.isPlaying()) {
                    musicPlayerManager!!.pauseSong(playPauseEx)
                    playPauseEx.setImageResource(R.drawable.play)
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!musicPlayerManager!!.isPlaying()) {
                    musicPlayerManager!!.resumeSong(playPauseEx)
                    playPauseEx.setImageResource(R.drawable.pause)
                }
            }
        }
    }

    fun updateSeekBarProgress() {
        while (true) {
            try {
                if (musicPlayerManager!!.isPlaying()) {
                    val message = Message()
                    message.what = musicPlayerManager!!.getCurrentPosition().toInt()
                    handler.sendMessage(message)
                    Thread.sleep(200)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            songseekBar.progress = msg.what
            updateStartTimeTextView()
            updateEndTimeTextView()
        }
    }

    fun startUpdatingSeekBarProgress() {
        Thread {
            updateSeekBarProgress()
        }.start()
    }

    fun updateStartTimeTextView() {
        val currentTime = musicPlayerManager!!.getCurrentPosition()
        val startTime = formatTime(currentTime)
        progressForwadTv.text = startTime
    }

    fun updateEndTimeTextView() {
        val duration = musicPlayerManager!!.getDuration()
        val currentTime = musicPlayerManager!!.getCurrentPosition()
        val endTime = formatTime(duration - currentTime)
        songRemainingTv.text = "-$endTime"
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / (1000 * 60)
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun setVolume(volumeLevel: Int) {
        val volume = volumeLevel / 100f
        musicPlayerManager!!.setValume(volume, volume)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handlePlayPauseClick(button: ImageView) {

        if (musicPlayerManager!!.isPlaying()) {
            musicPlayerManager!!.pauseSong(button)
            button.setImageResource(R.drawable.play)
            releaseAudioFocus()
        } else {
            if (requestAudioFocus()) {
                musicPlayerManager!!.resumeSong(button)
                button.setImageResource(R.drawable.pause)
            }
        }

        updateUI()
    }

    private fun handlePreviousClick() {
        if (currentSongPosition > 0) {
            currentSongPosition--
        } else {
            currentSongPosition = adapter.itemCount - 1
        }
        val previousSong = adapter.getItem(currentSongPosition)
        musicPlayerManager!!.playSong(previousSong, playPauseEx, songTitle, songArtistName, albumCoverImageView)
        updateCompactPlayerUI()
    }

    private fun handleNextClick() {
        if (currentSongPosition < (adapter.itemCount - 1)) {
            currentSongPosition++
        } else {
            currentSongPosition = 0
        }
        val nextSong = adapter.getItem(currentSongPosition)
        musicPlayerManager!!.playSong(nextSong, playPauseEx, songTitle, songArtistName, albumCoverImageView)
        updateCompactPlayerUI()

    }

    private fun updateCompactPlayerUI() {
        updateCompactPlayerPlayPauseButton()
        updateCompactPlayerSongTitle()
    }

    fun updateUI() {
        updateStartTimeTextView()
        updateEndTimeTextView()
        updatePlayPauseButton()

        updateCompactPlayerPlayPauseButton()
        updateCompactPlayerSongTitle()
    }

    fun updatePlayPauseButton() {
        val isPlaying = musicPlayerManager!!.isPlaying()
        val playPauseButton = if (binding.expandedPlayerLayout.visibility == View.VISIBLE) playPauseEx else playPauseCom
        playPauseButton.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }

    private fun updateCompactPlayerPlayPauseButton() {
        val isPlaying = musicPlayerManager!!.isPlaying()
        playPauseCom.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.playwhite)
    }

    private fun updateCompactPlayerSongTitle() {
        val currentSong = adapter.getItem(currentSongPosition)
        songTitleCom.text = currentSong.title

        val textWidth = songTitleCom.paint.measureText(songTitleCom.text.toString())
        val screenWidth = requireContext().resources.displayMetrics.widthPixels.toFloat()

        val translateAnimation = TranslateAnimation(screenWidth, -textWidth, 0f, 0f)
        translateAnimation.duration = (textWidth / screenWidth * 10000).toLong() // Adjust the duration as per your preference
        translateAnimation.repeatCount = Animation.INFINITE
        translateAnimation.interpolator = LinearInterpolator()

        songTitleCom.startAnimation(translateAnimation)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        Log.d("MyFragment", "onStop")
        super.onStop()
        requireContext().stopService(musicPlayerServiceIntent)
        releaseAudioFoc()
        musicPlayerManager!!.releaseMediaPlayer()
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        Log.d("MyFragment", "onDestroy")
        super.onDestroy()
//        if (isServiceRunning(MusicPlayerService::class.java)) {
            requireContext().stopService(musicPlayerServiceIntent)
//        }

        musicPlayerManager?.releaseMediaPlayer()
    }

    override fun onSongCompletion() {
        Log.d("MyFragment", "onSongCompletion")
        handleNextClick()

    }
}