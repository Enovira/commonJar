package com.enovira.common.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.enovira.common.core.tryRead
import com.enovira.common.global.ConstantStore
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.Timer
import java.util.TimerTask

/**
 * 蓝牙Socket服务端
 * 目前存在较多问题(连接、客户端掉线检测、重连等)，需进行优化
 */
@SuppressLint("MissingPermission")
class BluetoothSocketServerThread(
    //蓝牙服务端回调函数
    private val listener: (Int, String?) -> Unit,
) : Thread() {
    private val name: String = "bluetoothServerSocket"
    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var bluetoothServerSocket: BluetoothServerSocket? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var timer: Timer? = null

    override fun run() {
        try {
            //bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, ConstantStore.uuid)
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, ConstantStore.commonUUID)
//            listener.invoke("创建蓝牙Rfcomm通道成功")
            listener.invoke(0, "等待连接蓝牙Socket中")
            bluetoothSocket = bluetoothServerSocket?.accept()
            listener.invoke(1, "连接蓝牙socket成功")
            // 连接成功后立即结束监听
            bluetoothServerSocket?.close()
            println(bluetoothSocket?.maxReceivePacketSize)
            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream
            inputStream?.let { inStream ->
                while (!interrupted()) {
                    while (inStream.available() == 0) {
                        sleep(100)
                    }
                    var resultArray = ByteArray(0) // 最终字节数组
                    val bytes = ByteArray(1024) // 分段读取
                    var count: Int
                    while (inStream.tryRead(bytes).also { count = it } > 0) {
                        val subArray = ByteArray(resultArray.size + count)
                        System.arraycopy(resultArray, 0, subArray, 0, resultArray.size)
                        System.arraycopy(bytes, 0, subArray, resultArray.size, count)
                        resultArray = subArray
                    }
                    listener.invoke(2, String(resultArray))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.invoke(-1, null)
        }
    }

    fun sendMessage(string: String) {
        try {
            outputStream?.write(string.toByteArray(Charset.defaultCharset()))
            outputStream?.flush()
            println("发送数据: $string")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(bytes: ByteArray) {
        try {
            outputStream?.write(bytes)
            outputStream?.flush()
            println("发送数据: ${String(bytes)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        interrupt()
        timer?.cancel()
        timer = null
        inputStream?.close()
        outputStream?.close()
        bluetoothSocket?.close()
        inputStream = null
        outputStream = null
        bluetoothSocket = null
    }
}