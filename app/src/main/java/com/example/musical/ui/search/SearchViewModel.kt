package com.example.musical.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    object Empty : SearchUiState()
    data class Results(val songs: List<Song>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository by lazy {
        val db = MusicalDatabase.getInstance(application)
        MusicRepository(db.songDao(), db.playlistDao())
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collect { q ->
                    if (q.isBlank()) {
                        _uiState.value = SearchUiState.Idle
                    } else {
                        performSearch(q)
                    }
                }
        }
    }

    private suspend fun performSearch(q: String) {
        _uiState.value = SearchUiState.Loading
        try {
            val results = repository.searchSongs(q)
            if (results.isEmpty() && _query.value.isNotBlank()) {
                _uiState.value = SearchUiState.Empty
            } else {
                _uiState.value = SearchUiState.Results(results)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = SearchUiState.Error(e.localizedMessage ?: "Failed to perform search. Check internet.")
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun retry() {
        val q = _query.value
        if (q.isNotBlank()) {
            viewModelScope.launch {
                performSearch(q)
            }
        }
    }
}
