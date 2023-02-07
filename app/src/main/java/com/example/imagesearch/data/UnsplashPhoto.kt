package com.example.imagesearch.data


import android.os.Parcelable
import com.example.imagesearch.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashPhoto(
    val description: String,
    val id: String,
    val urls: Urls,
    val user: User
) : Parcelable {

    @Parcelize
    data class Urls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ) : Parcelable

    @Parcelize
    data class User(
        val name: String,
        val username: String
    ) : Parcelable {
        val attributionUrl get() = "${Constants.API_URL}$username?utm_source=DemoApp&utm_medium=referral"
    }
}