package com.example.musical.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.local.entities.PlaylistEntity
import com.example.musical.data.local.entities.SongEntity
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository by lazy {
        val db = MusicalDatabase.getInstance(application)
        MusicRepository(db.songDao(), db.playlistDao())
    }

    val likedSongs: StateFlow<List<Song>> = repository.getLikedSongs()
        .map { list -> list.map { it.toSong() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentlyPlayed: StateFlow<List<Song>> = repository.getRecentlyPlayed()
        .map { list -> list.map { it.toSong() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playlists: StateFlow<List<PlaylistEntity>> = repository.getPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }
}

fun SongEntity.toSong() = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    artworkUrl = artworkUrl,
    durationMs = durationMs,
    streamUrl = streamUrl
)
