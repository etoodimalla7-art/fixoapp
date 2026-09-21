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
}
