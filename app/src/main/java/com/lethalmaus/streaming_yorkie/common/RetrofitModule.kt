package com.lethalmaus.streaming_yorkie.common

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.lethalmaus.streaming_yorkie.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder().apply {
        protocols(listOf(Protocol.HTTP_1_1))
        connectTimeout(30L, TimeUnit.SECONDS)
        readTimeout(30L, TimeUnit.SECONDS)
        writeTimeout(30L, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(logging)
        }
    }.build()
}

fun provideRetrofitClient(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .build()
}

fun <T> createService(service: Class<T>, baseUrl: String): T {
    return provideRetrofitClient(baseUrl).create(service)
}

suspend fun <T> Call<T>.mapToGenericResponse(): GenericResponse<T?> {
    val url = this.request().url.toString()

    fun handleError(url: String, errorCode: Int? = null, errorCodeInt: Int? = null, body: Any? = null, message: String? = null, headers: Headers? = null): NetworkError {
        return NetworkError(
            errorCode = errorCode ?: errorCodeInt ?: 0,
            url = url,
            body = body,
            message = message,
            headers = headers
        )
    }

    try {
        return this.awaitResponse().let {
            when {
                it.isSuccessful -> SuccessResponse(it.body(), url)
                else -> handleError(url, errorCodeInt = it.code(), body = it.errorBody(), message = it.message(), headers = it.headers())
            }
        }
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.NO_NETWORK_ERROR.errorCode)
    } catch (e: SocketException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.SOCKET_EXCEPTION.errorCode)
    } catch (e: SSLHandshakeException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.SSL_HANDSHAKE_EXCEPTION.errorCode)
    } catch (e: ConnectException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.CONNECTION_EXCEPTION.errorCode)
    } catch (e: SocketTimeoutException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.SOCKET_TIMEOUT_EXCEPTION.errorCode)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.INVALID_RESPONSE_DATA_ERROR.errorCode)
    } catch (e: IllegalStateException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.INVALID_RESPONSE_DATA_ERROR.errorCode)
    } catch (e: IOException) {
        e.printStackTrace()
        return handleError(url, ErrorCode.IO_EXCEPTION.errorCode)
    }
}

suspend fun <T : Any?> Call<T>.awaitResponse(): Response<T> {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (continuation.isCancelled) return
                continuation.resumeWithException(t)
            }
        })
        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
        }
    }
}

sealed class GenericResponse<out T>(var metadata: Any? = null)
data class SuccessResponse<T>(val data: T, val url: String = "URL not defined") : GenericResponse<T>()
open class ErrorResponse : GenericResponse<Nothing>()
data class NetworkError(
    val errorCode: Int,
    val url: String = "URL not defined",
    val body: Any? = null,
    val message: String? = null,
    val headers: Headers? = null
): ErrorResponse()

enum class ErrorCode(val errorCode: Int) {

    NO_NETWORK_ERROR(10101),
    INVALID_RESPONSE_DATA_ERROR(10103),
    SOCKET_TIMEOUT_EXCEPTION(10105),
    CONNECTION_EXCEPTION(10106),
    SSL_HANDSHAKE_EXCEPTION(10107),
    SOCKET_EXCEPTION(10109),
    IO_EXCEPTION(10110),
    TOKENS_EXPIRED(10111);

    override fun toString(): String {
        return "${super.toString()}, code: $errorCode"
    }
}