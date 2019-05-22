package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class LoginResponse(
        @field:SerializedName("id") var id: Int,

        @field:SerializedName("login") var login: String?,

        @field:SerializedName("name") var name: String?,

        @field:SerializedName("image") var image: String?,

        @field:SerializedName("socialtoken") var socialtoken: String?,

        @field:SerializedName("authtoken") var authToken: String?
)