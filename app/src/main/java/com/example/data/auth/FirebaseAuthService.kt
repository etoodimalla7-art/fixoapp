package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.LoyaltyTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Service centralisé pour l'authentification Firebase et Google Sign-In via Credential Manager.
 * Inclut la synchronisation automatique des profils utilisateurs dans Firestore.
 */
class FirebaseAuthService(private val context: Context) {

    private val tag = "FirebaseAuthService"
    private val credentialManager = CredentialManager.create(context)

    // Initialisation sécurisée de Firebase (même en l'absence de google-services.json complet)
    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Firebase Auth initialization warning: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Firestore initialization warning: ${e.message}")
            null
        }
    }

    /**
     * Flux réactif observant l'état d'authentification Firebase en temps réel.
     */
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authInstance = auth
        if (authInstance == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        authInstance.addAuthStateListener(listener)
        awaitClose {
            authInstance.removeAuthStateListener(listener)
        }
    }

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    /**
     * Récupère le Client ID Web Google depuis les ressources générées par google-services ou le manifest.
     */
    private fun getWebClientId(): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            // Identifiant de secours pour l'environnement de développement / bac à sable
            "151148198467-auth-fixo-mobile.apps.googleusercontent.com"
        }
    }

    /**
     * Connexion via Google Sign-In avec l'API native Android Credential Manager
     * et échange du jeton ID contre les identifiants Firebase Auth.
     */
    suspend fun signInWithGoogle(preferredRole: UserRole = UserRole.CUSTOMER): Result<User> = withContext(Dispatchers.IO) {
        val authInstance = auth ?: return@withContext Result.failure(
            IllegalStateException("Firebase Auth n'est pas initialisé sur cet appareil.")
        )

        try {
            val serverClientId = getWebClientId()
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = try {
                credentialManager.getCredential(
                    request = request,
                    context = context
                )
            } catch (e: GetCredentialCancellationException) {
                return@withContext Result.failure(Exception("Connexion Google annulée par l'utilisateur."))
            } catch (e: GetCredentialException) {
                Log.w(tag, "Credential Manager error, fallback: ${e.message}")
                return@withContext Result.failure(Exception("Échec de récupération du compte Google : ${e.message}"))
            }

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)

                val authResult = authInstance.signInWithCredential(authCredential).await()
                val firebaseUser = authResult.user
                    ?: return@withContext Result.failure(Exception("Aucun utilisateur renvoyé par Firebase."))

                val domainUser = mapFirebaseUserToDomain(firebaseUser, preferredRole)
                syncUserToFirestore(domainUser)

                Result.success(domainUser)
            } else {
                Result.failure(Exception("Format d'identifiant Google inattendu."))
            }
        } catch (e: Exception) {
            Log.e(tag, "Google Sign-In failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Authentification par E-mail et Mot de passe avec Firebase Auth.
     */
    suspend fun signInWithEmail(email: String, pass: String, role: UserRole = UserRole.CUSTOMER): Result<User> = withContext(Dispatchers.IO) {
        val authInstance = auth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth non disponible."))
        try {
            val result = authInstance.signInWithEmailAndPassword(email, pass).await()
            val fbUser = result.user ?: return@withContext Result.failure(Exception("Utilisateur introuvable."))
            val domainUser = mapFirebaseUserToDomain(fbUser, role)
            syncUserToFirestore(domainUser)
            Result.success(domainUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inscription par E-mail et Mot de passe avec Firebase Auth.
     */
    suspend fun signUpWithEmail(email: String, pass: String, name: String, phone: String, role: UserRole = UserRole.CUSTOMER): Result<User> = withContext(Dispatchers.IO) {
        val authInstance = auth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth non disponible."))
        try {
            val result = authInstance.createUserWithEmailAndPassword(email, pass).await()
            val fbUser = result.user ?: return@withContext Result.failure(Exception("Impossible de créer le compte."))
            val domainUser = User(
                id = fbUser.uid,
                role = role,
                name = name.ifEmpty { fbUser.email?.substringBefore("@") ?: "Client FIXO" },
                email = fbUser.email ?: email,
                phone = phone.ifEmpty { "+237 670 000 000" },
                avatarUrl = fbUser.photoUrl?.toString() ?: "",
                verificationStatus = if (role == UserRole.WORKER) VerificationStatus.PENDING else VerificationStatus.VERIFIED_PRO,
                loyaltyTier = LoyaltyTier.BRONZE,
                city = "Douala",
                quarter = "Akwa"
            )
            syncUserToFirestore(domainUser)
            Result.success(domainUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Connexion anonyme / invité pour explorer l'application sans friction.
     */
    suspend fun signInAnonymously(role: UserRole = UserRole.CUSTOMER): Result<User> = withContext(Dispatchers.IO) {
        val authInstance = auth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth non disponible."))
        try {
            val result = authInstance.signInAnonymously().await()
            val fbUser = result.user ?: return@withContext Result.failure(Exception("Échec de connexion invité."))
            val guestUser = User(
                id = fbUser.uid,
                role = role,
                name = "Invité FIXO",
                email = "guest_${fbUser.uid.take(6)}@fixo.cm",
                phone = "+237 690 000 000",
                avatarUrl = "",
                verificationStatus = VerificationStatus.UNVERIFIED,
                loyaltyTier = LoyaltyTier.BRONZE,
                city = "Douala",
                quarter = "Akwa"
            )
            syncUserToFirestore(guestUser)
            Result.success(guestUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Déconnexion complète de Firebase Auth et réinitialisation de Credential Manager.
     */
    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            auth?.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.w(tag, "Error during signOut: ${e.message}")
        }
    }

    /**
     * Sauvegarde et synchronise le profil utilisateur dans la collection Firestore "users".
     */
    suspend fun syncUserToFirestore(user: User) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val data = hashMapOf(
                "id" to user.id,
                "role" to user.role.name,
                "name" to user.name,
                "email" to user.email,
                "phone" to user.phone,
                "avatarUrl" to user.avatarUrl,
                "rating" to user.rating,
                "balance" to user.balance,
                "escrowLocked" to user.escrowLocked,
                "fixoPoints" to user.fixoPoints,
                "verificationStatus" to user.verificationStatus.name,
                "loyaltyTier" to user.loyaltyTier.name,
                "city" to user.city,
                "quarter" to user.quarter,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("users")
                .document(user.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(tag, "User ${user.id} successfully synced to Firestore.")
        } catch (e: Exception) {
            Log.w(tag, "Firestore sync skipped or unavailable: ${e.message}")
        }
    }

    /**
     * Convertit un FirebaseUser en modèle de domaine FIXO User.
     */
    fun mapFirebaseUserToDomain(firebaseUser: FirebaseUser, role: UserRole = UserRole.CUSTOMER): User {
        return User(
            id = firebaseUser.uid,
            role = role,
            name = firebaseUser.displayName?.ifBlank { null }
                ?: firebaseUser.email?.substringBefore("@")
                ?: "Client FIXO",
            email = firebaseUser.email ?: "",
            phone = firebaseUser.phoneNumber ?: "+237 670 123 456",
            avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 150,
            verificationStatus = if (firebaseUser.isEmailVerified) VerificationStatus.VERIFIED_PRO else VerificationStatus.UNVERIFIED,
            loyaltyTier = LoyaltyTier.BRONZE,
            city = "Douala",
            quarter = "Akwa"
        )
    }
}
