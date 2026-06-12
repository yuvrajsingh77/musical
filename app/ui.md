HOME SCREEN

LAYOUT STRUCTURE

Header
├── Greeting Text
├── User Avatar
└── Notification Icon

Search Section
├── Search Input
└── Voice Search Button

Content Section
├── Recently Played
│   ├── Horizontal Scroll
│   └── Album Cards
├── Daily Mix
├── Trending Now
├── Popular Artists
└── Podcasts

Bottom Navigation
├── Home
├── Search
├── Library
└── Profile


## GLOBAL RULES

Platform: Android

Design Style:

* Dark Theme First
* Premium
* Minimal
* Spotify Inspired

Spacing System:

* 4px
* 8px
* 12px
* 16px
* 24px
* 32px

Corner Radius:

* Cards: 16px
* Buttons: 12px
* Inputs: 14px

Animations:

* 300ms duration
* Ease In Out
* GPU Accelerated

---

# APP STRUCTURE

App
├── Splash
├── Onboarding
├── Authentication
├── Home
├── Search
├── Library
├── Player
├── Playlist
├── Artist
├── Premium
├── Profile
└── Settings

---

# SPLASH SCREEN

Screen
├── Background
├── Animated Logo
└── Loading Animation

Behavior
├── Fade In
├── Scale Animation
└── Navigate To Home

---

# HOME SCREEN

HomeScreen
├── Header
├── SearchBar
├── QuickActions
├── RecentlyPlayed
├── DailyMixes
├── TrendingMusic
├── RecommendedSongs
├── TopArtists
├── Podcasts
├── Genres
├── NewReleases
├── Charts
├── ContinueListening
└── BottomNavigation

Header
├── Greeting
├── Avatar
└── NotificationButton

SearchBar
├── SearchInput
└── VoiceButton

RecentlyPlayed
├── HorizontalScroll
└── AlbumCards

AlbumCard
├── CoverImage
├── SongTitle
└── ArtistName

BottomNavigation
├── Home
├── Search
├── Library
└── Profile

---

# SEARCH SCREEN

SearchScreen
├── SearchInput
├── VoiceSearch
├── RecentSearches
├── TrendingSearches
├── Categories
└── Results

Results
├── Songs
├── Artists
├── Albums
├── Podcasts
└── Playlists

---

# PLAYER SCREEN

PlayerScreen
├── DynamicBackground
├── AlbumArt
├── SongInformation
├── ProgressBar
├── PlaybackControls
├── Lyrics
├── Queue
├── DeviceSelection
└── AdditionalActions

PlaybackControls
├── Shuffle
├── Previous
├── PlayPause
├── Next
└── Repeat

AdditionalActions
├── Like
├── Download
├── Share
├── Equalizer
└── SleepTimer

---

# LIBRARY SCREEN

Library
├── LikedSongs
├── Playlists
├── Artists
├── Albums
├── Downloads
├── History
└── Folders

---

# PLAYLIST SCREEN

PlaylistScreen
├── PlaylistCover
├── PlaylistInformation
├── PlayButton
├── ShuffleButton
├── SongList
└── MoreActions

SongList
├── SongImage
├── SongTitle
├── ArtistName
└── Duration

---

# ARTIST SCREEN

ArtistScreen
├── ArtistBanner
├── ArtistImage
├── FollowButton
├── MonthlyListeners
├── PopularTracks
├── Albums
├── Singles
├── Videos
└── SimilarArtists

---

# PROFILE SCREEN

Profile
├── UserAvatar
├── Username
├── ListeningStats
├── SubscriptionStatus
├── ActivityHistory
└── SettingsButton

---

# SETTINGS SCREEN

Settings
├── Account
├── Playback
├── Downloads
├── AudioQuality
├── Notifications
├── Privacy
├── Security
├── Theme
├── Accessibility
└── About

---

# DESIGN TOKENS

Colors
├── Primary: #1DB954
├── Background: #000000
├── Surface: #121212
├── Card: #181818
├── TextPrimary: #FFFFFF
└── TextSecondary: #B3B3B3

Typography
├── Display
├── Heading
├── Title
├── Body
└── Caption

Icons
├── Material Symbols Rounded
└── Consistent Stroke Width

---

# UX RULES

Every button must have:

* Hover State
* Press State
* Disabled State

Every screen must have:

* Loading State
* Error State
* Empty State
* Success State

Every list must support:

* Pull To Refresh
* Infinite Scroll

Every navigation transition:

* 60 FPS
* Shared Element Animation

Accessibility:

* WCAG AA
* Dynamic Font Scaling
* Screen Reader Support
* High Contrast Support

OUTPUT REQUIREMENT

Generate complete production-ready Flutter/Jetpack Compose UI from this architecture.
