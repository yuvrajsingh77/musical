package com.example.musical.data.model

object SongsRepository {
    val dummySongs = listOf(
        Song(
            id = "1",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            artworkUrl = "https://picsum.photos/seed/song1/300/300",
            durationMs = 200000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Song(
            id = "2",
            title = "Shape of You",
            artist = "Ed Sheeran",
            album = "Divide",
            artworkUrl = "https://picsum.photos/seed/song2/300/300",
            durationMs = 234000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Song(
            id = "3",
            title = "Levitating",
            artist = "Dua Lipa",
            album = "Future Nostalgia",
            artworkUrl = "https://picsum.photos/seed/song3/300/300",
            durationMs = 203000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
    )

    fun getSongById(id: String?): Song? {
        return dummySongs.find { it.id == id }
    }
}
