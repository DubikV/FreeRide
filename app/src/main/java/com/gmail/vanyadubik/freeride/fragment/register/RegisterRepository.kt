package com.gmail.vanyadubik.freeride.fragment.register

import android.content.Context
import android.util.Log
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubik.freeride.common.Consts.TOKEN
import com.gmail.vanyadubik.freeride.common.Consts.TYPE_LOGIN_EMAIL
import com.gmail.vanyadubik.freeride.fragment.login.LoginMVPContract
import com.gmail.vanyadubik.freeride.model.dto.*
import com.gmail.vanyadubik.freeride.service.sync.SyncService
import com.gmail.vanyadubik.freeride.service.sync.SyncServiceFactory
import com.gmail.vanyadubik.freeride.utils.NetworkUtils
import com.gmail.vanyadubik.freeride.utils.SharedStorage
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RegisterRepository(private var mContex: Context, private var mainView: RegisterMVPContract.View?) :
    RegisterMVPContract.Repository {


    override fun register(registerRequest: RegisterRequest) {
        if (!NetworkUtils.checkEthernet(mContex)) {
            Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_internet_connecting))
            mainView?.onErrorApi(mContex.getString(R.string.error_internet_connecting))
            return
        }

        SyncServiceFactory.createService(
            SyncService::class.java, mContex)
            .register(TYPE_LOGIN_EMAIL, registerRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RegisterResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(registerResponse: RegisterResponse) {

                    if (registerResponse == null) {
                        Log.e(TAGLOG_SYNC, mContex.getString(R.string.error_no_data))
                        mainView?.onErrorApi(mContex.getString(R.string.error_retrieving_data))
                        return
                    }

                    Log.i(TAGLOG_SYNC, "On next")

                    SharedStorage.setString(mContex, TOKEN, registerResponse?.authToken)

                    mainView?.onRegister()
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

}