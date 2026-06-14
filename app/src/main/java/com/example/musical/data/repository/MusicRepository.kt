package com.example.musical.data.repository

import com.example.musical.data.local.dao.PlaylistDao
import com.example.musical.data.local.dao.SongDao
import com.example.musical.data.local.entities.PlaylistEntity
import com.example.musical.data.local.entities.PlaylistSongCrossRef
import com.example.musical.data.local.entities.SongEntity
import com.example.musical.data.model.Song
import com.example.musical.data.remote.RetrofitInstance
import com.example.musical.data.remote.dto.toSong
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao
) {
    suspend fun searchSongs(query: String): List<Song> {
        return try {
            val response = RetrofitInstance.api.searchSongs(query)
            response.results
                ?.filter { !it.id.isNullOrEmpty() }
                ?.map { it.toSong() }
                ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getFullSong(song: Song): Song? {
        return try {
            // Use the direct URL from search result (points to original fast API)
            // This bypasses our slow Vercel Python deployment entirely
            val detail = if (!song.songDetailUrl.isNullOrEmpty()) {
                android.util.Log.d("MusicRepo", "Using direct URL: ${song.songDetailUrl}")
                RetrofitInstance.api.getSongFromDirectUrl(song.songDetailUrl)
            } else {
                android.util.Log.d("MusicRepo", "Falling back to /song?id=${song.id}")
                RetrofitInstance.api.getSongById(song.id)
            }

            val streamUrl = detail.mediaUrls?.kbps320
                ?: detail.mediaUrls?.kbps160
                ?: detail.mediaUrls?.kbps96
                ?: detail.mediaUrl

            android.util.Log.d("MusicRepo", "Got stream URL: $streamUrl")

            if (!streamUrl.isNullOrEmpty()) {
                detail.toSong().copy(songDetailUrl = song.songDetailUrl)
            } else {
                android.util.Log.w("MusicRepo", "No stream URL found in response!")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicRepo", "getFullSong failed: ${e.message}")
            null
        }
    }

    suspend fun getLyrics(songId: String): String? {
        return try {
            RetrofitInstance.api.getLyrics(songId).lyrics
        } catch (e: Exception) { null }
    }

    suspend fun getTrendingSongs(): List<Song> = searchSongs("top Bollywood 2024")
    suspend fun getDailyMix(): List<Song> = searchSongs("Arijit Singh hits")
    suspend fun getNewReleases(): List<Song> = searchSongs("new Hindi songs 2024")
    suspend fun getTopCharts(): List<Song> = searchSongs("Punjabi hits 2024")

    fun getLikedSongs(): Flow<List<SongEntity>> {
        return songDao.getLikedSongs()
    }

    fun getRecentlyPlayed(): Flow<List<SongEntity>> {
        return songDao.getRecentlyPlayed()
    }

    suspend fun likeSong(song: Song) {
        // Ensure the song exists in the database first
        val existing = songDao.getSongById(song.id)
        if (existing == null) {
            songDao.insertSong(
                SongEntity(
                    id = song.id,
                    title = song.title,
                    artist = song.artist,
                    album = song.album,
                    artworkUrl = song.artworkUrl,
                    durationMs = song.durationMs,
                    streamUrl = song.streamUrl,
                    isLiked = true
                )
            )
        } else {
            songDao.toggleLike(song.id)
        }
    }

    suspend fun unlikeSong(song: Song) {
        val existing = songDao.getSongById(song.id)
        if (existing != null && existing.isLiked) {
            songDao.toggleLike(song.id)
        }
    }

    suspend fun isSongLiked(songId: String): Boolean {
        return songDao.getSongById(songId)?.isLiked ?: false
    }

    suspend fun markAsPlayed(song: Song) {
        val existing = songDao.getSongById(song.id)
        if (existing == null) {
            songDao.insertSong(
                SongEntity(
                    id = song.id,
                    title = song.title,
                    artist = song.artist,
                    album = song.album,
                    artworkUrl = song.artworkUrl,
                    durationMs = song.durationMs,
                    streamUrl = song.streamUrl,
                    lastPlayedAt = System.currentTimeMillis()
                )
            )
        } else {
            songDao.updateLastPlayed(song.id, System.currentTimeMillis())
        }
    }

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.createPlaylist(PlaylistEntity(name = name))
    }

    suspend fun addToPlaylist(songId: String, playlistId: Long) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId))
    }

    fun getPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getPlaylists()
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<SongEntity>> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}
