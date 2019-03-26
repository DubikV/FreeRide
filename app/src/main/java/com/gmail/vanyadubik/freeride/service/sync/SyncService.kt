package com.gmail.vanyadubik.freeride.service.sync

import com.gmail.vanyadubik.freeride.common.Consts.POI_DETAIL_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_REVIEW_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_URL
import com.gmail.vanyadubik.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.model.dto.PoiReview
import io.reactivex.Observable
import retrofit2.http.*

interface SyncService {

    @GET(POI_URL)
    fun getByPointName(@Query("searchString") searchString: String): Observable<List<Poi>>

    @GET(POI_DETAIL_URL)
    fun getDetailPoint(@Path("id") id: String): Observable<PoiDetailed>

    @GET(POI_REVIEW_URL)
    fun getReview(@Path("id") id: String,
                  @Query("offset") offset: Int,
                  @Query("limit") limit: Int): Observable<PoiReview>

    @POST(POI_REVIEW_URL)
    fun setReview(@Path("id") id: String,
                  @Body newReviewRequest: NewReviewRequest)
}
