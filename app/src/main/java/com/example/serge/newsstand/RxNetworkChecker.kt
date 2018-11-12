package com.example.serge.newsstand

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class RxNetworkChecker(private val context: Context) {

    private val DEBUG_TAG = this::class.java.simpleName

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun networkRuntimeStatusObservable(): Observable<Boolean> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkStatusObservableNewApi()
        } else {
            networkStatusObservableOldApi()
        }
    }

    private fun networkStatusObservableOldApi(): Observable<Boolean> {
        return Observable.create {
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Log.d(DEBUG_TAG, "onReceive")
                    val networkInfo = connectivityManager.activeNetworkInfo
                    when (networkInfo) {
                        null -> it.onNext(false)
                        else -> it.onNext(networkInfo.isConnected)
                    }
                }
            }
            it.setCancellable {
                Log.d(DEBUG_TAG, "unregisterReceiver()")
                context.unregisterReceiver(broadcastReceiver)
            }
            Log.d(DEBUG_TAG, "registerReceiver()")
            context.registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    @SuppressLint("NewApi")
    private fun networkStatusObservableNewApi(): Observable<Boolean> {
        return createNetworkChangeObservable().startWith(Unit)
                .doOnNext { Log.d(DEBUG_TAG, "Network status changed!") }
                .flatMapSingle { createConnectSocketSingle() }
                .doOnNext { Log.d(DEBUG_TAG, "Connection to Socket: $it") }
    }

    @SuppressLint("NewApi")
    private fun createNetworkChangeObservable(): Observable<Unit> {
        val request = NetworkRequest.Builder()
        request.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        return Observable.create<Unit> {
            connectivityManager.registerNetworkCallback(request.build(), object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    Log.d(DEBUG_TAG, "onAvailable()")
                    super.onAvailable(network)
                    it.onNext(Unit)
                }

                override fun onLosing(network: Network?, maxMsToLive: Int) {
                    Log.d(DEBUG_TAG, "onLosing()")
                    super.onLosing(network, maxMsToLive)
                    it.onNext(Unit)
                }

                override fun onUnavailable() {
                    Log.d(DEBUG_TAG, "onUnavailable()")
                    super.onUnavailable()
                    it.onNext(Unit)
                }

                override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities?) {
                    Log.d(DEBUG_TAG, "onCapabilitiesChanged()")
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    it.onNext(Unit)
                }

                override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
                    Log.d(DEBUG_TAG, "onLinkPropertiesChanged()")
                    super.onLinkPropertiesChanged(network, linkProperties)
                    it.onNext(Unit)
                }

                override fun onLost(network: Network) {
                    Log.d(DEBUG_TAG, "onLost()")
                    super.onLost(network)
                    it.onNext(Unit)
                }
            })
            it.setCancellable {  }
        }
    }

    private fun createConnectSocketSingle(): Single<Boolean> {
        return Single.fromCallable<Boolean> {
            try {
                val timeoutMillis = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)
                socket.connect(socketAddress, timeoutMillis)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }
}