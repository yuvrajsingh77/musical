package com.example.musical.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.local.entities.PlaylistEntity
import com.example.musical.data.local.entities.SongEntity
import com.example.musical.data.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository by lazy {
        val db = MusicalDatabase.getInstance(application)
        MusicRepository(db.songDao(), db.playlistDao())
    }

    private val _playlistName = MutableStateFlow("Playlist")
    val playlistName: StateFlow<String> = _playlistName.asStateFlow()

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = repository.getPlaylistById(playlistId)
            _playlistName.value = playlist?.name ?: "Playlist"
            repository.getSongsInPlaylist(playlistId).collect { songs ->
                _songs.value = songs
                _isLoading.value = false
            }
        }
    }

    fun removeSong(playlistId: Long, songId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(playlistId)
        }
    }
}
