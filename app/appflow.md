# SPOTIFY-STYLE APP FLOW

## APPLICATION FLOW

```text
App Launch
│
├── Splash Screen
│
├── Check Authentication
│   │
│   ├── User Logged In
│   │      │
│   │      └── Home Screen
│   │
│   └── User Not Logged In
│          │
│          └── Onboarding
│                 │
│                 ├── Login
│                 ├── Sign Up
│                 └── Continue As Guest
```

---

# AUTHENTICATION FLOW

```text
Authentication
│
├── Login
│   │
│   ├── Email + Password
│   ├── Google Login
│   ├── Apple Login
│   └── Facebook Login
│
├── Forgot Password
│   │
│   ├── Enter Email
│   ├── OTP Verification
│   └── Create New Password
│
└── Registration
    │
    ├── Basic Information
    ├── Select Music Preferences
    ├── Select Favorite Genres
    ├── Select Artists
    └── Home Screen
```

---

# MAIN APPLICATION FLOW

```text
Home
│
├── Search
├── Library
├── Profile
└── Mini Player
```

Bottom Navigation

```text
Bottom Navigation
│
├── Home
├── Search
├── Library
└── Profile
```

---

# HOME FLOW

```text
Home
│
├── Recently Played
│   └── Open Player
│
├── Daily Mix
│   └── Playlist Detail
│
├── Recommended Songs
│   └── Song Detail
│
├── Popular Artists
│   └── Artist Profile
│
├── Podcasts
│   └── Podcast Detail
│
├── New Releases
│   └── Album Detail
│
└── Charts
    └── Playlist Detail
```

---

# SEARCH FLOW

```text
Search
│
├── Search Input
│
├── Recent Searches
│
├── Trending Searches
│
├── Browse Categories
│
├── Search Results
│   │
│   ├── Songs
│   ├── Artists
│   ├── Albums
│   ├── Podcasts
│   └── Playlists
│
└── Result Selection
    │
    ├── Song → Player
    ├── Artist → Artist Page
    ├── Album → Album Detail
    ├── Podcast → Podcast Page
    └── Playlist → Playlist Detail
```

---

# PLAYER FLOW

```text
Mini Player
│
└── Expand
      │
      └── Full Player
             │
             ├── Play
             ├── Pause
             ├── Next
             ├── Previous
             ├── Shuffle
             ├── Repeat
             ├── Lyrics
             ├── Queue
             ├── Like Song
             ├── Download Song
             ├── Share Song
             ├── Sleep Timer
             ├── Equalizer
             └── Device Selection
```

---

# PLAYLIST FLOW

```text
Playlist Detail
│
├── Play Playlist
├── Shuffle Playlist
├── Download Playlist
├── Share Playlist
├── Edit Playlist
├── Collaborate Playlist
│
└── Song List
      │
      └── Open Player
```

---

# ARTIST FLOW

```text
Artist Profile
│
├── Follow Artist
├── Popular Songs
│      └── Player
│
├── Albums
│      └── Album Detail
│
├── Singles
│      └── Single Detail
│
├── Music Videos
│
├── Events
│
└── Similar Artists
       └── Artist Profile
```

---

# PODCAST FLOW

```text
Podcast
│
├── Episode List
│
├── Continue Listening
│
├── Download Episode
│
├── Bookmark Episode
│
├── Transcript
│
└── Podcast Player
```

---

# LIBRARY FLOW

```text
Library
│
├── Liked Songs
│
├── Playlists
│
├── Albums
│
├── Artists
│
├── Downloads
│
├── History
│
└── Folders
```

---

# PROFILE FLOW

```text
Profile
│
├── Edit Profile
│
├── Listening Statistics
│
├── Premium Status
│
├── Subscription
│
├── Recently Played
│
└── Settings
```

---

# PREMIUM FLOW

```text
Premium
│
├── View Plans
│
├── Compare Plans
│
├── Select Plan
│
├── Payment Gateway
│
├── Success Page
│
└── Premium Activated
```

---

# SETTINGS FLOW

```text
Settings
│
├── Account
│
├── Playback
│
├── Audio Quality
│
├── Downloads
│
├── Notifications
│
├── Privacy
│
├── Security
│
├── Theme
│
├── Accessibility
│
├── Language
│
└── Logout
```

---

# COMPLETE USER JOURNEY

```text
Launch App
│
├── Splash
│
├── Authentication
│
├── Genre Selection
│
├── Artist Selection
│
├── Home
│
├── Search Music
│
├── Open Playlist
│
├── Play Song
│
├── Add To Liked Songs
│
├── Follow Artist
│
├── Create Playlist
│
├── Download Playlist
│
├── Purchase Premium
│
├── Manage Library
│
└── Continue Listening Daily
```

---

# NAVIGATION MAP

```text
HOME
├── Playlist
├── Album
├── Artist
├── Podcast
└── Player

SEARCH
├── Artist
├── Album
├── Playlist
├── Podcast
└── Player

LIBRARY
├── Playlist
├── Album
├── Downloads
└── Player

PROFILE
├── Settings
├── Premium
└── Statistics
```
