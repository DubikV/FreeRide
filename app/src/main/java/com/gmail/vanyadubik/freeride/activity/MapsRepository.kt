package com.gmail.vanyadubik.freeride.activity

import android.content.Context
import android.util.Log
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.service.sync.SyncService
import com.gmail.vanyadubik.freeride.service.sync.SyncServiceFactory
import com.gmail.vanyadubik.freeride.utils.NetworkUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapsRepository(private var mContex: Context, private var mainView: MapsMVPContract.View?) : MapsMVPContract.Repository{

    override fun getPoiByName(name: String) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        mainView?.onStartLoad()



        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .getByPointName(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Poi>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(list: List<Poi>) {

                        if (list == null) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
                            mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
                            return
                        }

                        Log.i(TAGLOG_SYNC, "On next")
                        mainView?.onShowPoiList(list)
                    }

                    override fun onError(e: Throwable) {
                        if (!NetworkUtils.checkEthernet(mContex)) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
                            return
                        }

                        Log.e(TAGLOG_SYNC, e.toString())
                        mainView?.onErrorApi(e.message.toString())
                    }

                    override fun onComplete() {}
                })

    }

    override fun getPoiDetail(id: String) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        mainView?.onStartLoad()



        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .getDetailPoint(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<PoiDetailed> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(poiDetailed: PoiDetailed) {

                        if (poiDetailed == null) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
                            mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
                            return
                        }

                        Log.i(TAGLOG_SYNC, "On next")
                        mainView?.onShowPoiDetail(poiDetailed)
                    }

                    override fun onError(e: Throwable) {
                        if (!NetworkUtils.checkEthernet(mContex)) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
                            return
                        }

                        Log.e(TAGLOG_SYNC, e.toString())
                        mainView?.onErrorApi(e.message.toString())
                    }

                    override fun onComplete() {}
                })
    }

}