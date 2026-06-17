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
            "https://i1.sndcdn.com/artworks-000057089738-tbeker-t500x500.jpg",
            261000, null),
        Song("f2", "Kesariya", "Arijit Singh", "Brahmastra",
            "https://i1.sndcdn.com/artworks-O8qABODgGHTo-0-t500x500.jpg",
            263000, null),
        Song("f3", "Raataan Lambiyan", "Jubin Nautiyal", "Shershaah",
            "https://i1.sndcdn.com/artworks-vNGjFbnWWCjo-0-t500x500.jpg",
            237000, null),
        Song("f4", "Apna Bana Le", "Arijit Singh", "Bhediya",
            "https://i1.sndcdn.com/artworks-P2IGDFgbXOCJ-0-t500x500.jpg",
            258000, null),
        Song("f5", "Jhoome Jo Pathaan", "Arijit Singh", "Pathaan",
            "https://i1.sndcdn.com/artworks-7DKPcDhGWfJF-0-t500x500.jpg",
            203000, null)
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
