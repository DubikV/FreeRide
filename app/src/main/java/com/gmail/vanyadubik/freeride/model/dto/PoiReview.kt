package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class PoiReview(
        @field:SerializedName("accessible") var accessible: Int,

        @field:SerializedName("review") var review: String?,

        @field:SerializedName("date") var date: Long,

        @field:SerializedName("user") var user: User?
)