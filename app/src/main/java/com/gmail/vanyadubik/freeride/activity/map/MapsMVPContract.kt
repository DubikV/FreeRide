package com.gmail.vanyadubik.freeride.activity.map

import com.gmail.vanyadubik.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.model.dto.PoiReview

interface MapsMVPContract {


    interface View {

        fun onShowPoiList(list: List<Poi>)

        fun onErrorApi(textError: String)

        fun onStartLoadSearch()

        fun onShowPoiDetail(poiDetailed: PoiDetailed)

        fun onStartLoadDetail()

        fun onAddReview()

        fun onShowListReviews(list: List<PoiReview>)

        fun onStartLoadListReview()

    }

    interface Repository {

        fun getPoiByName(name: String)

        fun getPoiDetail(id: String)

        fun addReview(idPoi: String, newReviewRequest: NewReviewRequest)

        fun getReviews(idPoi: String, fromPos: Int, limit: Int)

    }


}
