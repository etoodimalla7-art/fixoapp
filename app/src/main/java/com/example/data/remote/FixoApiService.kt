package com.example.data.remote

import com.example.data.model.Booking
import com.example.data.model.Reel
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.User
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkerProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginDto(
    val email: String,
    val password: String
)

data class RegisterDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String,
    val category: String? = null
)

data class GoogleAuthDto(
    val id_token: String,
    val role: String? = "CUSTOMER"
)

data class AuthResponseDto(
    val access_token: String,
    val refresh_token: String,
    val user_id: String,
    val role: String,
    val name: String,
    val email: String
)

data class CreateBookingDto(
    val worker_id: String,
    val service_id: String,
    val date: String,
    val time_slot: String,
    val address: String,
    val notes: String = "",
    val payment_method: String = "MTN_MOMO"
)

data class CreateReelDto(
    val title: String,
    val description: String,
    val category: String,
    val service_id: String,
    val video_url: String,
    val thumbnail_url: String,
    val tags: List<String> = emptyList()
)

data class DepositRequestDto(
    val amount_xaf: Double,
    val provider: String,
    val phone_number: String
)

data class WithdrawRequestDto(
    val amount_xaf: Double,
    val provider: String,
    val phone_number: String
)

data class CancelBookingDto(
    val reason: String
)

data class ReviewBookingDto(
    val rating: Float,
    val review_text: String
)

data class CreateDisputeDto(
    val reason: String,
    val description: String,
    val evidence_urls: List<String> = emptyList()
)

data class UpdateLocationDto(
    val latitude: Double,
    val longitude: Double,
    val speed_kmh: Float = 0f,
    val bearing_degrees: Float = 0f,
    val accuracy_meters: Float = 0f
)

data class SendChatMessageDto(
    val text: String,
    val attachment_url: String? = null,
    val attachment_type: String? = null
)

data class ConversationDto(
    val id: String,
    val booking_id: String? = null,
    val customer_id: String,
    val customer_name: String,
    val worker_id: String,
    val worker_name: String,
    val last_message: String = "",
    val last_message_time: Long = 0L,
    val customer_unread: Int = 0,
    val worker_unread: Int = 0
)

data class ChatMessageDto(
    val id: String,
    val conversation_id: String,
    val booking_id: String? = null,
    val sender_id: String,
    val sender_name: String,
    val sender_role: String,
    val recipient_id: String,
    val text: String,
    val attachment_url: String? = null,
    val attachment_type: String? = null,
    val delivery_status: String = "SENT",
    val is_read: Boolean = false,
    val created_at: Long = 0L
)

data class NotificationDto(
    val id: String,
    val user_id: String,
    val category: String,
    val title: String,
    val body: String,
    val reference_id: String? = null,
    val deep_link: String? = null,
    val is_read: Boolean = false,
    val created_at: Long = 0L
)

data class WorkerLocationResponseDto(
    val booking_id: String,
    val worker_id: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed_kmh: Float = 0f,
    val bearing_degrees: Float = 0f,
    val accuracy_meters: Float = 0f,
    val is_active: Boolean = true,
    val updated_at: Long = 0L
)

interface FixoApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterDto): Response<AuthResponseDto>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginDto): Response<AuthResponseDto>

    @POST("api/v1/auth/google")
    suspend fun googleAuth(@Body body: GoogleAuthDto): Response<AuthResponseDto>

    @GET("api/v1/auth/me")
    suspend fun getMe(): Response<User>

    @GET("api/v1/workers")
    suspend fun getWorkers(
        @Query("category") category: String? = null,
        @Query("city") city: String? = null,
        @Query("verified_only") verifiedOnly: Boolean = false,
        @Query("emergency_only") emergencyOnly: Boolean = false,
        @Query("query") query: String? = null
    ): Response<List<WorkerProfile>>

    @GET("api/v1/workers/{id}")
    suspend fun getWorkerProfile(@Path("id") workerId: String): Response<WorkerProfile>

    @GET("api/v1/workers/{id}/services")
    suspend fun getWorkerServices(@Path("id") workerId: String): Response<List<ServiceItem>>

    @GET("api/v1/bookings")
    suspend fun getBookings(): Response<List<Booking>>

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body body: CreateBookingDto): Response<Booking>

    @PUT("api/v1/bookings/{id}/status")
    suspend fun updateJobStatus(
        @Path("id") bookingId: String,
        @Query("new_status") newStatus: String
    ): Response<Map<String, String>>

    @POST("api/v1/bookings/{id}/release")
    suspend fun releaseEscrow(
        @Path("id") bookingId: String,
        @Query("rating") rating: Float,
        @Query("review") review: String
    ): Response<Map<String, Any>>

    @GET("api/v1/reels")
    suspend fun getReels(@Query("category") category: String? = null): Response<List<Reel>>

    @POST("api/v1/reels")
    suspend fun createReel(@Body body: CreateReelDto): Response<Reel>

    @POST("api/v1/reels/{id}/like")
    suspend fun likeReel(@Path("id") reelId: String): Response<Map<String, String>>

    @DELETE("api/v1/reels/{id}")
    suspend fun deleteReel(@Path("id") reelId: String): Response<Map<String, String>>

    @GET("api/v1/wallet/transactions")
    suspend fun getTransactions(): Response<List<WalletTransaction>>

    @POST("api/v1/wallet/deposit/initiate")
    suspend fun initiateDeposit(@Body body: DepositRequestDto): Response<Map<String, Any>>

    @POST("api/v1/wallet/withdraw/request")
    suspend fun requestWithdrawal(@Body body: WithdrawRequestDto): Response<Map<String, Any>>

    @POST("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: String,
        @Body body: CancelBookingDto
    ): Response<Map<String, Any>>

    @POST("api/v1/bookings/{id}/review")
    suspend fun reviewBooking(
        @Path("id") bookingId: String,
        @Body body: ReviewBookingDto
    ): Response<Map<String, String>>

    @POST("api/v1/bookings/{id}/dispute")
    suspend fun createDispute(
        @Path("id") bookingId: String,
        @Body body: CreateDisputeDto
    ): Response<Map<String, Any>>

    @POST("api/v1/bookings/{id}/location")
    suspend fun updateWorkerLocation(
        @Path("id") bookingId: String,
        @Body body: UpdateLocationDto
    ): Response<Map<String, String>>

    @GET("api/v1/bookings/{id}/location")
    suspend fun getWorkerLocation(
        @Path("id") bookingId: String
    ): Response<WorkerLocationResponseDto>

    @GET("api/v1/chat/conversations")
    suspend fun getConversations(): Response<List<ConversationDto>>

    @POST("api/v1/chat/conversations")
    suspend fun getOrCreateConversation(
        @Query("worker_id") workerId: String,
        @Query("booking_id") bookingId: String? = null
    ): Response<ConversationDto>

    @GET("api/v1/chat/conversations/{id}/messages")
    suspend fun getChatMessages(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 50
    ): Response<List<ChatMessageDto>>

    @POST("api/v1/chat/conversations/{id}/messages")
    suspend fun sendChatMessage(
        @Path("id") conversationId: String,
        @Body body: SendChatMessageDto
    ): Response<ChatMessageDto>

    @PUT("api/v1/chat/conversations/{id}/read")
    suspend fun markChatRead(
        @Path("id") conversationId: String
    ): Response<Map<String, String>>

    @GET("api/v1/notifications")
    suspend fun getNotifications(): Response<List<NotificationDto>>

    @PUT("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") notificationId: String
    ): Response<Map<String, String>>

    @PUT("api/v1/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<Map<String, String>>
}
