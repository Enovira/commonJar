package com.enovira.common.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.enovira.common.core.tryRead
import com.enovira.common.global.ConstantStore
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

/**
 * 蓝牙socket客户端线程
 */
@SuppressLint("MissingPermission")
class BluetoothSocketClientThread(
    //目标蓝牙设备
    private val bluetoothDevice: BluetoothDevice,
    //蓝牙客户端回调函数
    private val listener: (Int, String?) -> Unit,
) : Thread() {
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override fun run() {
        try {
            //使用数据安全的Rfcomm传输通道
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(ConstantStore.commonUUID)
            //使用不安全的数据传输通道
//            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(ConstantStore.commonUUID)
            listener.invoke(0, "已创建蓝牙客户端,正在连接中……(<10s)")
            bluetoothSocket?.connect()
            listener.invoke(1, "蓝牙客户端连接成功")
            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream
            while (!interrupted()) {
                inputStream?.let { inStream ->
                    while (inStream.available() == 0) { //循环监听输入流，为空则休眠一段时间直到输入流内容不为空
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
                    println("蓝牙客户端接收到数据: ${String(resultArray)}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.invoke(-1, "蓝牙客户端连接已断开")
        }
    }

    fun disconnect() {
        interrupt()
        inputStream?.close()
        outputStream?.close()
        bluetoothSocket?.close()
        inputStream = null
        outputStream = null
        bluetoothSocket = null
    }

    fun sendMessage(str: String) {
        try {
            outputStream?.write(str.toByteArray(Charset.defaultCharset()))
            outputStream?.flush()
            println("蓝牙客户端发送数据: $str")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(bytes: ByteArray) {
        try {
            outputStream?.write(bytes)
            outputStream?.flush()
            println("蓝牙客户端发送数据: ${String(bytes)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isConnect(): Boolean {
        return bluetoothSocket?.isConnected == true
    }
}