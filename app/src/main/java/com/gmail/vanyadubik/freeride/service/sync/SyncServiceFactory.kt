package com.gmail.vanyadubik.freeride.service.sync

import android.content.Context
import android.util.Base64
import com.google.gson.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.io.IOException
import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.TimeUnit

import com.gmail.vanyadubik.freeride.common.Consts.CONNECT_SERVER_URL
import com.gmail.vanyadubik.freeride.common.Consts.CONNECT_TIMEOUT_SECONDS_RETROFIT


object SyncServiceFactory {
    private val httpClient = OkHttpClient.Builder()
        .readTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
        .connectTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
    private val logging = HttpLoggingInterceptor()
    private var builder: Retrofit.Builder? = null


    private fun getBuilder(url: String): Retrofit.Builder? {

        builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())

        return builder
    }

    fun <S> createService(serviceClass: Class<S>, context: Context): S {
        logging.level = HttpLoggingInterceptor.Level.NONE

        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val credentials = "" + ":" + ""
            val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

            val requestBuilder = original.newBuilder()
                .header("Authorization", basic)
                .header("Accept", "application/json")
                .method(original.method(), original.body())

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        httpClient.addInterceptor(logging)

        val client = httpClient.build()

        var retrofit: Retrofit

        try {
            retrofit = getBuilder(CONNECT_SERVER_URL)!!
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        } catch (e: IllegalArgumentException) {
            retrofit = getBuilder("http://localhost")!!
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        }

        return retrofit.create(serviceClass)
    }

    private class NullOnEmptyConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(
            type: Type?,
            annotations: Array<Annotation>?,
            retrofit: Retrofit?
        ): Converter<ResponseBody, *>? {
            val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
            return Converter<ResponseBody, Any> { body ->
                if (body.contentLength() == 0L) null else delegate.convert(
                    body
                )
            }
        }
    }
}
