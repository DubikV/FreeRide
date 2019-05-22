package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class PoiDetailed(
    @field:SerializedName("id") var id: String?,

    @field:SerializedName("name") var name: String?,

    @field:SerializedName("address") var address: String?,

    @field:SerializedName("types") var types: List<String>?,

    @field:SerializedName("reviews") var reviews: Int,

    @field:SerializedName("accessible") var accessible: Double,

    @field:SerializedName("workinghours") var workinghours: List<String>?,

    @field:SerializedName("location") var location: PoiLocation?
)
