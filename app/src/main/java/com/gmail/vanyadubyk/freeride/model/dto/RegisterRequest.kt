package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class RegisterRequest(

        @field:SerializedName("login") var login: String,

        @field:SerializedName("name") var name: String,

        @field:SerializedName("password") var password: String,

        @field:SerializedName("socialtoken") var socialToken: String

)