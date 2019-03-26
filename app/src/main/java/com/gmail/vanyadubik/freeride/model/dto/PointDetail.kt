package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class PointDetail(
    @field:SerializedName("id")
    var id: String?, @field:SerializedName("name")
    var name: String?, @field:SerializedName("address")
    var address: String?, @field:SerializedName("type")
    var type: String?, @field:SerializedName("reviews")
    var reviews: Int,
    @field:SerializedName("accessible")
    var accessible: Int, @field:SerializedName("workinghours")
    var workinghours: List<WorkingHour>?
)
