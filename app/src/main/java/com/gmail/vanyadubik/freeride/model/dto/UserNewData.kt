package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class UserNewData(
        @field:SerializedName("id") var id: Int,

        @field:SerializedName("name") var name: String
)