package com.gmail.vanyadubyk.freeride.activity.map

import android.content.Context
import android.util.Log
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubyk.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubyk.freeride.model.dto.Poi
import com.gmail.vanyadubyk.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubyk.freeride.model.dto.PoiReview
import com.gmail.vanyadubyk.freeride.service.sync.SyncService
import com.gmail.vanyadubyk.freeride.service.sync.SyncServiceFactory
import com.gmail.vanyadubyk.freeride.utils.NetworkUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MapsRepository(private var mContex: Context, private var mainView: MapsMVPContract.View?) :
    MapsMVPContract.Repository {

    override fun getPoiByName(name: String) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        mainView?.onStartLoadSearch()



        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .getByPointName(name)
                .subscribeOn(Schedulers.io())
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
                        mainView?.onErrorApi(NetworkUtils.getConnectExeption(mContex, e, e.message.toString()))
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

        mainView?.onStartLoadDetail()


        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .getDetailPoint(id)
                .subscribeOn(Schedulers.io())
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
                        mainView?.onErrorApi(NetworkUtils.getConnectExeption(mContex, e, e.message.toString()))
                    }

                    override fun onComplete() {}
                })
    }

    override fun addReview(idPoi: String, newReviewRequest: NewReviewRequest) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

//        mainView?.onStartLoadDetail()
//        val s = Gson().toJson(newReviewRequest)

        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .setReview(idPoi, newReviewRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(responseBody: ResponseBody) {

                        Log.i(TAGLOG_SYNC, "On next")
                        mainView?.onAddReview()
                    }

                    override fun onError(e: Throwable) {
                        if (!NetworkUtils.checkEthernet(mContex)) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
                            return
                        }

                        Log.e(TAGLOG_SYNC, e.toString())
                        mainView?.onErrorApi(NetworkUtils.getConnectExeption(mContex, e, e.message.toString()))
                    }

                    override fun onComplete() {}
                })
//                .subscribe(object : Observable<ResponseBody> {
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//                    override fun onNext(responseBody: ResponseBody) {
//
//                        Log.i(TAGLOG_SYNC, "On next")
//                        mainView?.onAddReview()
//                    }
//                    override fun onError(e: Throwable) {
//                        if (!NetworkUtils.checkEthernet(mContex)) {
//                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
//                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
//                            return
//                        }
//
//                        Log.e(TAGLOG_SYNC, e.toString())
//                        mainView?.onErrorApi(e.message.toString())
//                    }
//
//                    override fun onComplete() {}
//                })

    }

    override fun getReviews(idPoi: String, fromPos: Int, limit: Int) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        mainView?.onStartLoadListReview()

        SyncServiceFactory.createService(
                SyncService::class.java, mContex)
                .getReview(idPoi, fromPos, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<PoiReview>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(list: List<PoiReview>) {

                        if (list == null) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
                            mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
                            return
                        }

                        Log.i(TAGLOG_SYNC, "On next")
                        mainView?.onShowListReviews(list)
                    }

                    override fun onError(e: Throwable) {
                        if (!NetworkUtils.checkEthernet(mContex)) {
                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
                            return
                        }

                        Log.e(TAGLOG_SYNC, e.toString())
                        mainView?.onErrorApi(NetworkUtils.getConnectExeption(mContex, e, e.message.toString()))
                    }

                    override fun onComplete() {}
                })

    }
}