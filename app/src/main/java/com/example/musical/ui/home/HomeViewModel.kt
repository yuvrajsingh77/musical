package com.example.musical.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import com.example.musical.ui.library.toSong
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    private val _newReleases = MutableStateFlow<List<Song>>(emptyList())
    val newReleases: StateFlow<List<Song>> = _newReleases.asStateFlow()

    private val _topCharts = MutableStateFlow<List<Song>>(emptyList())
    val topCharts: StateFlow<List<Song>> = _topCharts.asStateFlow()

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
        Song("f1", "Tum Hi Ho", "Arijit Singh", "Aashiqui 2",
            "https://upload.wikimedia.org/wikipedia/en/6/61/Aashiqui_2_soundtrack.jpg",
            261000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
        Song("f2", "Kesariya", "Arijit Singh", "Brahmastra",
            "https://upload.wikimedia.org/wikipedia/en/3/3b/Kesariya_single_cover.jpg",
            263000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
        Song("f3", "Raataan Lambiyan", "Jubin Nautiyal", "Shershaah",
            "https://upload.wikimedia.org/wikipedia/en/6/6b/Shershaah_film_poster.jpg",
            237000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"),
        Song("f4", "Apna Bana Le", "Arijit Singh", "Bhediya",
            "https://upload.wikimedia.org/wikipedia/en/0/04/Bhediya_film_poster.jpg",
            258000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"),
        Song("f5", "Jhoome Jo Pathaan", "Arijit Singh", "Pathaan",
            "https://upload.wikimedia.org/wikipedia/en/6/6d/Pathaan_film_poster.jpg",
            203000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"),
        Song("f6", "Lutt Putt Gaya", "Arijit Singh", "Dunki",
            "https://upload.wikimedia.org/wikipedia/en/9/9f/Dunki_film_poster.jpg",
            318000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"),
        Song("f7", "Wahaan", "Jasleen Royal", "12th Fail",
            "https://upload.wikimedia.org/wikipedia/en/4/49/12th_Fail_film_poster.jpg",
            245000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3"),
        Song("f8", "Ik Vaari Aa", "Arijit Singh", "Raazi",
            "https://upload.wikimedia.org/wikipedia/en/c/c1/Raazi_2018_film_poster.jpg",
            294000, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3")
    )

    fun fetchData() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                coroutineScope {
                    val mixDeferred = async { repository.getDailyMix() }
                    val trendingDeferred = async { repository.getTrendingSongs() }
                    val newReleasesDeferred = async { repository.getNewReleases() }
                    val topChartsDeferred = async { repository.getTopCharts() }

                    val (mix, trending, newR, charts) = awaitAll(
                        mixDeferred, trendingDeferred, newReleasesDeferred, topChartsDeferred
                    )

                    _dailyMix.value = mix.ifEmpty { fallbackSongs }
                    _trendingNow.value = trending.ifEmpty { fallbackSongs.reversed() }
                    _newReleases.value = newR.ifEmpty { fallbackSongs.shuffled() }
                    _topCharts.value = charts.ifEmpty { fallbackSongs.shuffled() }
                }
                _uiState.value = HomeUiState.Success
            } catch (e: Exception) {
                _dailyMix.value = fallbackSongs
                _trendingNow.value = fallbackSongs.reversed()
                _newReleases.value = fallbackSongs.shuffled()
                _topCharts.value = fallbackSongs.shuffled()
                _uiState.value = HomeUiState.Success
            }
        }
    }

    fun retry() {
        fetchData()
    }
}
