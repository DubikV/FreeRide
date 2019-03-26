package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class User(
        @field:SerializedName("id") var id: Int,

        @field:SerializedName("login") var login: String?,

        @field:SerializedName("password") var password: String?,

        @field:SerializedName("name") var name: String?,

        @field:SerializedName("image") var image: String?,

        @field:SerializedName("socialtoken") var socialToken: String?,

        @field:SerializedName("authtoken") var authToken: String?
)