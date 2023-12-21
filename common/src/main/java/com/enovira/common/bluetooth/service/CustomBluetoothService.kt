package com.enovira.common.bluetooth.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.enovira.common.bluetooth.BluetoothSocketClientThread
import com.enovira.common.utils.BluetoothUtil
import com.enovira.common.bluetooth.receiver.BluetoothBroadcastReceiver
import com.enovira.common.global.ConstantStore

/**
 * 蓝牙前台服务，用于启动一个蓝牙客户端线程进行数据通信
 */
class CustomBluetoothService : Service() {

    //前台通知相关
    private val channelId = "bluetoothSocket.channelId"
    private val channelName = "bluetoothSocket.channelName"
    private val notificationId = 76
    private val notificationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getSystemService(NotificationManager::class.java)
    }
    //蓝牙广播接收器
    private val receiver: BluetoothBroadcastReceiver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BluetoothBroadcastReceiver() }
    //蓝牙工具类
    private var bluetoothUtil: BluetoothUtil? = null
    //蓝牙客户端线程
    private var bluetoothSocketClientThread: BluetoothSocketClientThread? = null
    //蓝牙客户端线程回调函数
    private val bluetoothCallback = object: (Int, String?) -> Unit {
        override fun invoke(p1: Int, p2: String?) {
            if (p1 == 2) {
                customHandler.sendMessage(customHandler.obtainMessage(p1, p2))
            } else {
                customHandler.sendEmptyMessage(p1)
            }
        }
    }
    //内部类 继承 Handler
    inner class CustomHandler(looper: Looper, callback: Callback): Handler(looper, callback)


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startCustomForegroundService()
        registerBluetoothReceiver()
        bluetoothUtil = BluetoothUtil.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ConstantStore.COMMAND_START_SERVICE -> {
                startCustomForegroundService()
            }
            ConstantStore.COMMAND_STOP_SERVICE -> {
                stopSelf()
            }
            ConstantStore.COMMAND_START_DISCOVERY -> {
                bluetoothUtil?.startDiscovery()
            }
            ConstantStore.COMMAND_STOP_DISCOVERY -> {
                bluetoothUtil?.stopDiscovery()
            }
            ConstantStore.COMMAND_DISCONNECT_BLUETOOTH -> {
                bluetoothSocketClientThread?.disconnect()
            }
            ConstantStore.COMMAND_BONDED_DEVICE_LIST -> {
                bluetoothUtil?.getBondedDeviceList()
            }
            ConstantStore.COMMAND_SEND_MESSAGE -> {
                intent.getStringExtra(ConstantStore.EXTRA_STRING)?.let {
                    bluetoothSocketClientThread?.sendMessage(it)
                }
            }
            ConstantStore.COMMAND_CONNECT_DEVICE -> {
                if (bluetoothSocketClientThread?.isConnect() == true) {
                    Toast.makeText(this, "已建立ACL连接，请先断开先前的连接", Toast.LENGTH_SHORT).show()
                } else {
                    intent.getStringExtra(ConstantStore.BLUETOOTH_DEVICE_MAC)?.let { address ->
                        //接收发送过来的mac地址，尝试建立ACL连接
                        bluetoothUtil?.getRemoteDevice(address)?.let { device ->
                            bluetoothSocketClientThread = BluetoothSocketClientThread(device, bluetoothCallback)
                            bluetoothSocketClientThread?.start()
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销广播接收器
        unregisterReceiver(receiver)
        //取消前台通知
        notificationManager.cancel(notificationId)
    }

    /**
     * 启动前端通知，保持前台服务运行
     */
    private fun startCustomForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.mipmap.icon)
            .setContentTitle("蓝牙前台服务")
            .setContentText("服务正在运行中…………")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true) //打开程序后图标消失
            .setOngoing(true).apply {
                val intent = Intent(this@CustomBluetoothService, CustomBluetoothService::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.action = ConstantStore.COMMAND_STOP_SERVICE
                val pendingIntent = PendingIntent.getService(this@CustomBluetoothService, 201, intent, PendingIntent.FLAG_IMMUTABLE)
                setContentIntent(pendingIntent)
            }.build()
        startForeground(notificationId, notification)
    }

    private fun registerBluetoothReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        registerReceiver(receiver, intentFilter)
    }

    private val customHandler: CustomHandler = CustomHandler(Looper.getMainLooper()) {
        when(it.what) {
            0 -> {
                Toast.makeText(this@CustomBluetoothService, "正在连接中……", Toast.LENGTH_SHORT).show()
            }
            1 -> {
                Toast.makeText(this@CustomBluetoothService, "连接成功", Toast.LENGTH_SHORT).show()
            }
            -1 -> {
                Toast.makeText(this@CustomBluetoothService, "未连接", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                println("最终结果: ${it.obj}")
            }
        }
        true
    }

}