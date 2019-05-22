package com.gmail.vanyadubyk.freeride.fragment.menu

import android.content.Context
import android.util.Log
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.common.Consts.AUTH_TOKEN
import com.gmail.vanyadubyk.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubyk.freeride.common.Consts.TYPE_LOGIN_EMAIL
import com.gmail.vanyadubyk.freeride.model.dto.LoginRequest
import com.gmail.vanyadubyk.freeride.model.dto.LoginResponse
import com.gmail.vanyadubyk.freeride.service.sync.SyncService
import com.gmail.vanyadubyk.freeride.service.sync.SyncServiceFactory
import com.gmail.vanyadubyk.freeride.utils.NetworkUtils
import com.gmail.vanyadubyk.freeride.utils.SharedStorage
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MenuRepository(private var mContex: Context, private var mainView: MenuMVPContract.View?) :
    MenuMVPContract.Repository {


    override fun loginEmail(loginRequest: LoginRequest) {

        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        SyncServiceFactory.createService(
            SyncService::class.java, mContex)
            .login(TYPE_LOGIN_EMAIL, loginRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LoginResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(loginResponse: LoginResponse) {

                    if (loginResponse == null) {
                        Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
                        mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
                        return
                    }

                    Log.i(TAGLOG_SYNC, "On next")

                    SharedStorage.setString(mContex, AUTH_TOKEN, loginResponse?.authToken)

                    mainView?.onLogin()
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

    override fun loginFB() {
    }


//    override fun getPoiByName(name: String) {
//
//        if (!NetworkUtils.checkEthernet(mContex)) {
//            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
//            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
//            return
//        }
//
//        mainView?.onStartLoadSearch()
//
//
//
//        SyncServiceFactory.createService(
//                SyncService::class.java, mContex)
//                .getByPointName(name, SharedStorage.getString(mContex, TOKEN, ""))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<List<Poi>> {
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//
//                    override fun onNext(list: List<Poi>) {
//
//                        if (list == null) {
//                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
//                            mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
//                            return
//                        }
//
//                        Log.i(TAGLOG_SYNC, "On next")
//                        mainView?.onShowPoiList(list)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        if (!NetworkUtils.checkEthernet(mContex)) {
//                            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
//                            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
//                            return
//                        }
//
//                        Log.e(TAGLOG_SYNC, e.toString())
//                        mainView?.onErrorApi(NetworkUtils.getConnectExeption(mContex, e, e.message.toString()))
//                    }
//
//                    override fun onComplete() {}
//                })
//
//    }

}