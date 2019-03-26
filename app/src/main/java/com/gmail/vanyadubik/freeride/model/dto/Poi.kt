package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class Poi(
    @field:SerializedName("id") var id: String?,

    @field:SerializedName("name") var name: String?,

    @field:SerializedName("address") var address: String?
)
