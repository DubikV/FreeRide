package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class NewReviewRequest(
        @field:SerializedName("accessible") var accessible: Int,

        @field:SerializedName("review") var review: String?,

        @field:SerializedName("date") var date: Long
)