package com.gmail.vanyadubyk.freeride.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubyk.freeride.model.dto.UserRecoveryRequest
import com.gmail.vanyadubyk.freeride.service.sync.SyncService
import com.gmail.vanyadubyk.freeride.service.sync.SyncServiceFactory
import com.gmail.vanyadubyk.freeride.utils.ActivityUtils
import com.gmail.vanyadubyk.freeride.utils.NetworkUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recovery_password.*
import okhttp3.ResponseBody

class RecoveryPassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recovery_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white)
        supportActionBar?.title = getString(R.string.restoring_access)

        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendBtn.isEnabled = p0?.length!! >5
            }
        })

        sendBtn.setOnClickListener {
            recovery()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun recovery() {

        if (!NetworkUtils.checkEthernet(this)) {
            Log.e(TAGLOG_SYNC, getString(R.string.error_internet_connecting))
            ActivityUtils.showMessage(this, "", null, getString(R.string.error_internet_connecting))
            return
        }

        SyncServiceFactory.createService(
            SyncService::class.java, this)
            .userRecovery(UserRecoveryRequest(email.text.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(responseBody: ResponseBody) {

                    if (responseBody == null) {
                        Log.e(TAGLOG_SYNC, getString(R.string.error_no_data))
                        ActivityUtils.showMessage(this@RecoveryPassActivity, "", null, getString(R.string.error_retrieving_data))
                        return
                    }

                    Log.i(TAGLOG_SYNC, "On next")

                    finish()
                }

                override fun onError(e: Throwable) {
                    if (!NetworkUtils.checkEthernet(this@RecoveryPassActivity)) {
                        Log.e(TAGLOG_SYNC, getString(R.string.error_internet_connecting))
                        ActivityUtils.showMessage(this@RecoveryPassActivity, "", null, getString(R.string.error_internet_connecting))
                        return
                    }

                    Log.e(TAGLOG_SYNC, e.toString())
                    ActivityUtils.showMessage(this@RecoveryPassActivity, "", null,
                        NetworkUtils.getConnectExeption(this@RecoveryPassActivity, e, e.message.toString()))
                }

                override fun onComplete() {}
            })
    }
}
