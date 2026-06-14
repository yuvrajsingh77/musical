package com.example.musical.ui.player

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import com.example.musical.service.MusicService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RepeatMode {
    object Off : RepeatMode()
    object One : RepeatMode()
    object All : RepeatMode()
}

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var pendingQueue: Pair<List<Song>, Int>? = null

    private val repository: MusicRepository by lazy {
        val db = MusicalDatabase.getInstance(application)
        MusicRepository(db.songDao(), db.playlistDao())
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _song = MutableStateFlow<Song?>(null)
    val song: StateFlow<Song?> = _song.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled.asStateFlow()

    private val _repeatMode = MutableStateFlow<RepeatMode>(RepeatMode.Off)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()


    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val player = controller ?: return
            val index = player.currentMediaItemIndex
            _currentIndex.value = index
            if (index >= 0 && index < _queue.value.size) {
                val currentSong = _queue.value[index]
                _song.value = currentSong
                viewModelScope.launch {
                    _isLiked.value = repository.isSongLiked(currentSong.id)
                }
            } else if (mediaItem != null) {
                val uri = mediaItem.localConfiguration?.uri?.toString()
                val title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown Song"
                val artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist"
                val artworkUrl = mediaItem.mediaMetadata.artworkUri?.toString() ?: ""
                val currentSong = Song(
                    id = uri ?: "",
                    title = title,
                    artist = artist,
                    album = "Unknown Album",
                    artworkUrl = artworkUrl,
                    durationMs = player.duration.toInt(),
                    streamUrl = uri
                )
                _song.value = currentSong
                viewModelScope.launch {
                    _isLiked.value = repository.isSongLiked(currentSong.id)
                }
            }
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
        }
    }

    init {
        val sessionToken = SessionToken(application, ComponentName(application, MusicService::class.java))
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                val player = controllerFuture?.get()
                controller = player
                // Apply any queued songs that came in before controller was ready
                pendingQueue?.let { (songs, index) ->
                    player?.let { applyQueue(it, songs, index) }
                    pendingQueue = null
                }
                player?.addListener(playerListener)
                
                // Sync initial states if controller is already playing
                player?.let {
                    _isPlaying.value = it.isPlaying
                    _currentPositionMs.value = it.currentPosition.toInt()
                    _isShuffled.value = it.shuffleModeEnabled
                    _repeatMode.value = when (it.repeatMode) {
                        Player.REPEAT_MODE_ONE -> RepeatMode.One
                        Player.REPEAT_MODE_ALL -> RepeatMode.All
                        else -> RepeatMode.Off
                    }
                    val index = it.currentMediaItemIndex
                    if (index >= 0 && index < _queue.value.size) {
                        _song.value = _queue.value[index]
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())

        viewModelScope.launch {
            var lastTrackId: String? = null
            while (true) {
                controller?.let { player ->
                    _currentPositionMs.value = player.currentPosition.toInt()
                    _isPlaying.value = player.isPlaying

                    if (player.isPlaying) {
                        _song.value?.let { currentSong ->
                            if (lastTrackId != currentSong.id) {
                                lastTrackId = currentSong.id
                                repository.markAsPlayed(currentSong)
                            }
                        }
                    }
                }
                delay(500)
            }
        }
    }

    fun setSong(song: Song) {
        if (_song.value?.id == song.id) return
        setQueue(listOf(song), 0)
    }

    fun setQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, songs.size - 1)
        _queue.value = songs
        _currentIndex.value = safeIndex
        _song.value = songs[safeIndex]

        viewModelScope.launch {
            val targetSong = songs[safeIndex]
            android.util.Log.d("PlayerViewModel",
                "Fetching full song for: ${targetSong.title}, detailUrl: ${targetSong.songDetailUrl}")

            val fullSong = try {
                val fetched = repository.getFullSong(targetSong)
                android.util.Log.d("PlayerViewModel",
                    "Full song result: ${fetched?.title}, streamUrl: ${fetched?.streamUrl}")
                fetched?.takeIf { !it.streamUrl.isNullOrEmpty() } ?: run {
                    android.util.Log.w("PlayerViewModel",
                        "No full song found, using fallback vlink: ${targetSong.streamUrl}")
                    targetSong
                }
            } catch (e: Exception) {
                android.util.Log.e("PlayerViewModel", "setQueue failed: ${e.message}")
                targetSong
            }

            val updated = _queue.value.toMutableList()
            updated[safeIndex] = fullSong
            _queue.value = updated
            _song.value = fullSong

            val player = controller
            if (player == null) {
                pendingQueue = Pair(updated, safeIndex)
            } else {
                applyQueue(player, updated, safeIndex)
            }

            // Pre-fetch next song in background
            if (safeIndex + 1 < songs.size) {
                try {
                    val next = songs[safeIndex + 1]
                    val nextFull = repository.getFullSong(next)
                    if (nextFull != null && !nextFull.streamUrl.isNullOrEmpty()) {
                        val q = _queue.value.toMutableList()
                        if (safeIndex + 1 < q.size) {
                            q[safeIndex + 1] = nextFull
                            _queue.value = q
                        }
                    }
                } catch (e: Exception) { }
            }
        }
    }

    fun fetchLyrics() {
        val songId = _song.value?.id ?: return
        viewModelScope.launch {
            _lyrics.value = repository.getLyrics(songId)
        }
    }

    private fun applyQueue(player: MediaController, songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.mapNotNull { song ->
            val uri = song.streamUrl ?: return@mapNotNull null
            MediaItem.Builder()
                .setUri(uri)
                .setMimeType("audio/mp4")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setArtworkUri(Uri.parse(song.artworkUrl))
                        .build()
                )
                .build()
        }
        if (mediaItems.isEmpty()) return
        player.setMediaItems(mediaItems)
        player.seekToDefaultPosition(startIndex)
        player.prepare()
        player.play()
        viewModelScope.launch {
            _isLiked.value = repository.isSongLiked(songs[startIndex].id)
        }
    }

    fun skipNext() {
        controller?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    fun toggleShuffle() {
        val player = controller ?: return
        val nextShuffle = !_isShuffled.value
        _isShuffled.value = nextShuffle
        player.shuffleModeEnabled = nextShuffle
    }

    fun toggleRepeat() {
        val player = controller ?: return
        val nextMode = when (_repeatMode.value) {
            RepeatMode.Off -> RepeatMode.One
            RepeatMode.One -> RepeatMode.All
            RepeatMode.All -> RepeatMode.Off
        }
        _repeatMode.value = nextMode
        player.repeatMode = when (nextMode) {
            RepeatMode.Off -> Player.REPEAT_MODE_OFF
            RepeatMode.One -> Player.REPEAT_MODE_ONE
            RepeatMode.All -> Player.REPEAT_MODE_ALL
        }
    }

    fun toggleLike() {
        val currentSong = _song.value ?: return
        val currentlyLiked = _isLiked.value
        viewModelScope.launch {
            if (currentlyLiked) {
                repository.unlikeSong(currentSong)
            } else {
                repository.likeSong(currentSong)
            }
            _isLiked.value = !currentlyLiked
        }
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun seekTo(positionMs: Int) {
        controller?.seekTo(positionMs.toLong())
    }

    override fun onCleared() {
        super.onCleared()
        controller?.removeListener(playerListener)
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }
        controller = null
    }
}
