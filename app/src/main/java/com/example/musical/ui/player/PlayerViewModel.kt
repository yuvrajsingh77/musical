package com.example.musical.ui.player

import androidx.lifecycle.ViewModel
import com.example.musical.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel : ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _song = MutableStateFlow<Song?>(null)
    val song: StateFlow<Song?> = _song.asStateFlow()

    fun setSong(song: Song) {
        _song.value = song
    }

    fun play() {
        _isPlaying.value = true
    }

    fun pause() {
        _isPlaying.value = false
    }

    fun seekTo(positionMs: Int) {
        _currentPositionMs.value = positionMs
    }
}
