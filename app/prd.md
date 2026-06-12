MELODIX — Product Requirements Document & Technical Requirements Document



Document Control
Version: 1.0.0 | Date: June 12, 2026 | Status: Draft for Review
Prepared By: Senior Product & Architecture Team (AI-Assisted)




EXECUTIVE SUMMARY

FieldDetailsProject NameMelodixVersion1.0.0Document DateJune 12, 2026Prepared ByProduct & Architecture TeamDocument TypePRD + TRD (Combined)

Project Vision:
Melodix is a full-featured, open-stack music streaming platform that delivers a Spotify-grade listening experience — personalised playlists, AI-powered discovery, social features, offline playback, and high-quality audio — powered entirely by free, legal, unofficial APIs for audio streaming, with zero per-stream licensing cost to the operator.

Problem Being Solved:
Existing music streaming services (Spotify, Apple Music, YouTube Music) charge users ₹119–₹299/month for premium features and impose strict geo-restrictions in South Asia. Indie developers and smaller teams cannot afford the licensing overhead to build competing platforms. Yet high-quality, unofficial streaming APIs exist that serve 320kbps audio at no cost, leaving a massive gap between raw infrastructure capability and a polished product built on top of it.

Target Market:


Primary: Indian music listeners aged 16–35 (550M+ smartphone users)
Secondary: South-East Asian markets underserved by Western streaming platforms
Tertiary: Open-source / self-hosted music enthusiast communities globally


Expected Impact:


100,000 registered users within 12 months of launch
DAU/MAU ratio ≥ 35% (benchmark: Spotify ~28%)
Zero direct streaming licensing cost to the platform operator
Developer-friendly codebase that can be white-labelled or extended by teams


Business Opportunity:


Freemium model with ad-supported free tier and a ₹49/month premium tier
B2B white-label licensing to regional OTT operators
Data-driven music recommendation engine as a standalone SaaS product



⚠️ ASSUMPTION (Legal): This document assumes Melodix operates as a personal/educational project or within the jurisdictional grey area where unofficial API consumption is not explicitly prohibited. The team must obtain independent legal counsel before any commercial launch. All streaming is sourced from existing licensed catalogues (JioSaavn); Melodix does not host any audio files.




MARKET ANALYSIS

Industry Overview

The global music streaming market was valued at USD 46.6B in 2025 and is projected to grow at a CAGR of 14.7% through 2030. India alone accounts for over 200M active music streaming users, making it the third-largest market by volume. The shift from downloads to streams is complete; the next battleground is personalisation, social features, and local-language content.

Existing Solutions & Competitor Analysis

FeatureSpotifyYouTube MusicJioSaavnGaanaMelodixFree Tier✅ (ads)✅ (ads)✅ (ads)✅ (ads)✅ (ads)320kbps Free❌❌❌❌✅Offline Free❌❌❌❌✅ (limited)AI Playlists✅❌❌❌✅Open Source❌❌❌❌✅India Pricing₹119/mo₹89/mo₹99/mo₹99/mo₹49/moLyrics✅✅✅❌✅Social✅LimitedLimited❌✅Lossless✅ (select)❌❌❌Roadmap

Current Limitations


Cost Barrier: Spotify Premium India = ₹119/month, far above median disposable income for students
Geo-restrictions: Many Spotify features (DJ, certain podcasts) are unavailable in India
No 320kbps on free tiers: Every major platform caps free-tier audio quality
Locked ecosystems: No open APIs, no self-hosting, no white-labelling
Limited vernacular support: Indian regional language interfaces are afterthoughts


Market Gap

A feature-complete streaming client built atop JioSaavn's unofficial API occupies a unique gap: it can serve 320kbps audio for free to every user, offer a premium tier at ₹49/month purely for UX perks (offline, no ads, advanced features), and operate with near-zero per-user variable cost.

Unique Value Proposition


"Spotify-quality music experience. Free, high-quality audio for everyone. ₹49/month for power users. Built on open infrastructure."




PRODUCT REQUIREMENTS DOCUMENT (PRD)


1. Product Overview

Purpose

Melodix is a cross-platform music streaming application offering high-quality audio playback, AI-powered discovery, personalised playlists, social listening, offline downloads, and rich metadata — all sourced through the JioSaavn unofficial API with supplemental metadata from Last.fm, MusicBrainz, and TheAudioDB.

Objectives


Deliver a 10/10 listening experience rivalling Spotify on mobile and web
Provide 320kbps audio streaming to all users, free and premium
Offer AI-powered playlist generation and music discovery
Enable social features: shared playlists, friend activity feeds, collaborative listening
Achieve offline playback through encrypted local caching
Build a maintainable, scalable open-source codebase


Scope

In Scope (v1.0):


Web app (React PWA), Android app, iOS app
Audio streaming via JioSaavn API
User authentication, profiles, library management
Search, discovery, AI playlists, curated moods
Offline downloads (premium)
Social: friend activity, collaborative playlists, sharing
Lyrics (synced), artist metadata, album art
Analytics dashboard for users (Wrapped equivalent)


Out of Scope (v1.0):


Podcast streaming
Audiobook support
Music video playback
Live radio
Desktop native apps (Windows/macOS)
Lossless audio


Success Definition

MetricTarget (12 months)Registered Users100,000DAU35,000Premium Conversion8%Session Length≥25 min/dayApp Store Rating≥4.3Crash-Free Rate≥99.5%P95 Stream Start Time<1.2s


2. Problem Statement

Current Situation

Music lovers in India and South-East Asia are forced to choose between poor-quality free tiers (128kbps, ads every 2 songs, no offline) or subscription fees that represent 2–5% of a student's monthly budget.

User Pain Points


Cannot afford ₹119/month for Spotify Premium
Free tiers throttle audio to 128kbps — perceptibly poor on earphones
Cannot listen offline on public transport without mobile data
Discovery algorithms feel stale after 3–6 months of use
No way to listen collaboratively with friends in real-time
Lyrics are paywalled on most platforms


Business Pain Points


No white-label music client available for regional OTT operators
Existing platforms do not expose APIs for third-party integrations
Music data/metadata pipelines are expensive to build from scratch


Consequences of Existing Solutions

Users resort to illegal MP3 download sites (exposing themselves to malware), piracy apps, or simply listen to lower-quality audio daily — both outcomes that reduce artist discovery and harm the ecosystem.


3. Target Audience

Persona 1 — The College Student

FieldDetailsNameArjun, 20OccupationEngineering student, DelhiGoalsListen to music all day without ads; discover new songs; share playlists with friendsPain PointsCan't afford Spotify Premium; data is expensive; hostel Wi-Fi unreliableTechnical SkillMedium — comfortable with apps, not technicalUsage FrequencyDaily, 3–5 hours

Persona 2 — The Working Professional

FieldDetailsNamePriya, 28OccupationMarketing Manager, BengaluruGoalsBackground music at work; curated playlists by mood; podcast-like song narrationPain PointsSpotify recommendations feel repetitive; wants something smarterTechnical SkillMedium-highUsage FrequencyDaily, 1–2 hours

Persona 3 — The Audiophile

FieldDetailsNameRahul, 32OccupationSound Engineer, MumbaiGoals320kbps minimum; EQ control; gapless playback; metadata accuracyPain PointsJioSaavn's own app has poor UX; metadata is often wrongTechnical SkillHighUsage FrequencyDaily, 5+ hours

Persona 4 — The Social Listener

FieldDetailsNameSneha, 22OccupationInfluencer / Content Creator, HyderabadGoalsShare what she's listening to; create collaborative playlists; embed music in contentPain PointsSpotify's social features are geo-limited; no easy "listen together" featureTechnical SkillHighUsage FrequencyDaily, 2–3 hours

Persona 5 — The Vernacular Listener

FieldDetailsNameKavitha, 45OccupationHomemaker, ChennaiGoalsBrowse Tamil, Telugu, and Kannada music easily; simple UIPain PointsJioSaavn app has poor language filtering; English-only UIs are confusingTechnical SkillLowUsage FrequencyDaily, 4+ hours


4. User Stories

Authentication


As a new user, I want to sign up with my email or phone number so that I can create a personalised account.
As a returning user, I want to sign in with Google/Apple SSO so that I don't have to remember a password.
As a user, I want my session to persist across app restarts so that I don't have to log in every time.


Playback


As a user, I want to tap a song and have it start playing within 1 second so that my listening experience feels instant.
As a user, I want to see synced, scrolling lyrics while a song plays so that I can sing along.
As a user, I want to shuffle my liked songs so that I get variety without manual curation.
As a user, I want crossfade between tracks so that transitions feel smooth.
As a user, I want a sleep timer so that music stops automatically when I fall asleep.
As a premium user, I want gapless playback so that live albums sound uninterrupted.


Library & Organisation


As a user, I want to like a song so that it is saved to my library instantly.
As a user, I want to create playlists and add songs so that I can organise music by mood or occasion.
As a user, I want to follow artists so that I see their new releases first.
As a user, I want to save albums so that I can revisit entire records.


Discovery


As a user, I want to receive a personalised "Daily Mix" each morning so that I always have fresh listening.
As a user, I want to search by lyrics or mood so that I can find songs I half-remember.
As a user, I want an AI to generate a playlist from a text prompt so that I can describe a vibe and instantly listen.
As a user, I want an Artist Radio feature so that I can discover music similar to an artist I love.


Social


As a user, I want to see what my friends are listening to in real-time so that I can share music discoveries.
As a user, I want to invite a friend to co-edit a playlist so that we can build a shared music identity.
As a user, I want to share a song to WhatsApp/Instagram with a rich preview so that my friends can hear what I'm excited about.
As a user, I want a "Blend" feature that merges my taste with a friend's so that we have a shared playlist.


Offline & Downloads (Premium)


As a premium user, I want to download playlists for offline listening so that I can listen without mobile data.
As a premium user, I want to choose my download quality so that I can manage storage on my device.


Profile & Personalisation


As a user, I want a year-end Wrapped-style recap so that I can see and share my listening stats.
As a user, I want to choose between dark and light themes so that the app matches my preference.
As a user, I want to set my preferred language so that the UI and content suggestions are in my language.



5. Functional Requirements


FR-001 — User Authentication & Profile Management

FieldDetailsFeature IDFR-001Feature NameAuthentication & Profile ManagementDescriptionFull identity management: email/password, Google OAuth, Apple Sign-In, phone OTP. Profile creation, avatar upload, username, language preference, and account settings.Business ValueEnables personalisation, subscription management, social graph, and analyticsPriorityP0 — Critical

Inputs: Email, password, Google token, Apple token, phone number + OTP, display name, avatar image

Outputs: JWT access token (15min expiry), refresh token (30 days), user profile object

Validation Rules:


Email: RFC 5322 compliant
Password: ≥8 chars, 1 uppercase, 1 number, 1 special character
Phone: E.164 format, Indian (+91) numbers validated against carrier prefix list
Username: 3–30 chars, alphanumeric + underscore, no profanity (word-list filter)
Avatar: JPEG/PNG/WebP, max 5MB, auto-cropped to 1:1, stored at 256×256 and 64×64


Error Handling:


Duplicate email → 409 Conflict with message "An account with this email already exists"
Invalid OTP → 401 with remaining attempts count; lock after 5 attempts for 30 minutes
OAuth failure → redirect to email fallback with pre-filled email field
Rate limit → 429 with Retry-After header


Dependencies: FR-009 (Subscription tier affects profile features)

Acceptance Criteria:


User can register with email in <30 seconds
Google/Apple SSO completes in <3 seconds
Refresh token silently renews access token without user interaction
Password reset email delivered within 60 seconds
Account deletion purges all PII within 30 days (GDPR compliance)


Edge Cases:


User registers with Google, later tries email sign-in → link accounts
User changes email address → verify new email before switching
Simultaneous login from 5 devices → allow all, track device list
Deleted account tries to re-register → allow after 30-day grace period
Under-13 age detected → block registration (COPPA/DPDP compliance)



FR-002 — Audio Streaming Engine

FieldDetailsFeature IDFR-002Feature NameCore Audio StreamingDescriptionStream audio from JioSaavn unofficial API (saavn.me). Supports 96kbps (free), 160kbps (free), and 320kbps (free). Adaptive bitrate based on network conditions. Implements play/pause/skip/previous, seek bar, progress tracking, and queue management.Business ValueCore product functionality — everything else depends on thisPriorityP0 — Critical

Inputs: Song ID (JioSaavn format), quality preference, current network speed

Outputs: Audio stream URL (proxied), playback state object, progress event stream

Validation Rules:


Song ID must be valid JioSaavn format (alphanumeric, 8–12 chars)
Quality setting must be one of: low (96kbps), medium (160kbps), high (320kbps)
Seek position must be within [0, duration] range


Error Handling:


JioSaavn API unreachable → retry 3× with exponential backoff (1s, 2s, 4s), then show "Unavailable in your region" with suggestion to try another song
Stream URL expired (typically 6-hour TTL on JioSaavn URLs) → auto-refresh URL silently
Network drops mid-stream → buffer 30s ahead; resume automatically on reconnection
Song not in catalogue → show "Song not available" with "Find similar" CTA


Dependencies: FR-007 (Queue), FR-003 (Offline cache), JioSaavn API

Acceptance Criteria:


Stream starts within 1.0s on 4G, 2.0s on 3G
Seeking to any position completes within 0.5s
30-second audio buffer maintained
Zero audible glitch on network quality transitions
Background playback works with screen locked


Edge Cases:


User skips very rapidly (10 songs in 5 seconds) → debounce, cancel in-flight requests
Song is geo-restricted by JioSaavn → detect 403, try alternate source, else skip
Audio focus lost (phone call) → pause, resume after call ends
Bluetooth device disconnects mid-song → pause playback, notify user
Corrupt stream data received → detect via checksum, reload stream



FR-003 — Offline Downloads (Premium)

FieldDetailsFeature IDFR-003Feature NameOffline Download & PlaybackDescriptionPremium users can download up to 10,000 songs. Downloads are encrypted (AES-256) and tied to the user's device. Playback requires valid premium session (verified every 30 days). Download quality selectable: 96/160/320kbps.Business ValuePrimary premium upsell driver; addresses India's unreliable connectivityPriorityP1 — High

Inputs: Song/playlist/album selection, quality preference, available device storage

Outputs: Encrypted audio file stored in app sandbox, download progress events, offline playback capability

Validation Rules:


Maximum 10,000 offline songs per account
Maximum 5 authorised devices simultaneously
Each download must check premium status before initiating
Re-verify premium status on app launch (max 30-day offline grace period)


Error Handling:


Insufficient storage → show current usage, suggest removing old downloads
Premium expired → mark downloads as locked, show upgrade prompt
Device limit reached → show device management screen; allow de-authorisation
Download interrupted → resume from byte offset (resumable downloads)


Edge Cases:


User cancels subscription → grace period of 30 days before downloads locked
User uninstalls and reinstalls → re-authorise device, restore download list from cloud manifest
User transfers downloads to new phone → must re-download (encrypted to device)
Song removed from JioSaavn catalogue mid-download → complete download with cached data or abort gracefully



FR-004 — Search & Discovery

FieldDetailsFeature IDFR-004Feature NameSearch, Browse & DiscoveryDescriptionFull-text search across songs, albums, artists, playlists. Supports lyric snippets (via JioSaavn), mood search ("songs for long drives"), trending charts, genre browsing, and artist radio generation.Business ValueDiscovery is the #1 driver of engagement and session lengthPriorityP0 — Critical

Inputs: Search query (text), filter type (song/album/artist/playlist), mood tag, language filter

Outputs: Ranked search results with thumbnails, audio previews, relevance scores

Validation Rules:


Minimum query length: 2 characters
Maximum query length: 150 characters
Sanitise input: strip SQL/script injection characters
Auto-correct common misspellings (fuzzy matching via backend)


Error Handling:


No results → show "No results for X" with "Try: [suggested alternatives]"
API timeout → show cached recent searches as fallback
Profanity in search → allow (catalogue has explicit content), but respect user's explicit filter preference


Edge Cases:


Query in Hindi/Tamil (Devanagari/Tamil script) → pass to JioSaavn as-is (they handle transliteration)
Very long query (paragraph) → truncate to first 150 chars, show truncation notice
Search while offline → search local cached metadata only, show "(Offline)" badge



FR-005 — AI Playlist Generation

FieldDetailsFeature IDFR-005Feature NameAI Playlist GeneratorDescriptionUsers type a natural language prompt ("rainy evening jazz with Indian vocals") and receive a 20-song playlist generated by Claude API. The backend builds a structured query from the prompt, fetches matching songs from JioSaavn, and returns a curated playlist.Business ValueHighly shareable, viral feature; key differentiator from JioSaavn's own appPriorityP1 — High

Inputs: Natural language text prompt (max 500 chars), optional language/genre filters

Outputs: Named playlist with 15–25 songs, cover art collage, shareable link

Validation Rules:


Prompt must be ≥10 chars and ≤500 chars
Filter inappropriate/harmful prompts before sending to AI
Deduplicate songs in generated playlist
All generated songs must be verifiable in JioSaavn catalogue before showing to user


Error Handling:


AI API timeout (>10s) → show loading state, retry once, then offer manual search
Generated songs not in catalogue → replace with similar catalogue items, note substitution
Prompt is offensive → reject with "Please describe a music mood or style"


Edge Cases:


User generates 50 playlists in one day → rate limit to 20 AI generations/day per user
Same prompt submitted multiple times → return same playlist (cache by prompt hash) for 24 hours
Prompt in regional language → translate to English for AI processing, keep original title



FR-006 — Social Features

FieldDetailsFeature IDFR-006Feature NameSocial — Friend Activity, Collaborative Playlists, BlendDescriptionUsers can follow friends, see their real-time listening activity, co-edit playlists, and generate a "Blend" playlist combining two users' taste profiles. In-app messaging for sharing tracks.Business ValueSocial features reduce churn and increase DAU significantly (Spotify data: social users have 2× retention)PriorityP1 — High

Inputs: Friend's username/phone, playlist share link, collaboration invite

Outputs: Friend activity feed, collaborative playlist (real-time updates), Blend playlist, share card

Validation Rules:


Follow request requires mutual confirmation (no default public following without consent)
Collaborative playlist: max 10 collaborators per playlist
Blend: requires both users to have ≥50 liked songs for accurate taste profiling
Share card: generated as PNG/WebP, max 200KB


Error Handling:


Friend revokes follow → remove from activity feed silently
Collaborator deletes song → log deletion with username, allow undo within 24 hours
Blend partner has no data → show "Not enough listening history yet" with suggested timeframe


Edge Cases:


User blocks another user → remove from all shared playlists, hide from activity feed, prevent re-follow
Both users in Blend have identical taste → still generate but show "You have nearly identical taste!" message
Collaborative playlist with 10 editors: 2 add same song simultaneously → deduplicate, keep one, notify both



FR-007 — Queue & Playback Controls

FieldDetailsFeature IDFR-007Feature NameQueue Management & Playback ControlsDescriptionFull queue management: view current queue, reorder by drag-and-drop, add next, add to end, remove from queue. Playback modes: normal, shuffle, repeat-one, repeat-all. Crossfade (0–12s adjustable). Gapless playback. Sleep timer.Business ValuePower user retention; differentiates from basic playersPriorityP1 — High

Inputs: Queue reorder action (drag), crossfade duration (slider), sleep timer duration, repeat/shuffle toggle

Outputs: Updated queue state, crossfade-blended audio transition, timer countdown notification

Validation Rules:


Queue maximum size: 500 songs
Crossfade: integer 0–12 seconds
Sleep timer: 5 / 10 / 15 / 30 / 45 / 60 / 90 minutes options + "end of song"


Edge Cases:


User adds 501st song → remove oldest from bottom of queue with toast notification
Crossfade with very short song (<15s) → cap crossfade at half song duration
Sleep timer fires during crossfade → complete current song, then stop



FR-008 — Lyrics Display

FieldDetailsFeature IDFR-008Feature NameSynced & Static LyricsDescriptionShow word-by-word synced lyrics sourced from JioSaavn API. Lyrics auto-scroll with playback. Tapping a lyric line seeks to that position. Falls back to static (unsynced) lyrics if synced not available.Business ValueLyrics are a top-3 requested feature; drives session timePriorityP1 — High

Edge Cases:


Synced lyrics drift from audio (common with live recordings) → allow manual ±2 second timing offset adjustment
Lyrics in mixed scripts (Hinglish) → render natively; no transliteration forced
No lyrics available → show "Lyrics not available" with Last.fm attribution link



FR-009 — Subscription & Payments

FieldDetailsFeature IDFR-009Feature NameFreemium Subscription & Payment ProcessingDescriptionFree tier (ad-supported, 320kbps streaming, no offline). Premium tier at ₹49/month: ad-free, offline downloads, full equaliser, high-res artwork. Payment via Razorpay (UPI, cards, net banking, wallets).Business ValuePrimary revenue streamPriorityP1 — High

Inputs: Payment method selection, UPI ID / card details (handled by Razorpay, never stored by Melodix)

Outputs: Subscription record, premium flags on user profile, receipt email

Validation Rules:


Never store raw card data — use Razorpay tokenisation
Subscription auto-renews unless cancelled 24 hours before renewal
Provide prorated refund within 48 hours of first subscription if requested


Edge Cases:


Payment succeeds but webhook fails → idempotent webhook processing; reconcile via Razorpay dashboard query
User subscribes, immediately cancels → retain premium until end of billing period
Bank declines auto-renewal → 3-day grace period with daily email reminders



FR-010 — Melodix Wrapped (Annual Stats)

FieldDetailsFeature IDFR-010Feature NameMelodix Wrapped — Annual Listening RecapDescriptionInteractive, shareable year-end (and monthly) listening recap: top 5 artists, top 10 songs, total listening time, top genre, listening streaks, discovery score, and a personalised "music personality" label. Generated as an animated story card shareable to Instagram/WhatsApp.Business ValueAnnual viral growth engine; high social sharing drives organic acquisitionPriorityP2 — Medium

Edge Cases:


User joined in December → show "Too new? Check back next year" with 30-day mini-wrap instead
User has listened to only 1 artist → generate valid single-artist wrap without crashing charts
Offline-only songs must be included in stats tracking



6. User Flow

First Visit (New User)


Splash screen (animated logo, 1.5s max)
Language selector (10 supported languages)
Sign Up options: Email, Google, Apple, Phone OTP
Interests onboarding: Select 3+ genres (grid of 20 cards with music snippets)
Artist quickfollow: Auto-suggested 10 artists based on genre picks → follow 3+
Home screen with pre-populated "For You" section


Authentication (Returning User)


App launch → validate stored refresh token
If valid → navigate directly to Home (no login screen)
If expired → show sign-in screen with SSO options pre-selected


Core Listening Flow


User browses Home / Search / Library
Taps a song card → Now Playing screen slides up
Song starts buffering → plays within 1s
Lyrics panel accessible via swipe-up
Queue accessible via queue icon
Mini-player persistent at bottom when navigating away from Now Playing


Playlist Creation Flow


Like a song → added to Liked Songs automatically
Tap "Add to Playlist" → sheet shows existing playlists + "New Playlist"
New Playlist → enter name, set cover (auto-generated or custom photo), set public/private
Playlist created → confirmation toast with "Open Playlist" CTA


Offline Download Flow (Premium)


Tap download icon on song/album/playlist
Quality selector sheet appears (96 / 160 / 320kbps with storage estimate)
Download queues with progress indicator in Library tab
Completed downloads shown with green icon; accessible offline


Error Recovery Flow


Stream failure → auto-retry 3× → fallback message with "Try again" button + "Report issue" link
Login session expired → silent token refresh → if fails, show sign-in with "Session expired" notice
No internet on launch → detect offline mode, show only downloaded content, grey-out online features with tooltip
Payment failure → show Razorpay error in human-readable form, offer retry or alternative payment method



7. Non-Functional Requirements

Performance


P95 stream start time: <1.2s on 4G, <2.5s on 3G
App cold start: <2.5s on mid-range Android (Snapdragon 680)
UI frame rate: ≥60fps on all screens; 120fps on capable devices
Search results: <300ms for cached queries, <800ms for API queries
API response time P95: <500ms


Availability


Target uptime: 99.5% (≤43.8h downtime/year)
Planned maintenance: 02:00–04:00 IST Sundays only
Degraded-mode fallback: If JioSaavn API unavailable, show cached content + "Live streaming paused" banner


Scalability


Support 1M concurrent streams without architectural changes (see TRD Phase 3)
Horizontal scaling via Kubernetes auto-scaling


Reliability


Audio never skips due to application bug (only acceptable cause: network)
Offline downloads never silently corrupt (checksum verified on write and read)
User library changes sync across devices within 5 seconds


Accessibility


WCAG 2.1 AA compliance
VoiceOver/TalkBack support for all core playback controls
Minimum tap target: 44×44px
Colour contrast ratio ≥ 4.5:1 on all text
Captions on all video content (future)


Maintainability


Test coverage ≥80% (unit + integration)
Zero known Critical/High severity bugs at release
All services documented with OpenAPI 3.0 specs
Codebase linted, formatted, and passing CI before any merge


Observability


Structured JSON logging on all backend services
Distributed tracing (OpenTelemetry)
Uptime and error-rate dashboards (Grafana)
P95/P99 latency tracked per API endpoint


Internationalisation


10 languages at launch: English, Hindi, Tamil, Telugu, Bengali, Kannada, Malayalam, Marathi, Punjabi, Gujarati
RTL not required at v1.0 (no Arabic/Urdu UI)
All content strings in i18n JSON files; no hardcoded strings



8. Security Requirements

Authentication


JWT RS256 signed tokens; access token TTL: 15 minutes
Refresh token stored in HttpOnly Secure cookie (web) / encrypted keychain (mobile)
Google/Apple OAuth 2.0 PKCE flow
OTP: TOTP algorithm, 6-digit, 5-minute expiry, 5-attempt lockout


Authorization


RBAC: roles are user (free), premium, admin
Every API endpoint declares required role; enforced at gateway level
Users can only access their own data (row-level security in Postgres)


Encryption


All data in transit: TLS 1.3 minimum
All data at rest (database, file storage): AES-256-GCM
Offline downloads: AES-256 per-device keys derived from user+device UUID using PBKDF2
Passwords: bcrypt, cost factor 12


Rate Limiting


Authentication endpoints: 10 requests/minute/IP
Search: 60 requests/minute/user
AI playlist generation: 20 requests/day/user
Stream URL fetch: 300 requests/minute/user
Global: 1000 requests/minute/IP (DDoS protection layer)


Input Validation


All user-supplied input validated and sanitised at API gateway
SQL injection prevention: parameterised queries only (no string concatenation)
XSS prevention: React's JSX auto-escaping + CSP headers
File uploads: MIME type validation + virus scan (ClamAV)


Data Protection


PII (email, phone, name): encrypted at column level in Postgres
Listening history: retained 2 years by default; user can delete at any time
No selling/sharing of user data to third parties
GDPR: right to erasure, right to portability (export as JSON)
India DPDP Act 2023 compliance


OWASP Top 10 Mitigations


A01 Broken Access Control → RBAC + row-level security + automated testing
A02 Cryptographic Failures → TLS 1.3 + AES-256 + bcrypt; no MD5/SHA1
A03 Injection → parameterised queries; ORMs with no raw SQL in hot paths
A05 Security Misconfiguration → IaC (Terraform) enforces all security settings
A07 Auth Failures → account lockout, MFA option, refresh token rotation
A09 Security Logging → all auth events logged to immutable audit log


Session Management


Refresh token rotation: every access-token issue generates a new refresh token
Concurrent session limit: 5 devices; oldest session expires on 6th login
Full session invalidation on password change and account deletion



9. Analytics & Metrics

User Metrics


DAU / MAU / WAU
Session length (mean and median)
Songs played per session
Feature discovery rate (% users who used search, AI playlist, social)
Onboarding completion rate
D1 / D7 / D30 retention


Business Metrics


Free → Premium conversion rate
Monthly Recurring Revenue (MRR)
Churn rate (monthly)
Average Revenue Per User (ARPU)
Customer Acquisition Cost (CAC)
Lifetime Value (LTV)


Technical Metrics


Stream start latency (P50/P95/P99)
Buffering events per session
API error rates (5xx, 4xx)
Crash-free rate by platform
JioSaavn API success rate


KPIs (v1.0 launch targets, 12 months)

KPITargetRegistered Users100,000DAU35,000Premium Subscribers8,000MRR₹392,000 (~₹49 × 8,000)DAU/MAU≥35%D30 Retention≥40%App Store Rating≥4.3Stream P95 Latency<1.2s


10. Future Scope

Short-Term (6–12 months post-launch)


Podcast streaming integration
Music video playback (YouTube iframe embed)
Lossless audio (Hi-Fi tier) if API source becomes available
Advanced equaliser with presets
Car Mode (large touch targets, simplified UI)
Widget support (Android/iOS lock screen and home screen)


Medium-Term (12–24 months)


Live radio / DJ livestreams
Artist profiles with exclusive content upload
Audiobook catalogue
Collaborative real-time listening ("Jam Session" — listen together with friends)
Lyrics karaoke mode with microphone scoring
Regional language AI playlists (prompts in Tamil, Hindi, etc.)


Long-Term Vision (24+ months)


White-label B2B licensing to telecom operators (bundled with data plans)
Creator monetisation — indie artists upload and earn per stream
AI-generated personalised music (generative audio model integration)
Smart speaker / IoT device SDK
Melodix for Web3 — artist NFT drops, token-gated exclusive content