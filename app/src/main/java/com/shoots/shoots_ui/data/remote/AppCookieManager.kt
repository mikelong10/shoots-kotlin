package com.shoots.shoots_ui.data.remote

import android.content.Context
import android.webkit.CookieManager
import com.shoots.shoots_ui.data.local.UserDao
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class AppCookieManager(
    private val context: Context,
    private val userDao: UserDao
) : CookieJar {
    private val cookieManager = CookieManager.getInstance()

    init {
        cookieManager.setAcceptCookie(true)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieManager.setCookie(url.toString(), cookie.toString())

            // Ensure the userDao is not null before updating the tokens
            userDao?.let { dao ->
                when (cookie.name) {
                    "accessToken" -> {
                        runBlocking {
                            dao.updateAccessToken(cookie.value)
                        }
                    }
                    "refreshToken" -> {
                        runBlocking {
                            dao.updateRefreshToken(cookie.value)
                        }
                    }
                }
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieManager.getCookie(url.toString())
        return if (cookies != null && cookies.isNotEmpty()) {
            cookies.split(";")
                .mapNotNull { Cookie.parse(url, it.trim()) }
        } else {
            emptyList()
        }
    }

    fun clearCookies() {
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }
}