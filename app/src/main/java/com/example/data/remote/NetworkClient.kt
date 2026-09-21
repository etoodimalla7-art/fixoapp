package com.example.data.remote

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val DEFAULT_BASE_URL = "https://fixo-api.fixo.cm/"
    private var customBaseUrl: String? = null
    private var currentAuthToken: String? = null

    fun setAuthToken(token: String?) {
        currentAuthToken = token
    }

    fun setBaseUrl(url: String) {
        customBaseUrl = if (url.endsWith("/")) url else "$url/"
        retrofitInstance = null
        apiServiceInstance = null
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        currentAuthToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }
        builder.header("Accept", "application/json")
        builder.header("X-App-Platform", "Android-FIXO-Production")
        chain.proceed(builder.build())
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private var retrofitInstance: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val retrofit = Retrofit.Builder()
                .baseUrl(customBaseUrl ?: DEFAULT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            retrofitInstance = retrofit
            retrofit
        }
    }

    private var apiServiceInstance: FixoApiService? = null

    fun getApiService(): FixoApiService {
        return apiServiceInstance ?: synchronized(this) {
            val service = getRetrofit().create(FixoApiService::class.java)
            apiServiceInstance = service
            service
        }
    }
}
