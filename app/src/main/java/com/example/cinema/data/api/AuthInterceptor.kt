package com.example.cinema.data.api

import android.util.Log
import com.example.cinema.data.local.AuthStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authStorage: AuthStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Логируем исходный запрос
        Log.d("AuthInterceptor", "Original Request: ${originalRequest.url}")

        val token = authStorage.getToken()


        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()

        // Логируем запрос с заголовками
        Log.d("AuthInterceptor", "Request Headers: ${request.headers}")

        val response = chain.proceed(request)

        // Логируем ответ
        Log.d("AuthInterceptor", "Response Code: ${response.code}")
        Log.d("AuthInterceptor", "Response Headers: ${response.headers}")

        if (response.isRedirect) {
            response.close()

            val redirectRequest = response.request.newBuilder()
                .header("Authorization", "Bearer $token") // Добавляем токен в редирект
                .build()

            // Логируем редирект
            Log.d("AuthInterceptor", "Redirecting to: ${redirectRequest.url}")
            return chain.proceed(redirectRequest)
        }

        return response
    }
}
