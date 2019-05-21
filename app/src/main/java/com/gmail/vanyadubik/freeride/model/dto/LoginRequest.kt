package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class LoginRequest(

        @field:SerializedName("login") var login: String,

        @field:SerializedName("password") var password: String,

        @field:SerializedName("socialtoken") var socialToken: String

)