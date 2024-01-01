package com.example.joymp3.ui.fragments.song

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.joymp3.data.model.SongModel
import com.example.joymp3.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val audioS = MutableLiveData<List<SongModel>>()
    val audioK: LiveData<List<SongModel>> get() = audioS

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    @RequiresApi(Build.VERSION_CODES.R)
    fun fetchAudio(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAudioFiles(context)

            withContext(Dispatchers.Main){
                audioS.value = result.value
            }
        }
    }

    //clear
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun showtheList(context: Context) : LiveData<List<SongModel>> {
        return repository.getAudioFiles(context)
    }
}