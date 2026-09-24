package com.example.ui.screens.reels

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.data.model.WorkerProfile
import com.example.data.model.WorkerReview
import com.example.localization.AppLanguage
import com.example.ui.components.WorkerPassportModal
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoElectricAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CHANTIER 4 : MOTEUR VIDÉO REELS CHANTIERS (TIKTOK-STYLE & CONVERSION DIRECTE)
 *
 * 1. Immersion Plein Écran Absolu (100% Edge-to-Edge, zéro top bar, bottom scrim dégradé 40%)
 * 2. Rail Latéral d'Interactions (Avatar + [ + ], Like rebond, Avis & Preuves, WhatsApp, Mute)
 * 3. Métadonnées du Chantier (Identité @, Badge Émeraude, Broche 📍, Titre technique, Hashtags)
 * 4. Bouton de Commande Directe (52 dp ambre, conversion instantanée avec WorkerPassportModal)
 * 5. Moteur Gestuel & ExoPlayer Media3 (VerticalPager, double-tap heart, cycle de vie, pre-caching 3s, recyclage > 2)
 */
@Composable
fun ReelsFeedScreen(
    reels: List<Reel>,
    allWorkers: List<WorkerProfile>,
    reviews: List<WorkerReview> = emptyList(),
    language: AppLanguage = AppLanguage.FR,
    initialReelId: String? = null,
    onLikeReel: (String) -> Unit,
    onToggleSaveWorker: (String) -> Unit = {},
    savedWorkerIds: Set<String> = emptySet(),
    onBookFromReel: (Reel, WorkerProfile) -> Unit,
    onOpenWorkerProfile: (WorkerProfile) -> Unit,
    onReportReel: (Reel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playerPool = remember { ReelPlayerPool(context) }

    // Initial page computation
    val initialIndex = remember(reels, initialReelId) {
        if (initialReelId != null) {
            val found = reels.indexOfFirst { it.id == initialReelId }
            if (found >= 0) found else 0
        } else {
            0
        }
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { reels.size }
    )

    // Global states across the reels feed
    var globalMuted by remember { mutableStateOf(false) }
    val likedReelIds = remember { mutableStateMapOf<String, Boolean>() }
    val customLikeCounts = remember { mutableStateMapOf<String, Int>() }

    // Bottom sheet state for Reviews
    var activeReviewsReel by remember { mutableStateOf<Reel?>(null) }

    // Modal state for Direct Booking Passport
    var activeBookingReelAndWorker by remember { mutableStateOf<Pair<Reel, WorkerProfile>?>(null) }

    // Prune players when page changes (recycle players with distance > 2)
    LaunchedEffect(pagerState.currentPage, reels) {
        val allIds = reels.map { it.id }
        playerPool.prunePlayers(pagerState.currentPage, allIds, maxDistance = 2)

        // Pre-cache / prepare next video (+1) for instantaneous start
        if (pagerState.currentPage + 1 < reels.size) {
            val nextReel = reels[pagerState.currentPage + 1]
            playerPool.getOrCreatePlayer(nextReel.id, nextReel.videoUrl)
        }
    }

    // Scroll to initialReelId if changed dynamically
    LaunchedEffect(initialReelId) {
        if (initialReelId != null) {
            val targetIdx = reels.indexOfFirst { it.id == initialReelId }
            if (targetIdx >= 0 && targetIdx != pagerState.currentPage) {
                pagerState.scrollToPage(targetIdx)
            }
        }
    }

    // Release all players on disposal
    DisposableEffect(Unit) {
        onDispose {
            playerPool.releaseAll()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_feed_screen")
    ) {
        if (reels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (language == AppLanguage.FR) "Aucune vidéo de chantier disponible" else "No craftsmanship reels available",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("reels_vertical_pager")
            ) { pageIndex ->
                val reel = reels[pageIndex]
                val worker = allWorkers.find { it.id == reel.workerId }
                    ?: WorkerProfile(
                        id = reel.workerId,
                        userId = "usr_${reel.workerId}",
                        name = reel.workerName,
                        category = reel.category,
                        hourlyRate = reel.priceAmount,
                        emergencyCalloutAvailable = true,
                        bio = "Artisan Homologué FIXO",
                        skills = reel.tags,
                        certifications = "Identité Biométrique Validée (CNI), Casier Vierge (Bulletin n°3)",
                        completedJobs = 95,
                        rating = 4.9,
                        reviewCount = reel.reviewsCount,
                        avatarUrl = reel.workerAvatar,
                        locationCity = reel.location,
                        locationDistanceKm = 1.2,
                        backgroundVerified = true,
                        phone = "+237 670 000 000"
                    )

                val isCurrentPage = (pagerState.currentPage == pageIndex)
                val isLiked = likedReelIds[reel.id] ?: false
                val likesCount = (customLikeCounts[reel.id] ?: reel.likesCount) + (if (isLiked) 1 else 0)
                val isFollowed = savedWorkerIds.contains(worker.id)

                ReelPageItem(
                    reel = reel,
                    worker = worker,
                    isCurrentPage = isCurrentPage,
                    isMuted = globalMuted,
                    isLiked = isLiked,
                    likesCount = likesCount,
                    isFollowed = isFollowed,
                    language = language,
                    playerPool = playerPool,
                    onToggleMute = { globalMuted = !globalMuted },
                    onToggleLike = {
                        val newLike = !isLiked
                        likedReelIds[reel.id] = newLike
                        if (newLike) onLikeReel(reel.id)
                    },
                    onToggleFollow = { onToggleSaveWorker(worker.id) },
                    onOpenReviews = { activeReviewsReel = reel },
                    onOpenArtisanProfile = { onOpenWorkerProfile(worker) },
                    onBookPrestation = {
                        activeBookingReelAndWorker = Pair(reel, worker)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Reviews Bottom Sheet
        if (activeReviewsReel != null) {
            val r = activeReviewsReel!!
            val worker = allWorkers.find { it.id == r.workerId }
                ?: WorkerProfile(
                    id = r.workerId,
                    userId = "usr_${r.workerId}",
                    name = r.workerName,
                    category = r.category,
                    hourlyRate = r.priceAmount,
                    bio = "Artisan Homologué FIXO",
                    skills = r.tags,
                    certifications = "Homologué",
                    completedJobs = 95,
                    rating = 4.9,
                    reviewCount = r.reviewsCount,
                    avatarUrl = r.workerAvatar,
                    locationCity = r.location,
                    locationDistanceKm = 1.2
                )

            val workerReviews = reviews.filter { it.workerId == worker.id }
            ReelReviewsBottomSheet(
                reel = r,
                worker = worker,
                reviews = workerReviews,
                language = language,
                onDismiss = { activeReviewsReel = null }
            )
        }

        // Direct Booking Modal (WorkerPassportModal pré-remplie avec prestation exacte du Reel)
        if (activeBookingReelAndWorker != null) {
            val (bookingReel, bookingWorker) = activeBookingReelAndWorker!!

            WorkerPassportModal(
                worker = bookingWorker,
                language = language,
                initialIsFlash = false,
                customServiceTitle = bookingReel.title,
                customPrice = bookingReel.priceAmount,
                onDismiss = { activeBookingReelAndWorker = null },
                onBookConfirmed = { isFlash, totalXaf ->
                    activeBookingReelAndWorker = null
                    onBookFromReel(bookingReel, bookingWorker)
                }
            )
        }
    }
}

/**
 * Single Reel Viewport with Video Player, Double-Tap Heart Pulse, Bottom Scrim,
 * Action Rail, Metadata Overlay, and Direct Conversion Booking CTA.
 */
@OptIn(UnstableApi::class)
@Composable
private fun ReelPageItem(
    reel: Reel,
    worker: WorkerProfile,
    isCurrentPage: Boolean,
    isMuted: Boolean,
    isLiked: Boolean,
    likesCount: Int,
    isFollowed: Boolean,
    language: AppLanguage,
    playerPool: ReelPlayerPool,
    onToggleMute: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleFollow: () -> Unit,
    onOpenReviews: () -> Unit,
    onOpenArtisanProfile: () -> Unit,
    onBookPrestation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Play/Pause indicator overlay state (shown for 300ms on tap)
    var showPlayPauseIndicator by remember { mutableStateOf(false) }
    var indicatorIsPlay by remember { mutableStateOf(true) }

    // Double-tap floating heart state
    var doubleTapHeartOffset by remember { mutableStateOf<Offset?>(null) }
    var showFloatingHeart by remember { mutableStateOf(false) }
    val floatingHeartScale = remember { Animatable(0f) }

    val exoPlayer = remember(reel.id) {
        playerPool.getOrCreatePlayer(reel.id, reel.videoUrl)
    }

    // Playback control synced with current page visibility and user play/pause state
    LaunchedEffect(isCurrentPage, isPlaying) {
        if (isCurrentPage && isPlaying) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
        }
    }

    // Audio mute synchronization
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Lifecycle events (pause on app background, resume on foreground)
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    hasError = false
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                hasError = true
                isBuffering = false
            }
        }
        exoPlayer.addListener(listener)

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (isCurrentPage && isPlaying) exoPlayer.play()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.removeListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .testTag("reel_viewport_${reel.id}")
            .pointerInput(reel.id) {
                detectTapGestures(
                    onTap = {
                        isPlaying = !isPlaying
                        indicatorIsPlay = isPlaying
                        showPlayPauseIndicator = true
                        coroutineScope.launch {
                            delay(300)
                            showPlayPauseIndicator = false
                        }
                    },
                    onDoubleTap = { offset ->
                        doubleTapHeartOffset = offset
                        if (!isLiked) onToggleLike()
                        showFloatingHeart = true
                        coroutineScope.launch {
                            floatingHeartScale.snapTo(0.2f)
                            floatingHeartScale.animateTo(
                                targetValue = 1.3f,
                                animationSpec = tween(250, easing = FastOutSlowInEasing)
                            )
                            floatingHeartScale.animateTo(
                                targetValue = 1.0f,
                                animationSpec = tween(150)
                            )
                            delay(150)
                            showFloatingHeart = false
                            floatingHeartScale.snapTo(0f)
                        }
                    }
                )
            }
    ) {
        // Video Surface via Media3 PlayerView with Fallback to Thumbnail
        if (!hasError) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = reel.thumbnailUrl,
                contentDescription = reel.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Protection de Lisibilité : Dégradé sombre vertical couvrant les 40% inférieurs (WCAG AAA)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.55f to Color.Transparent,
                            0.75f to Color(0x99080C15),
                            1.00f to Color(0xE6080C15) // rgba(8, 12, 21, 0.90)
                        )
                    )
                )
        )

        // Buffering Indicator
        if (isBuffering && !hasError) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.Center),
                color = FixoElectricAmber,
                strokeWidth = 3.dp
            )
        }

        // Tap Play/Pause 300ms Animated Feedback Icon in Center
        AnimatedVisibility(
            visible = showPlayPauseIndicator,
            enter = fadeIn(tween(100)) + scaleIn(tween(100)),
            exit = fadeOut(tween(150)) + scaleOut(tween(150)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (indicatorIsPlay) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Double-Tap Pulsing Amber Heart at Touch Offset
        if (showFloatingHeart && doubleTapHeartOffset != null) {
            val offset = doubleTapHeartOffset!!
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = FixoElectricAmber,
                modifier = Modifier
                    .offset { IntOffset(offset.x.toInt() - 36, offset.y.toInt() - 36) }
                    .size(72.dp)
                    .scale(floatingHeartScale.value)
            )
        }

        // Rail Latéral d'Interactions (Côté Droit)
        ReelActionRail(
            reel = reel,
            worker = worker,
            isLiked = isLiked,
            likesCount = likesCount,
            isFollowed = isFollowed,
            isMuted = isMuted,
            language = language,
            onLikeClick = onToggleLike,
            onFollowClick = onToggleFollow,
            onReviewsClick = onOpenReviews,
            onMuteToggle = onToggleMute,
            onOpenProfile = onOpenArtisanProfile,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 80.dp)
        )

        // Zone Inférieure : Métadonnées (Gauche) + Bouton de Commande Directe (Plein écran)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            // Métadonnées du Chantier (Zone Inférieure Gauche)
            ReelMetadataOverlay(
                reel = reel,
                worker = worker,
                language = language,
                onArtisanClick = onOpenArtisanProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 80.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Le Déclencheur Commercial : Bouton de Commande Directe — 52 dp ambre
            ReelBookingCTA(
                reel = reel,
                language = language,
                onClick = {
                    isPlaying = false
                    onBookPrestation()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
