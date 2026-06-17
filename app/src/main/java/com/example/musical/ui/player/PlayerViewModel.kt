package com.example.musical.ui.player

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musical.data.local.MusicalDatabase
import com.example.musical.data.model.Song
import com.example.musical.data.repository.MusicRepository
import com.example.musical.service.MusicService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed class RepeatMode {
    object Off : RepeatMode()
    object One : RepeatMode()
    object All : RepeatMode()
}

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var pendingQueue: Pair<List<Song>, Int>? = null
    private val queueMutex = Mutex()
    private var currentQueueJob: Job? = null
    private var positionUpdateJob: Job? = null

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                _currentPositionMs.value = controller?.currentPosition?.toInt() ?: 0
                delay(500)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val player = controller ?: return
            val index = player.currentMediaItemIndex
            _currentIndex.value = index
            if (index >= 0 && index < _queue.value.size) {
                val currentSong = _queue.value[index]
                _song.value = currentSong
                viewModelScope.launch(Dispatchers.IO) {
                    repository.markAsPlayed(currentSong)
                    _isLiked.value = repository.isSongLiked(currentSong.id)
                }
            }
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
            if (playing) startPositionUpdates() else stopPositionUpdates()
        }

        override fun onPlaybackStateChanged(state: Int) {
            val stateStr = when(state) {
                Player.STATE_IDLE -> "IDLE"
                Player.STATE_BUFFERING -> "BUFFERING"
                Player.STATE_READY -> "READY"
                Player.STATE_ENDED -> "ENDED"
                else -> "UNKNOWN"
            }
            android.util.Log.d("PlayerState", "State changed: $stateStr")
        }

        override fun onPlayerError(error: PlaybackException) {
            android.util.Log.e("PlayerError", "Code: ${error.errorCode}, Msg: ${error.message}")
            
            when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> {
                    // URL expired — refresh it
                    val currentSong = _song.value ?: return
                    viewModelScope.launch {
                        val refreshed = repository.getFullSong(currentSong)
                        refreshed?.streamUrl?.let { newUrl ->
                            val newMediaItem = MediaItem.Builder()
                                .setUri(newUrl)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(currentSong.title)
                                        .setArtist(currentSong.artist)
                                        .setArtworkUri(Uri.parse(currentSong.artworkUrl))
                                        .build()
                                )
                                .build()
                            
                            val position = controller?.currentPosition ?: 0
                            controller?.setMediaItem(newMediaItem, position)
                            controller?.prepare()
                            controller?.play()
                        }
                    }
                }
                else -> {
                    // Other error — propagate or handle
                    _errorMessage.value = "Playback error: ${error.message}"
                }
            }
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
                    if (it.isPlaying) {
                        startPositionUpdates()
                    }
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
    }

    fun setSong(song: Song) {
        if (_song.value?.id == song.id) return
        setQueue(listOf(song), 0)
    }

    fun setQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        // Cancel any in-progress queue fetch
        currentQueueJob?.cancel()
        currentQueueJob = viewModelScope.launch {
            val safeIndex = startIndex.coerceIn(0, songs.size - 1)
            queueMutex.withLock {
                _queue.value = songs
                _currentIndex.value = safeIndex
                _song.value = songs[safeIndex]
            }

            val targetSong = songs[safeIndex]
            android.util.Log.d("PlayerViewModel",
                "Fetching full song: ${targetSong.title}, url: ${targetSong.songDetailUrl}")

            val fullSong = try {
                val fetched = repository.getFullSong(targetSong)
                android.util.Log.d("PlayerViewModel", "getFullSong result: $fetched")
                fetched?.takeIf { !it.streamUrl.isNullOrEmpty() } ?: targetSong
            } catch (e: Exception) {
                android.util.Log.e("PlayerViewModel", "getFullSong failed: ${e.message}")
                targetSong
            }

            if (!isActive) return@launch // Job was cancelled, don't apply stale data

            queueMutex.withLock {
                val updated = _queue.value.toMutableList()
                if (safeIndex < updated.size) {
                    updated[safeIndex] = fullSong
                    _queue.value = updated
                    _song.value = fullSong
                }
            }

            android.util.Log.d("PlayerViewModel",
                "Playing: ${fullSong.title} | streamUrl: ${fullSong.streamUrl}")

            val player = controller
            if (player == null) {
                pendingQueue = Pair(_queue.value, safeIndex)
            } else {
                applyQueue(player, _queue.value, safeIndex)
            }

            // Pre-fetch next song only if job not cancelled
            if (isActive && safeIndex + 1 < songs.size) {
                try {
                    val next = songs[safeIndex + 1]
                    val nextFull = repository.getFullSong(next)
                    if (isActive && nextFull != null && !nextFull.streamUrl.isNullOrEmpty()) {
                        queueMutex.withLock {
                            val q = _queue.value.toMutableList()
                            if (safeIndex + 1 < q.size) {
                                q[safeIndex + 1] = nextFull
                                _queue.value = q
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PlayerViewModel", "Pre-fetch failed: ${e.message}")
                }
            }
        }
    }

    fun fetchLyrics() {
        _lyrics.value = null
    }

    private fun applyQueue(player: MediaController, songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.mapNotNull { song ->
            val uri = song.streamUrl ?: return@mapNotNull null
            MediaItem.Builder()
                .setUri(uri)
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
        stopPositionUpdates()
        currentQueueJob?.cancel()
        controller?.removeListener(playerListener)
        controller = null
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }
        controllerFuture = null
    }
}
