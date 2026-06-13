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
            if (response.success) {
                response.data.results.map { it.toSong() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTrendingSongs(): List<Song> {
        return searchSongs("trending hindi 2024")
    }

    suspend fun getDailyMix(): List<Song> {
        return searchSongs("top hits")
    }

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
