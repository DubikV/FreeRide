package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class LoginRequest(

        @field:SerializedName("email") var email: String,

        @field:SerializedName("password") var password: String,

        @field:SerializedName("socialtoken") var socialToken: String

)