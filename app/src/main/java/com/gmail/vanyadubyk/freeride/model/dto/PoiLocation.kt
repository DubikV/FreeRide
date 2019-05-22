package com.gmail.vanyadubyk.freeride.model.dto

import com.google.gson.annotations.SerializedName

class PoiLocation(
    @field:SerializedName("lat") var lat: Double?,

    @field:SerializedName("lng") var lng: Double?
)
