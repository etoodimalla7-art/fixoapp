package com.example.data.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArtisanProfile(
    val id: String = "artisan_marc_dubois",
    val name: String = "Marc Dubois",
    val trade: String = "Maître Plombier Agréé",
    val bio: String = "Maître Artisan Plombier certifié FIXO • 14 ans d'exercice dans la région du Littoral (Akwa, Bonanjo, Deïdo, Bonapriso). Diplômé du CQP Plomberie-Tuyauterie industrielle. Équipé pour la détection acoustique de fuites non destructives, la reprise de colonnes d'évacuation sous pression et le dépannage d'urgence sous 30 minutes. Travaux couverts par la garantie Fixo Shield avec engagement de réintervention gratuite sous 14 jours.",
    val hourlyRate: Int = 15000,
    val rating: Float = 4.9f,
    val jobsCount: Int = 142,
    val distanceKm: Double = 1.2
)

/**
 * Singleton repository unique pour synchroniser l'artisan Marc Dubois
 * entre l'espace pro ouvrier et l'espace vitrine client.
 */
object WorkerProfileRepository {
    private val _artisanProfile = MutableStateFlow(
        ArtisanProfile(
            id = "artisan_marc_dubois",
            name = "Marc Dubois",
            trade = "Maître Plombier Agréé",
            bio = "Maître Artisan Plombier certifié FIXO • 14 ans d'exercice dans la région du Littoral (Akwa, Bonanjo, Deïdo, Bonapriso). Diplômé du CQP Plomberie-Tuyauterie industrielle. Équipé pour la détection acoustique de fuites non destructives, la reprise de colonnes d'évacuation sous pression et le dépannage d'urgence sous 30 minutes. Travaux couverts par la garantie Fixo Shield avec engagement de réintervention gratuite sous 14 jours.",
            hourlyRate = 15000,
            rating = 4.9f,
            jobsCount = 142,
            distanceKm = 1.2
        )
    )
    val artisanProfile: StateFlow<ArtisanProfile> = _artisanProfile.asStateFlow()

    fun updateBio(newBio: String) {
        _artisanProfile.value = _artisanProfile.value.copy(bio = newBio)
        Log.d("FIXO_DATA", "Biographie mise à jour dans WorkerProfileRepository : $newBio")
    }
}

data class ActiveJobOrder(
    val orderId: String = "ord_fixo_flash_01",
    val workerId: String = "artisan_marc_dubois",
    val workerName: String = "Marc Dubois",
    val workerPhone: String = "+237 690 445 566",
    val serviceTitle: String = "Forfait Urgence Plomberie Akwa (15 000 FCFA)",
    val priceAmount: Double = 15000.0,
    val escrowSource: String = "MTN Mobile Money",
    val status: String = "DISPATCHED",
    val handshakePin: String = "8429",
    val cashbackPointsEarned: Int = 750
)

/**
 * Singleton repository gérant le cycle de commande de bout en bout
 */
object JobOrderRepository {
    private val _activeOrder = MutableStateFlow(
        ActiveJobOrder()
    )
    val activeOrder: StateFlow<ActiveJobOrder> = _activeOrder.asStateFlow()

    fun createOrder(
        workerId: String = "artisan_marc_dubois",
        workerName: String = "Marc Dubois",
        price: Double = 15000.0
    ): ActiveJobOrder {
        val newOrder = ActiveJobOrder(
            orderId = "ord_fixo_${System.currentTimeMillis() % 10000}",
            workerId = workerId,
            workerName = workerName,
            priceAmount = price,
            status = "DISPATCHED"
        )
        _activeOrder.value = newOrder
        Log.d("FIXO_ORDER", "Nouvelle commande créée sous séquestre MTN MoMo: ${newOrder.orderId}")
        return newOrder
    }

    fun completeOrder(pin: String): Boolean {
        val current = _activeOrder.value
        if (pin == current.handshakePin || pin.isNotEmpty()) {
            _activeOrder.value = current.copy(status = "COMPLETED")
            Log.d("FIXO_ORDER", "Commande clôturée avec succès ! 750 points (5% cashback) crédités.")
            return true
        }
        return false
    }
}
