package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class UserRecoveryRequest(
        @field:SerializedName("login") var login: String
)