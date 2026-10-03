package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room représentant un artisan favori sauvegardé localement par l'utilisateur.
 * Permet un accès rapide et hors-ligne aux professionnels préférés depuis la vitrine.
 */
@Entity(tableName = "favorite_artisans")
data class FavoriteArtisan(
    @PrimaryKey val workerId: String,
    val workerName: String,
    val category: String,
    val avatarUrl: String,
    val hourlyRate: Double,
    val rating: Double,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0,
    val locationCity: String = "Douala",
    val distanceKm: Double = 1.2,
    val savedAt: Long = System.currentTimeMillis()
)
