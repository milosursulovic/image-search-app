package com.example.imagesearch.api.dto

import android.os.Parcelable
import com.example.imagesearch.Constants
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String
) : Parcelable {
    val attributionUrl get() = "${Constants.API_URL}$username?utm_source=DemoApp&utm_medium=referral"
}
