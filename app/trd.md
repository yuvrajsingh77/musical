TECHNICAL REQUIREMENTS DOCUMENT (TRD)


1. Technical Overview

Architecture Style

Microservices with API Gateway for backend; React PWA + React Native for frontend.

Justification: Microservices allow independent scaling of high-load components (streaming proxy, search, recommendations) without scaling the entire monolith. A PWA covers web + lightweight mobile; React Native covers iOS/Android with code sharing.

Deployment Strategy


Cloud: AWS primary (Mumbai ap-south-1 region for low latency to India)
Containers: Docker + Kubernetes (EKS)
CDN: CloudFront for static assets + artwork; direct stream proxying
IaC: Terraform
CI/CD: GitHub Actions → ECR → EKS


Technology Stack Summary

LayerTechnologyJustificationWeb FrontendReact 18 + TypeScript + ViteFast dev experience, large ecosystem, PWA supportMobileReact Native + Expo~70% code sharing with web, single teamState ManagementZustand + React QueryLightweight, no boilerplate; server-state via RQBackend APINode.js (Fastify) + TypeScriptHigh throughput, non-blocking I/O ideal for streaming proxyAI ServicePython (FastAPI)Best AI/ML ecosystem; Claude API integrationDatabasePostgreSQL 16ACID compliance, rich JSON support, row-level securityCacheRedis 7Session store, rate limiting, search result cache, queue stateSearch IndexMeilisearchTypo-tolerant, self-hosted, fast full-text searchObject StorageAWS S3Profile avatars, artwork cache, download manifestsMessage QueueAWS SQSAsync tasks: playlist generation, Wrapped computationAuthCustom JWT + Google/Apple OAuthFull control; no vendor lock-inPaymentsRazorpayBest Indian payment gateway; UPI + cards + walletsMonitoringGrafana + Prometheus + OpenTelemetryFull-stack observabilityCDNCloudFrontStatic assets + album art


2. System Architecture

Layer Descriptions

Client Layer:


React PWA (web browsers, installable)
React Native apps (iOS App Store, Google Play)
Both clients communicate exclusively with the API Gateway


API Gateway Layer (Kong / AWS API Gateway):


TLS termination, JWT validation, rate limiting
Routes traffic to appropriate microservice
Request/response logging


Business Logic Layer (Microservices):


Auth Service — registration, login, token management
User Service — profiles, preferences, subscriptions
Music Service — JioSaavn API proxy, stream URL management, metadata enrichment
Search Service — Meilisearch integration, autocomplete, mood-based search
Playlist Service — CRUD playlists, collaborative editing, Blend algorithm
Social Service — friend graph, activity feed, sharing
AI Service (Python) — Claude API integration for playlist generation, recommendations
Payment Service — Razorpay integration, webhook handling, subscription lifecycle
Analytics Service — event ingestion, Wrapped computation


Data Layer:


PostgreSQL: core relational data (users, playlists, likes, social graph)
Redis: session cache, rate limit counters, real-time queue state, activity feed
Meilisearch: song/artist/album search index (synced from JioSaavn metadata)
S3: avatars, artwork cache, offline download manifests


Infrastructure Layer:


EKS (Kubernetes) running all services
RDS PostgreSQL (Multi-AZ)
ElastiCache Redis (cluster mode)
CloudFront CDN
Route 53 DNS
ACM for TLS certificates


Data Flow — Song Playback

User taps song
    → React client sends GET /api/v1/music/stream/{songId}?quality=320
    → API Gateway validates JWT, routes to Music Service
    → Music Service checks Redis cache for stream URL (TTL: 5 hours)
    → Cache miss: Music Service calls saavn.me API → gets stream URL
    → Cache hit / fresh URL stored → return stream URL to client
    → Client's audio element loads stream URL directly from JioSaavn CDN
    → Analytics event fired: play_started {userId, songId, quality, timestamp}
    → Playback progresses; client fires play_progress every 10s
    → Song ends: Analytics event fired: play_completed


3. Frontend Architecture

Framework & Build


React 18 with TypeScript (strict mode)
Vite for bundling (dev server + production builds)
PWA: Workbox service worker for offline shell caching
React Native 0.74 with Expo SDK 52 for mobile


Folder Structure

melodix-web/
├── public/                    # Static assets, manifest.json
├── src/
│   ├── api/                   # API client functions (React Query hooks)
│   │   ├── music.ts
│   │   ├── auth.ts
│   │   ├── playlist.ts
│   │   └── social.ts
│   ├── components/
│   │   ├── ui/                # Primitives: Button, Input, Card, Sheet
│   │   ├── player/            # NowPlaying, MiniPlayer, Queue, Lyrics
│   │   ├── library/           # PlaylistCard, AlbumGrid, ArtistRow
│   │   ├── social/            # FriendActivity, BlendCard, ShareSheet
│   │   └── discovery/         # DailyMix, ArtistRadio, AIPlaylistModal
│   ├── features/              # Feature-level modules (co-locate logic+UI)
│   │   ├── auth/
│   │   ├── player/
│   │   ├── search/
│   │   ├── library/
│   │   └── social/
│   ├── hooks/                 # Custom hooks (usePlayer, useQueue, useAuth)
│   ├── store/                 # Zustand stores
│   │   ├── playerStore.ts     # Playback state, queue
│   │   ├── authStore.ts       # User session
│   │   └── uiStore.ts         # Theme, modal states
│   ├── services/
│   │   ├── audioEngine.ts     # Web Audio API wrapper
│   │   ├── offlineManager.ts  # IndexedDB for offline metadata
│   │   └── analyticsService.ts
│   ├── utils/
│   ├── types/                 # Shared TypeScript interfaces
│   ├── i18n/                  # Translation JSON files
│   ├── App.tsx
│   └── main.tsx
└── melodix-mobile/            # React Native app (mirrors structure)
    ├── src/
    │   ├── navigation/        # React Navigation stack/tab config
    │   ├── screens/
    │   ├── components/        # Shared with web via monorepo
    │   └── native/            # Platform-specific: audio, downloads

State Management

StoreLibraryContentsServer stateReact QuerySongs, playlists, search results, user profilePlayer stateZustandCurrent song, queue, playback status, progressAuth stateZustand + localStorageJWT, user object, premium flagUI stateZustandTheme, active modal, sidebar open

Routing


Web: React Router v6 with lazy-loaded route chunks
Mobile: React Navigation v6 (Stack + Tab navigators)
Route-level code splitting to keep initial bundle <200KB


Performance Optimisation


Virtualised lists (react-window / Flashlist on RN) for all song/playlist lists
Image lazy loading with blur-hash placeholders
Service worker caches app shell, fonts, and critical CSS
Bundle analysis with rollup-plugin-visualizer; tree-shaking enforced
WebP/AVIF album art served via CloudFront with format negotiation



4. Backend Architecture

Framework

Fastify 4 (Node.js) — chosen for its 30–40% higher throughput vs Express; schema-based validation via Zod; native TypeScript support.

Python FastAPI for the AI service only (Claude integration, recommendation algorithms).

Project Structure (per service)

services/
├── auth-service/
│   ├── src/
│   │   ├── controllers/       # Route handlers
│   │   ├── services/          # Business logic
│   │   ├── repositories/      # Database access (Prisma)
│   │   ├── middleware/        # Auth, error handler
│   │   ├── validators/        # Zod schemas
│   │   └── utils/
│   ├── Dockerfile
│   ├── package.json
│   └── prisma/schema.prisma
├── music-service/
├── playlist-service/
├── social-service/
├── ai-service/               # Python FastAPI
├── payment-service/
└── analytics-service/

Key Services

Music Service — most critical for latency:


Proxies requests to saavn.me API
Caches stream URLs in Redis with 5-hour TTL
Enriches JioSaavn metadata with MusicBrainz (MBID linkage) and TheAudioDB (HD artwork)
Stream URLs are never stored long-term; re-fetched on expiry


AI Service (Python):


Receives playlist prompt from user
Calls Claude API (claude-sonnet-4-6) with structured prompt
Claude returns JSON: {songs: [{title, artist, reason}], playlist_name, description}
Service validates each song against JioSaavn search API
Returns verified playlist to Music Service for delivery



5. Database Design

Users Table

ColumnTypeConstraintsNotesidUUIDPK, DEFAULT gen_random_uuid()emailVARCHAR(255)UNIQUE, NOT NULL, encryptedphoneVARCHAR(20)UNIQUE, nullable, encryptedpassword_hashVARCHAR(255)nullable (null for OAuth users)bcryptdisplay_nameVARCHAR(100)NOT NULLavatar_urlTEXTnullableS3 URLusernameVARCHAR(30)UNIQUE, NOT NULLroleENUM('user','premium','admin')DEFAULT 'user'languageVARCHAR(10)DEFAULT 'en'BCP 47is_verifiedBOOLEANDEFAULT falseis_activeBOOLEANDEFAULT truecreated_atTIMESTAMPTZDEFAULT NOW()updated_atTIMESTAMPTZDEFAULT NOW()last_login_atTIMESTAMPTZnullabledeleted_atTIMESTAMPTZnullablesoft delete

Indexes: email (unique), username (unique), phone (unique), role (partial — only premium users), deleted_at (partial)


Songs Table (Metadata Cache)

ColumnTypeConstraintsNotesidUUIDPKInternal IDsaavn_idVARCHAR(50)UNIQUE, NOT NULLJioSaavn song IDtitleVARCHAR(255)NOT NULLartist_idsUUID[]NOT NULLArray FK to artistsalbum_idUUIDFK to albumsduration_msINTEGERNOT NULLlanguageVARCHAR(10)has_lyricsBOOLEANDEFAULT falselyrics_syncedBOOLEANDEFAULT falseartwork_urlTEXTCloudFront-cached URLmbidVARCHAR(50)nullableMusicBrainz IDexplicitBOOLEANDEFAULT falserelease_yearSMALLINTplay_countBIGINTDEFAULT 0denormalised countercreated_atTIMESTAMPTZDEFAULT NOW()

Indexes: saavn_id (unique), album_id, language, release_year, GIN index on artist_ids


Playlists Table

ColumnTypeConstraintsNotesidUUIDPKowner_idUUIDFK users.id, NOT NULLnameVARCHAR(255)NOT NULLdescriptionTEXTcover_urlTEXTis_publicBOOLEANDEFAULT falseis_collaborativeBOOLEANDEFAULT falsesong_countINTEGERDEFAULT 0denormalisedcreated_atTIMESTAMPTZDEFAULT NOW()updated_atTIMESTAMPTZDEFAULT NOW()


Playlist_Songs Table (Junction)

ColumnTypeConstraintsidUUIDPKplaylist_idUUIDFK playlists.id, NOT NULLsong_idUUIDFK songs.id, NOT NULLadded_byUUIDFK users.idpositionINTEGERNOT NULLadded_atTIMESTAMPTZDEFAULT NOW()

Unique constraint: (playlist_id, song_id)
Index: (playlist_id, position) for ordered fetches


Liked_Songs Table

ColumnTypeConstraintsuser_idUUIDFK users.idsong_idUUIDFK songs.idliked_atTIMESTAMPTZDEFAULT NOW()

PK: (user_id, song_id)
Index: user_id + liked_at DESC for chronological feed


Social_Graph Table (Follows)

ColumnTypeConstraintsfollower_idUUIDFK users.idfollowing_idUUIDFK users.idcreated_atTIMESTAMPTZDEFAULT NOW()statusENUM('pending','accepted')DEFAULT 'pending'

PK: (follower_id, following_id)


Listening_History Table

ColumnTypeConstraintsidUUIDPKuser_idUUIDFK users.idsong_idUUIDFK songs.idplayed_atTIMESTAMPTZNOT NULLduration_played_msINTEGERcompletedBOOLEANdevice_typeVARCHAR(20)qualityVARCHAR(10)

Partitioned by: played_at (monthly partitions, auto-dropped after 2 years)
Index: (user_id, played_at DESC)


Subscriptions Table

ColumnTypeConstraintsidUUIDPKuser_idUUIDFK users.id, UNIQUEplanVARCHAR(50)NOT NULLstatusENUM('active','cancelled','expired','grace')razorpay_subscription_idVARCHAR(100)UNIQUEcurrent_period_startTIMESTAMPTZcurrent_period_endTIMESTAMPTZcancelled_atTIMESTAMPTZnullablecreated_atTIMESTAMPTZ


Downloads Table (Premium)

ColumnTypeConstraintsidUUIDPKuser_idUUIDFK users.idsong_idUUIDFK songs.iddevice_idVARCHAR(255)NOT NULLqualityVARCHAR(10)file_size_bytesBIGINTdownloaded_atTIMESTAMPTZchecksumVARCHAR(64)SHA-256 of encrypted file

Unique: (user_id, song_id, device_id)


6. ER Diagram Description

One-to-One


users ↔ subscriptions — A user has at most one active subscription record


One-to-Many


users → playlists — One user owns many playlists
playlists → playlist_songs — One playlist has many song entries
users → listening_history — One user has many play events
users → downloads — One user has many downloaded songs


Many-to-Many


users ↔ users (via social_graph) — Users follow each other
users ↔ songs (via liked_songs) — Users like many songs; songs liked by many users
songs ↔ playlists (via playlist_songs) — Songs appear in many playlists; playlists contain many songs
playlists ↔ users (via playlist_collaborators) — Collaborative playlists have multiple editors



7. API Design

Auth Service

POST /api/v1/auth/register


Description: Create new user account
Request: {email, password, display_name, language?}
Response 201: {user: {...}, access_token, refresh_token}
Response 409: {error: "EMAIL_EXISTS"}
Auth: None


POST /api/v1/auth/login


Description: Email/password login
Request: {email, password}
Response 200: {access_token, refresh_token, user}
Response 401: {error: "INVALID_CREDENTIALS"}
Response 429: {error: "TOO_MANY_ATTEMPTS", retry_after: 1800}


POST /api/v1/auth/refresh


Description: Refresh access token using refresh token (HttpOnly cookie)
Request: Cookie refresh_token
Response 200: {access_token} + new refresh_token cookie
Response 401: {error: "INVALID_REFRESH_TOKEN"}


POST /api/v1/auth/oauth/google


Description: Google SSO
Request: {id_token}
Response 200: {access_token, refresh_token, user, is_new_user}


DELETE /api/v1/auth/logout


Description: Invalidate refresh token
Auth: Bearer token required



Music Service

GET /api/v1/music/stream/{songId}


Description: Get streaming URL for a song
Query: quality=96|160|320 (default: 160)
Response 200: {stream_url, expires_at, quality, duration_ms}
Response 404: {error: "SONG_NOT_FOUND"}
Response 403: {error: "PREMIUM_REQUIRED"} (if 320kbps requested by free user)
Auth: Bearer required
Note: stream_url is a time-limited URL from JioSaavn CDN


GET /api/v1/music/songs/{songId}


Description: Get song metadata
Response 200: {id, title, artists, album, duration_ms, artwork_url, has_lyrics, explicit, saavn_id}


GET /api/v1/music/songs/{songId}/lyrics


Description: Get synced or static lyrics
Response 200: {synced: true, lines: [{text, start_ms, end_ms}]}
Response 200: {synced: false, text: "..."}
Response 404: {error: "LYRICS_NOT_AVAILABLE"}


GET /api/v1/music/search


Description: Search songs, artists, albums, playlists
Query: q=string&type=song|artist|album|playlist&lang=hi|en|ta&page=1&limit=20
Response 200: {results: [...], total, page, has_more}


GET /api/v1/music/daily-mixes


Description: Get user's 6 personalised Daily Mix playlists
Response 200: {mixes: [{id, name, cover_url, song_count, mood}]}
Auth: Bearer required


GET /api/v1/music/artist-radio/{artistId}


Description: Generate an artist radio station
Response 200: {songs: [...], station_name}



Playlist Service

GET /api/v1/playlists


Description: Get current user's playlists
Response 200: {playlists: [{id, name, cover_url, song_count, is_public, is_collaborative}]}
Auth: Bearer required


POST /api/v1/playlists


Description: Create playlist
Request: {name, description?, cover_url?, is_public, is_collaborative}
Response 201: {playlist}


PUT /api/v1/playlists/{id}


Description: Update playlist metadata
Auth: Owner or collaborator


DELETE /api/v1/playlists/{id}


Description: Delete playlist (owner only)
Response 204


POST /api/v1/playlists/{id}/songs


Description: Add song to playlist
Request: {song_id, position?}
Response 201: {playlist_song}


DELETE /api/v1/playlists/{id}/songs/{songId}


Description: Remove song from playlist
Response 204


POST /api/v1/playlists/{id}/collaborators


Description: Invite collaborator
Request: {username}
Response 201: {collaborator}



AI Service

POST /api/v1/ai/playlist


Description: Generate AI playlist from text prompt
Request: {prompt: string, language?: string}
Response 202: {job_id} (async — polls for result)
Response 200 (poll): {status: "processing"|"complete"|"failed", playlist?}
Auth: Bearer required
Rate limit: 20/day/user


GET /api/v1/ai/playlist/{jobId}


Description: Poll AI playlist generation status
Response 200: {status, playlist: {name, songs: [...]}}



Social Service

GET /api/v1/social/feed


Description: Friend activity feed (real-time what friends are listening to)
Response 200: {activities: [{user, song, timestamp, is_live}]}
Auth: Bearer required


POST /api/v1/social/follow/{userId}


Description: Send follow request
Response 201: {status: "pending"}


POST /api/v1/social/blend/{userId}


Description: Create Blend playlist with another user
Response 202: {blend_id} (async computation)



Analytics Service

POST /api/v1/analytics/event


Description: Ingest playback/UI events
Request: {event_type, payload, timestamp} (batch: up to 20 events)
Response 202: {accepted: N}
Auth: Bearer required (silent fire-and-forget from client)


GET /api/v1/analytics/wrapped


Description: Get user's Wrapped stats
Query: year=2025&month=6 (month optional — for monthly mini-wrap)
Response 200: {top_artists, top_songs, total_minutes, top_genre, personality, listening_streak}



8. Security Architecture

Authentication Flow


User submits credentials → Auth Service validates → issues JWT (RS256, 15min)
Refresh token (UUIDv4, hashed with SHA-256) stored in Redis and delivered as HttpOnly Secure SameSite=Strict cookie
Client stores access token in memory only (never localStorage on web)
On 401, client automatically hits /auth/refresh → gets new access token
Refresh token rotation: each refresh issues a new RT and invalidates the old one


Password Strategy


Bcrypt with cost factor 12 (~250ms hash time → brute-force resistant)
Minimum complexity enforced at API + UI layers
Password reset via time-limited (1hr) single-use token, delivered via email
Passwords never logged or returned in API responses


Token Strategy


Access tokens: RS256 JWT, signed with 2048-bit RSA private key; public key published at /.well-known/jwks.json
Refresh tokens: opaque UUIDv4, stored hashed in Redis with TTL 30 days
Device tokens (for offline downloads): HMAC-SHA256 of userId + deviceId + secret


Encryption Strategy


Column-level encryption (PGP) for email, phone in Postgres
Application-level AES-256-GCM for offline download files
All S3 buckets: SSE-S3 encryption at rest
All RDS instances: AES-256 encryption at rest



9. Performance Engineering

Caching Strategy

DataCache LayerTTLStream URLsRedis5 hoursSong metadataRedis24 hoursSearch resultsRedis10 minutesUser profileRedis5 minutesDaily Mix playlistsRedis12 hoursActivity feedRedis Sorted SetReal-time, 48hr maxArtworkCloudFront CDN30 days

Database Optimisation


Connection pooling via PgBouncer (max 100 connections per service)
Read replicas for analytics and search queries (RDS read replica in same AZ)
listening_history partitioned by month; partition indexes maintained automatically
EXPLAIN ANALYZE run on all queries; target <5ms for 95% of queries


Query Optimisation


N+1 queries eliminated: use JOIN or Prisma's include with depth limit 2
Pagination: cursor-based for infinite scroll (not offset — offset degrades at scale)
Aggregates (play counts, follower counts) pre-computed and stored as denormalised columns; updated via background job, not real-time


CDN Strategy


Album artwork: cached at CloudFront edge; served as WebP (converted by Lambda@Edge if origin is JPEG)
App static assets: S3 + CloudFront with immutable cache headers (versioned filenames)
Stream audio: delivered directly from JioSaavn CDN; Melodix does not proxy audio bytes


Load Balancing


ALB (Application Load Balancer) distributing across EKS node groups
Health check: GET /health returning 200 within 2 seconds
Sticky sessions: disabled (stateless services)


Expected Throughput

ScenarioRequests/secMVP launch (1K concurrent users)~500 RPSGrowth (50K concurrent users)~25,000 RPSEnterprise scale (500K concurrent)~250,000 RPS (horizontal scale)

Performance Targets


API P95 response time: <500ms
Stream URL delivery: <200ms (cached), <800ms (cache miss → saavn.me)
Search P95: <300ms
Database query P95: <5ms



10. Scalability Plan

Phase 1 — MVP (0–100K users)


Single EKS cluster, 3 nodes (t3.large)
RDS PostgreSQL t3.medium (Multi-AZ)
ElastiCache Redis cache.t3.micro
All services on same cluster; resource limits per namespace
Expected monthly AWS cost: ~$450/month


Phase 2 — Growth (100K–1M users)


Separate node groups per service tier (high-CPU vs high-memory)
Read replica added for analytics and search
Redis cluster mode enabled
Meilisearch moved to dedicated instances
CDN cache-hit rate target: >90% for artwork
Streaming proxy: move to edge Lambda if needed
Estimated monthly AWS cost: ~$2,500/month


Phase 3 — Enterprise Scale (1M+ users)


Multi-region deployment (Mumbai + Singapore)
Aurora PostgreSQL (serverless v2) for auto-scaling DB connections
ElastiCache Global Datastore (multi-region replication)
Service mesh (Istio) for inter-service communication
Event-driven architecture for analytics (Kinesis → Spark → data warehouse)
Estimated monthly AWS cost: ~$15,000+/month


Bottleneck Analysis

BottleneckRiskMitigationJioSaavn API rate limitHIGHCache stream URLs aggressively; implement circuit breakerRedis memory on activity feedMEDIUMTrim feed to last 100 events per user; TTL 48hrsPostgreSQL connection limitMEDIUMPgBouncer connection poolingAudio stream bandwidthLOWMelodix doesn't proxy audio — JioSaavn CDN absorbs itAI Service (Claude API) latencyMEDIUMAsync job queue; user sees loading stateMusicBrainz rate limit (1 req/sec)HIGHCache all MB data; bulk pre-populate during off-peak


11. Logging & Monitoring

Logs


Format: Structured JSON (pino logger in Node.js; structlog in Python)
Levels: ERROR, WARN, INFO, DEBUG (INFO in production)
Mandatory fields: timestamp, service, traceId, spanId, userId (if authed), level, message
Shipping: Fluentd → AWS CloudWatch Logs → Grafana Loki (for long-term)


Metrics


Tool: Prometheus (scraped every 15s)
Key metrics per service: HTTP request rate, P50/P95/P99 latency, error rate, active connections, memory/CPU usage
Business metrics: Active streams, downloads initiated, AI playlist jobs queued


Tracing


Tool: OpenTelemetry SDK in all services → AWS X-Ray backend
Trace ID propagated via traceparent header
Critical paths traced: auth flow, song play flow, AI playlist generation


Alerting (PagerDuty / Slack)

AlertConditionSeverityHigh error rate5xx rate >1% for 5 minP1Stream URL fetch failuresaavn.me error rate >10%P1Payment webhook failure>3 failures in 10 minP1High latencyP95 API >2s for 5 minP2DB connection pool saturatedPool usage >90%P2Redis memory >80%Memory utilisation >80%P2


12. Testing Strategy

Unit Testing


Tools: Vitest (frontend + Node.js backend), pytest (Python AI service)
Test all business logic in service layer; mock repositories
Coverage target: ≥80% lines across all services


Integration Testing


Test each service with a real Postgres + Redis (Docker Compose in CI)
Test JioSaavn API proxy with recorded cassettes (nock/VCR)
Test payment webhooks with Razorpay test mode


API Testing


Tool: Hurl / Postman collections run in CI
Every API endpoint tested: happy path + 4 edge cases + auth failure
Contract testing: Pact for inter-service contracts


End-to-End Testing


Tool: Playwright (web) + Detox (React Native)
Core flows: register → search → play song → create playlist → add song → share
Run nightly on staging environment


Coverage Targets

LayerTargetBackend services (unit)≥80%API endpoints (integration)100%Critical user flows (E2E)5 core flows coveredFrontend components (unit)≥70%


13. DevOps & Deployment

CI/CD Pipeline (GitHub Actions)

PR created
  → Lint (ESLint, Prettier, mypy)
  → Unit tests (Vitest, pytest)
  → Integration tests (Docker Compose)
  → Security scan (Trivy container scan, npm audit)
  → Build Docker images
  → Push to ECR (tagged with commit SHA)

PR merged to main
  → Deploy to Staging (EKS staging namespace)
  → Run E2E tests on staging
  → Notify Slack

Manual approval → Deploy to Production
  → Rolling update in EKS (zero-downtime)
  → Post-deploy smoke test (10 critical API checks)
  → Notify Slack with deploy summary

Environments

EnvironmentPurposeDataDevelopmentLocal developer setup (Docker Compose)Seeded test dataStagingIntegration testing, product reviewAnonymised production snapshotProductionLive usersReal data

Docker Strategy


Each service has its own Dockerfile (multi-stage build)
Base images: node:20-alpine (Node.js), python:3.12-slim (Python)
Images scanned for CVEs before push (Trivy)
Non-root user in all containers


Cloud Infrastructure (Terraform)


All AWS resources defined in Terraform modules
Separate state per environment (S3 backend + DynamoDB lock)
Modules: vpc, eks, rds, elasticache, s3, cloudfront, route53



14. Backup & Disaster Recovery

Backup Schedule


RDS PostgreSQL: Automated daily snapshots (retained 7 days); transaction log backups every 5 minutes (PITR to 5-minute granularity)
Redis: RDB snapshots every 1 hour; AOF persistence enabled
S3 (avatars, manifests): Versioning enabled; Cross-Region Replication to ap-southeast-1


Recovery Plan

ScenarioRTORPORecovery StepsSingle EKS node failure<5 min0 (stateless)Kubernetes reschedules pods automaticallyRDS primary failure<2 min<5 minRDS Multi-AZ automatic failoverEntire AZ failure<15 min<5 minEKS multi-AZ; RDS failover to standbyFull region failure<4 hours<1 hourManual failover to ap-southeast-1 (Singapore)Data corruption<2 hours<5 minPITR restore to pre-corruption timestamp

Business Continuity Strategy


Runbooks documented for all disaster scenarios in Confluence/Notion
DR drill conducted quarterly
On-call rotation: minimum 2 engineers with production access at all times
Incident response process: PagerDuty alert → 5min acknowledge → 15min triage → 1hr resolution target (P1)



15. Risks & Mitigation

Technical Risks

RiskProbabilityImpactMitigationJioSaavn API deprecation / rate limitingHIGHCRITICALMulti-source fallback (BhariyaMusic, other unofficial APIs); circuit breaker patternsaavn.me self-hosted instance downtimeMEDIUMHIGHDeploy own saavn.me fork on separate infra; health-check with auto-failoverAudio stream URL TTL changesMEDIUMHIGHMonitor TTL experimentally; adaptive cache TTLReact Native upgrade breaking audioMEDIUMMEDIUMPin RN version; delay upgrades until testedMeilisearch index desyncLOWMEDIUMRebuild index nightly; version stamp on each document

Security Risks

RiskProbabilityImpactMitigationRefresh token theftLOWCRITICALRotation + family invalidation; device fingerprintingDDoS on APIMEDIUMHIGHAWS Shield Standard + WAF rate limitingOffline file extractionLOWMEDIUMPer-device AES keys; encrypted even at app sandbox levelSQL injectionLOWCRITICALParameterised queries enforced via Prisma ORMDependency vulnerabilityHIGHMEDIUMDependabot + weekly npm audit; Trivy in CI

Scaling Risks

RiskProbabilityImpactMitigationViral growth spike (10× overnight)MEDIUMHIGHAuto-scaling configured; load test at 3× expected peak before launchDatabase connection exhaustionMEDIUMHIGHPgBouncer pooling; read replicas for heavy queriesRedis memory OOMMEDIUMHIGHMemory alerts at 80%; eviction policy allkeys-lru

Business Risks

RiskProbabilityImpactMitigationLegal action for unofficial API usageMEDIUMCRITICALLegal review before commercial launch; implement DMCA takedown processJioSaavn launches competing product with API blocksHIGHCRITICALMaintain multi-source fallback architecture from day 1Low premium conversionMEDIUMHIGHA/B test premium positioning; focus offline+ad-free as core valueApp Store rejection (iOS)LOWHIGHComply with App Store guidelines; don't mention unofficial APIs in metadata



FINAL DELIVERABLES


1. Development Roadmap

Phase 0 — Foundation (Weeks 1–4)


Project scaffolding (monorepo, CI/CD, Docker, Terraform)
Auth service (complete)
Music service (JioSaavn proxy + basic stream)
React web app shell with player
PostgreSQL schema v1


Phase 1 — MVP Core (Weeks 5–12)


Complete playback engine (queue, shuffle, repeat, crossfade)
Search & discovery (basic)
Liked Songs + basic playlists
Lyrics (synced)
Android + iOS apps (React Native)
Subscription + Razorpay integration
Analytics event ingestion


Phase 2 — Growth Features (Weeks 13–20)


AI Playlist Generation (Claude API)
Daily Mix + Discover Weekly personalisation
Social features (friend activity, collaborative playlists, Blend)
Offline downloads (premium)
Artist Radio
Melodix Wrapped (monthly + annual)
i18n (10 languages)


Phase 3 — Scale & Polish (Weeks 21–26)


Performance hardening
Full observability stack
Accessibility audit + WCAG 2.1 AA remediation
Security penetration test
App Store + Play Store submission
Public launch



2. Milestone Plan

MilestoneDeliverableTarget WeekM1: Infrastructure ReadyCI/CD live, all services deployableWeek 4M2: Alpha PlaybackWeb app plays songs end-to-endWeek 6M3: Mobile AlphaiOS + Android play songsWeek 10M4: Closed Beta500 invited users, core featuresWeek 14M5: Payments LivePremium subscription workingWeek 16M6: AI PlaylistsPrompt-to-playlist workingWeek 18M7: Social FeaturesFriend activity + collaborative playlistsWeek 20M8: Public BetaInvite links, 5,000 usersWeek 22M9: LaunchApp Store + Play Store liveWeek 26


3. Sprint Breakdown (2-week sprints)

SprintFocusKey DeliverablesS1FoundationMonorepo setup, Docker Compose, CI/CD pipeline, auth service skeletonS2Auth CompleteLogin/register/OAuth, JWT, user profile CRUDS3Music ProxyJioSaavn API integration, stream URL caching, song metadata APIS4Player WebReact player component, queue, playback controls, mini-playerS5LibraryLiked Songs, playlist CRUD, library screenS6SearchMeilisearch integration, search UI, artist/album pagesS7Mobile BootstrapReact Native app, navigation, player screen on mobileS8Lyrics + QualitySynced lyrics, audio quality selector, gapless playbackS9PaymentsRazorpay integration, premium flags, webhook handlingS10AI PlaylistsClaude API integration, prompt → playlist flow, async job queueS11Social P1Friend follows, activity feed, share cardsS12Offline DownloadsDownload manager, encrypted storage, offline playbackS13DiscoveryDaily Mix, Discover Weekly, Artist Radio


4. Database Schema Summary

TableRows (at 100K users)Primary KeyKey Relationshipsusers100,000UUID→ subscriptions, playlists, liked_songssongs~5,000,000UUID← playlists (via junction), liked_songsartists~500,000UUID← songsalbums~1,000,000UUID← songsplaylists~500,000UUID→ playlist_songsplaylist_songs~5,000,000UUID→ playlists, songsliked_songs~3,000,000(user_id, song_id)→ users, songslistening_history~500,000,000UUIDPartitioned monthlysocial_graph~1,000,000(follower, following)→ userssubscriptions~8,000UUID→ usersdownloads~2,000,000UUID→ users, songs


5. API Summary Table

EndpointMethodAuthServicePriority/auth/registerPOSTNoneAuthP0/auth/loginPOSTNoneAuthP0/auth/refreshPOSTCookieAuthP0/auth/logoutDELETEBearerAuthP0/auth/oauth/googlePOSTNoneAuthP0/music/stream/{id}GETBearerMusicP0/music/songs/{id}GETBearerMusicP0/music/songs/{id}/lyricsGETBearerMusicP1/music/searchGETBearerMusicP0/music/daily-mixesGETBearerMusicP1/music/artist-radio/{id}GETBearerMusicP1/playlistsGET, POSTBearerPlaylistP0/playlists/{id}GET, PUT, DELETEBearerPlaylistP0/playlists/{id}/songsPOST, DELETEBearerPlaylistP0/playlists/{id}/collaboratorsPOST, DELETEBearerPlaylistP1/ai/playlistPOSTBearerAIP1/ai/playlist/{jobId}GETBearerAIP1/social/feedGETBearerSocialP1/social/follow/{userId}POST, DELETEBearerSocialP1/social/blend/{userId}POSTBearerSocialP2/analytics/eventPOSTBearerAnalyticsP0/analytics/wrappedGETBearerAnalyticsP2/payments/subscribePOSTBearerPaymentP1/payments/cancelDELETEBearerPaymentP1/payments/webhookPOSTRazorpay SigPaymentP1


6. Recommended Folder Structure (Monorepo)

melodix/
├── apps/
│   ├── web/                    # React PWA
│   └── mobile/                 # React Native + Expo
├── packages/
│   ├── ui/                     # Shared UI components
│   ├── types/                  # Shared TypeScript types
│   ├── utils/                  # Shared utilities
│   └── config/                 # ESLint, Prettier configs
├── services/
│   ├── auth-service/           # Node.js + Fastify
│   ├── music-service/          # Node.js + Fastify
│   ├── playlist-service/       # Node.js + Fastify
│   ├── social-service/         # Node.js + Fastify
│   ├── ai-service/             # Python + FastAPI
│   ├── payment-service/        # Node.js + Fastify
│   └── analytics-service/      # Node.js + Fastify
├── infra/
│   ├── terraform/              # AWS infra as code
│   ├── k8s/                    # Kubernetes manifests
│   └── docker/                 # Docker Compose (dev)
├── scripts/                    # DB migrations, seed scripts
├── docs/                       # ADRs, runbooks, API docs
├── .github/
│   └── workflows/              # GitHub Actions CI/CD
├── package.json                # Turborepo config
└── turbo.json


7. Recommended Tech Stack (Final)

CategoryTechnologyVersionNotesWeb FrameworkReact18With TypeScript strictBuild ToolVite5Fast HMR, excellent PWA supportMobileReact Native + Expo0.74 / SDK 52Navigation (mobile)React Navigation6State (server)TanStack Query5State (client)Zustand4StylingTailwind CSS3Web; NativeWind for mobileBackend RuntimeNode.js20 LTSBackend FrameworkFastify4ORMPrisma5ValidationZod3AI ServicePython FastAPI0.111AI ModelClaude Sonnet 4.6via APIPlaylist generationDatabasePostgreSQL16via AWS RDSCacheRedis7via AWS ElastiCacheSearchMeilisearch1.8Self-hosted on EKSObject StorageAWS S3—CDNAWS CloudFront—PaymentsRazorpayv1AuthCustom JWT RS256—+ Google/Apple OAuthMonitoringGrafana + Prometheus—TracingOpenTelemetry—→ AWS X-RayCI/CDGitHub Actions—ContainerDocker + EKS—IaCTerraform1.8MonorepoTurborepo2


8. Estimated Development Timeline

PhaseDurationTeamEffortPhase 0: Foundation4 weeks3 engineers12 person-weeksPhase 1: MVP Core8 weeks5 engineers40 person-weeksPhase 2: Growth Features8 weeks6 engineers48 person-weeksPhase 3: Scale & Launch6 weeks6 engineers36 person-weeksTotal26 weeks (6 months)Peak: 6136 person-weeks


9. Team Requirements

RoleCountKey SkillsWhen NeededFull-Stack Lead1React, Node.js, ArchitectureFrom Day 1Backend Engineer2Node.js, Fastify, PostgreSQL, RedisFrom Day 1Frontend Engineer1React, TypeScript, PWAFrom Week 2React Native Engineer1RN, Expo, Audio APIsFrom Week 5AI/ML Engineer1Python, FastAPI, Claude APIFrom Week 9DevOps Engineer1AWS, Terraform, KubernetesFrom Week 1 (part-time until Week 4)Product Designer1Figma, Mobile UX, Design SystemsFrom Day 1QA Engineer1Playwright, Detox, API testingFrom Week 8


10. Cost Estimation

Infrastructure (Monthly, at launch ~1K DAU)

ServiceConfigMonthly CostEKS + EC2 (3× t3.large)3 nodes~$220RDS PostgreSQL (t3.medium Multi-AZ)db.t3.medium~$80ElastiCache Redis (cache.t3.micro)Single node~$15S3 (100GB storage + transfer)Standard~$10CloudFront (1TB transfer)—~$85Route53 + ACM—~$5Total AWS (MVP)~$415/month

Third-Party APIs (Monthly)

ServiceCostClaude API (AI playlists, est. 5K calls/day)~$150/monthRazorpay (2% per transaction, estimated 500 subs)₹~490 (~$6) per ₹49 plan = payment gateway cost built into pricingLast.fm APIFree (non-commercial)MusicBrainzFree (self-imposed rate limits)JioSaavn Unofficial (saavn.me)Free (self-hosted on Vercel/Render)

Development Cost (6-month project, India team)

RoleCountMonthly (INR)6-Month TotalFull-Stack Lead1₹3,00,000₹18,00,000Backend Engineers2₹2,00,000 each₹24,00,000Frontend Engineer1₹2,00,000₹12,00,000React Native Engineer1₹2,50,000₹15,00,000AI Engineer1₹2,50,000₹15,00,000DevOps (part-time)1₹1,50,000₹9,00,000Designer1₹1,50,000₹9,00,000QA1₹1,20,000₹7,20,000Total Development~₹1,09,20,000 (~$130K USD)



SELF-REVIEW: GAPS, RISKS & RECOMMENDATIONS

Missing Requirements Identified


Push Notifications: No FCM/APNs notification system specified. Required for: "Friend started listening to your playlist", new releases from followed artists, subscription expiry reminders. Recommendation: Add a Notification Service using Firebase Cloud Messaging.
Content Moderation: User-generated content (playlist names, profile bios, collaborative playlist actions) has no moderation pipeline. Recommendation: Add AI-based content moderation (Perspective API or Claude classifier) on all UGC fields.
Equaliser: Mentioned in product features but no technical specification for Web Audio API or native audio processing. Recommendation: Implement 10-band EQ via Web Audio API (BiquadFilterNode) on web; use react-native-track-player's EQ integration on mobile.
Error Reporting: No Sentry/Bugsnag crash reporting specified for client apps. Recommendation: Add Sentry SDK on all clients; set up error budget alerting.
App Update Strategy: No over-the-air (OTA) update strategy for React Native. Recommendation: Use Expo Updates (EAS Update) for JS bundle OTA updates; critical bug fixes without App Store review.


Security Gaps


API Key for saavn.me not rotated: If the self-hosted saavn.me instance is compromised, all stream URLs could be poisoned. Recommendation: Verify stream URL checksums; HTTPS only for saavn.me instance.
No MFA option specified: For premium and admin accounts. Recommendation: Add TOTP-based 2FA as optional but recommended for premium users.
Insider threat on analytics data: The analytics service has broad read access to listening history. Recommendation: Implement column-level access control; analytics team gets only anonymised exports.


Scalability Concerns


Meilisearch single instance: At 5M+ songs, a single Meilisearch node could become a bottleneck. Recommendation: Plan for Meilisearch Cloud or sharded self-hosted setup in Phase 2.
Listening history table growth: At 35K DAU × 10 plays/day = 350K events/day = ~128M/year. Partitioning helps but cold query performance needs validation. Recommendation: Move analytics queries to a separate OLAP store (ClickHouse or Redshift) by Phase 2.
AI Playlist job queue: SQS-based async is fine for MVP but at 20K AI calls/day, Claude API costs become ~$600/month. Recommendation: Implement semantic caching: if two prompts are >90% similar (cosine similarity of embeddings), return the same playlist result.


UX Issues


Onboarding drop-off risk: The 3-step onboarding (language → genres → artists) may lose users. Recommendation: Make step 2 and 3 skippable; show "complete your profile for better recommendations" banner later.
Offline content discoverability: Users may not realise they have offline songs when they lose connectivity. Recommendation: Auto-switch to offline mode with a clear banner and "Downloaded songs" highlighted in Library.
Collaborative playlist conflict UI: When two users edit a playlist simultaneously, there's no conflict resolution UX specified. Recommendation: Use operational transforms (or last-write-wins with 5-second undo) and show "X just added Y" toasts.


Technical Debt Risks


Unofficial API dependency: The entire streaming layer depends on saavn.me — an unofficial, unsupported API. This is the single biggest technical debt item. Recommendation: Abstract the streaming layer behind a StreamProvider interface from day 1, so swapping providers requires changing only one module, not the entire music service.
Denormalised counters (play_count, song_count): These will drift under high concurrency. Recommendation: Use Redis atomic INCR for counters; reconcile with DB via nightly batch job.
Prisma ORM in high-throughput paths: Prisma adds latency overhead in hot paths (stream URL fetch). Recommendation: Use raw PostgreSQL queries (via pg directly) for the top 5 most-called DB operations; Prisma for everything else.