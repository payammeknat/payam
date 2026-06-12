package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    suspend fun getAiAnalysis(prompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException("کلید Gemini API یافت نشد. لطفا ابتدا کلید را در پنل Secrets آی‌پی‌کپی تنظیم کنید.")
        }
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.5f),
            systemInstruction = Content(parts = listOf(Part(text = """
                شما یک تحلیل‌گر و مشاور خبره ارشد روانشناسی صنعتی و سازمانی (I-O Psychology) هستید. 
                اطلاعات آزمون کاندیداها از جمله نمرات استاندارد ۳ بعد مهم شغلی (اضطراب شغلی، تاب‌آوری، امنیت و توانایی شغلی) را دریافت خواهید کرد.
                وظیفه شما ارائه تحلیلی عمیق، دلسوزانه، راهبردی و کاربردی به زبان فارسی در قالب ساختاری منظم، شفاف و خوانا است. 
                پیشنهادها باید معتبر، علمی و منطبق با تئوری‌های روانشناسی صنعتی باشند تا کاندیدا بتواند نقاط قوت خود را تقویت کرده و جنبه‌های تضعیف‌شده را بازسازی کند.
            """.trimIndent())))
        )
        
        val response = apiService.generateContent(apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
            ?: "پاسخی از هوش مصنوعی دریافت نشد."
    }
}
