package com.gmail.vanyadubik.freeride.service.sync

import com.gmail.vanyadubik.freeride.model.dto.Point
import com.gmail.vanyadubik.freeride.model.dto.PointDetail
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import com.gmail.vanyadubik.freeride.common.Consts.POI_DETAIL_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_URL

interface SyncService {

    @GET(POI_URL)
    fun getByPointName(@Query("searchString ") searchString: String): Observable<List<Point>>

    @GET(POI_DETAIL_URL)
    fun getByDetailPoint(@Path("id") id: String): Observable<PointDetail>
}
