package com.example.cinema.data.local

import android.content.Context
import android.content.SharedPreferences
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.Instant
import java.util.Date

class AuthStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        return editor.commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("jwt_token")
        editor.apply()
    }

    fun isTokenExpired(): Boolean {
        val token = getToken() ?: return true
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            val expirationDate: Date = decodedJWT.expiresAt
            val currentTime = Instant.now()
            expirationDate.toInstant().isBefore(currentTime)
        } catch (e: Exception) {
            true
        }
    }
}