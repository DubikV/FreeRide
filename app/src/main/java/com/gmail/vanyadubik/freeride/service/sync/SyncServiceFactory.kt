package com.gmail.vanyadubik.freeride.service.sync

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import com.gmail.vanyadubik.freeride.common.Consts
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
import com.gmail.vanyadubik.freeride.common.Consts.TAGLOG_SYNC
import com.gmail.vanyadubik.freeride.common.Consts.TOKEN_HEADER
import com.gmail.vanyadubik.freeride.service.sync.ssl.Tls12SocketFactory
import com.gmail.vanyadubik.freeride.utils.SharedStorage
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.ArrayList
import javax.net.ssl.*


object SyncServiceFactory {
//    private val httpClient = OkHttpClient.Builder()
//        .readTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
//        .connectTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
//        .retryOnConnectionFailure(true)
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

        val client = getClient(context)

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


    private fun getClient(context: Context): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .readTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT.toLong(), TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header(TOKEN_HEADER, SharedStorage.getString(context, Consts.TOKEN, ""))
                    .method(original.method(), original.body())

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        httpClient.addInterceptor(logging)

        return enableTls12OnPreLollipop(httpClient, context).build()
    }

    fun enableTls12OnPreLollipop(httpClientBuilder: OkHttpClient.Builder, context: Context): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        try {
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {

                val sslContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                httpClientBuilder.sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory))

                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build()

                val specs = ArrayList<Any>()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)

                httpClientBuilder.connectionSpecs(specs)

            } else {
                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                httpClientBuilder.sslSocketFactory(sslSocketFactory)

                httpClientBuilder.hostnameVerifier { hostname, session -> true }
            }

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return httpClientBuilder
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

private fun Any.connectionSpecs(specs: ArrayList<Any>) {

}
