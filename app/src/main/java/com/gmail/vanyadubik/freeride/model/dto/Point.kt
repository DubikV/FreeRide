package com.gmail.vanyadubik.freeride.model.dto

import com.google.gson.annotations.SerializedName

class Point(
    @field:SerializedName("id")
    var id: String?, @field:SerializedName("name")
    var name: String?, @field:SerializedName("address")
    var address: String?
)
