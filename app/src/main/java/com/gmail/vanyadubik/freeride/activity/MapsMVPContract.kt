package com.gmail.vanyadubik.freeride.activity

import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed

interface MapsMVPContract {


    interface View {

        fun onShowPoiList(list: List<Poi>)

        fun onErrorApi(textError: String)

        fun onStartLoad()

        fun onShowPoiDetail(poiDetailed: PoiDetailed)

    }

    interface Repository {

        fun getPoiByName(name: String)

        fun getPoiDetail(id: String)

    }


}
