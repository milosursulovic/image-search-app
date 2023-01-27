package com.example.imagesearch.api.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashPhotoDto(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("urls")
    val urls: UrlsDto,
    @SerializedName("user")
    val user: UserDto
) : Parcelable