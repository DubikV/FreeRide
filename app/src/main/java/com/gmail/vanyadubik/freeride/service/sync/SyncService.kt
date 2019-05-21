package com.gmail.vanyadubik.freeride.service.sync

import com.gmail.vanyadubik.freeride.common.Consts.LOGIN_URL
import com.gmail.vanyadubik.freeride.common.Consts.LOGOUT_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_DETAIL_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_REVIEW_URL
import com.gmail.vanyadubik.freeride.common.Consts.POI_URL
import com.gmail.vanyadubik.freeride.common.Consts.RECOVERY_URL
import com.gmail.vanyadubik.freeride.common.Consts.REGISTER_URL
import com.gmail.vanyadubik.freeride.common.Consts.TOKEN_HEADER
import com.gmail.vanyadubik.freeride.common.Consts.USER_AVATAR_URL
import com.gmail.vanyadubik.freeride.common.Consts.USER_DETAIL_URL
import com.gmail.vanyadubik.freeride.model.dto.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface SyncService {

    @GET(POI_URL)
    fun getByPointName(@Query("searchString") searchString: String): Observable<List<Poi>>

    @GET(POI_DETAIL_URL)
    fun getDetailPoint(@Path("id") id: String): Observable<PoiDetailed>

    @GET(POI_REVIEW_URL)
    fun getReview(@Path("id") id: String,
                  @Query("offset") offset: Int,
                  @Query("limit") limit: Int): Observable<List<PoiReview>>

    @POST(POI_REVIEW_URL)
    fun setReview(@Path("id") id: String,
                  @Body newReviewRequest: NewReviewRequest) : Observable<ResponseBody>

    @POST(REGISTER_URL)
    fun register(@Query("type") type: String,
                 @Body registerRequest: RegisterRequest): Observable<RegisterResponse>

    @POST(LOGIN_URL)
    fun login(@Query("type") type: String,
              @Body loginRequest: LoginRequest): Observable<LoginResponse>

    @POST(LOGOUT_URL)
    fun logout()

    @POST(RECOVERY_URL)
    fun userRecovery(@Body userRecovery: UserRecoveryRequest) : Observable<ResponseBody>

    @GET(USER_DETAIL_URL)
    fun getUser(@Path("id") userId: String): Observable<User>

    @POST(USER_DETAIL_URL)
    fun setUser(@Path("id") userId: String,
                @Body userNewData: UserNewData): Observable<User>

    @POST(USER_AVATAR_URL)
    fun setPhoto(@Path("id") userId: String,
                 @Part file : MultipartBody.Part)
}
