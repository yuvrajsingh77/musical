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

    fun fetchData() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                // Fetch daily mix and trending songs in parallel or sequence
                val mix = repository.getDailyMix()
                val trending = repository.getTrendingSongs()
                
                if (mix.isEmpty() && trending.isEmpty()) {
                    _uiState.value = HomeUiState.Error("Failed to fetch songs. Please try again.")
                } else {
                    _dailyMix.value = mix
                    _trendingNow.value = trending
                    _uiState.value = HomeUiState.Success
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Unknown network error occurred.")
            }
        }
    }

    fun retry() {
        fetchData()
    }
}
