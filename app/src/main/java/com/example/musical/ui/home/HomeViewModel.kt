package com.example.musical.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import com.example.musical.ui.library.toSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository by lazy {
        val db = MusicalDatabase.getInstance(application)
        MusicRepository(db.songDao(), db.playlistDao())
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _dailyMix = MutableStateFlow<List<Song>>(emptyList())
    val dailyMix: StateFlow<List<Song>> = _dailyMix.asStateFlow()

    private val _trendingNow = MutableStateFlow<List<Song>>(emptyList())
    val trendingNow: StateFlow<List<Song>> = _trendingNow.asStateFlow()

    val recentlyPlayed: StateFlow<List<Song>> = repository.getRecentlyPlayed()
        .map { list -> list.map { it.toSong() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        fetchData()
    }

    private val fallbackSongs = listOf(
        Song("f1","Tum Hi Ho","Arijit Singh","Aashiqui 2",
            "https://picsum.photos/seed/s1/300/300", 261000,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
        Song("f2","Kesariya","Arijit Singh","Brahmastra",
            "https://picsum.photos/seed/s2/300/300", 284000,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
        Song("f3","Raataan Lambiyan","Jubin Nautiyal","Shershaah",
            "https://picsum.photos/seed/s3/300/300", 237000,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"),
        Song("f4","Apna Bana Le","Arijit Singh","Bhediya",
            "https://picsum.photos/seed/s4/300/300", 258000,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"),
        Song("f5","Jhoome Jo Pathaan","Arijit Singh","Pathaan",
            "https://picsum.photos/seed/s5/300/300", 203000,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3")
    )

    fun fetchData() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val mix = repository.getDailyMix()
                val trending = repository.getTrendingSongs()
                _dailyMix.value = mix.ifEmpty { fallbackSongs }
                _trendingNow.value = trending.ifEmpty { fallbackSongs.reversed() }
                _uiState.value = HomeUiState.Success
            } catch (e: Exception) {
                _dailyMix.value = fallbackSongs
                _trendingNow.value = fallbackSongs.reversed()
                _uiState.value = HomeUiState.Success
            }
        }
    }

    fun retry() {
        fetchData()
    }
}
