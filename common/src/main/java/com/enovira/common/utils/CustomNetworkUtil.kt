package com.enovira.common.utils;

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 * 网络工具类
 * @date 2023/09/22
 */
class CustomNetworkUtil {

    companion object {
        val instance: CustomNetworkUtil by lazy(this) { CustomNetworkUtil() }
    }

    /**
     * @Description: 获取设备ip地址
     * @return String
     */
    fun getIpAddress(): String? {
        try {
            val enNetI = NetworkInterface.getNetworkInterfaces()
            while (enNetI.hasMoreElements()) {
                val netI = enNetI.nextElement()
                val enumIpAddr = netI.inetAddresses
                enumIpAddr?.let {
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @Description 判断是否有网络连接
     * @return boolean
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }

    /**
     * 判断手机时候开启移动网络
     */
    private fun isTelephonyDataEnabled(context: Activity): Boolean {
        context.getSystemService(TelephonyManager::class.java)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_PHONE_STATE), 0)
                } else {
                    return it.isDataEnabled
                }
            }
        }
        return false
    }
}