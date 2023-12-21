package com.enovira.common.global

import java.util.UUID

object ConstantStore {
    val commonUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // 蓝牙官方通用UUID

    /** 命令 */
    const val COMMAND = "command"
    /** 命令: 启动服务 */
    const val COMMAND_START_SERVICE = "command_StartService"
    /** 命令: 停止服务 */
    const val COMMAND_STOP_SERVICE = "command_stopService"

    /** 命令: 扫描蓝牙 */
    const val COMMAND_START_DISCOVERY = "command_startScanBle"
    /** 命令: 停止扫描蓝牙 */
    const val COMMAND_STOP_DISCOVERY = "command_stopScanBle"
    /** 命令: 已配对设备 */
    const val COMMAND_BONDED_DEVICE_LIST = "command_bondedDevices"
    /** 命令: 连接设备 */
    const val COMMAND_CONNECT_DEVICE = "command_connectDevice"
    /** 命令: 断开蓝牙ACL连接 */
    const val COMMAND_DISCONNECT_BLUETOOTH = "command_disconnectBle"
    /** 命令: 发送数据 */
    const val COMMAND_SEND_MESSAGE = "command_SendMessage"

    /** 额外信息Key: String */
    const val EXTRA_STRING = "extra_string"

    //蓝牙信息
    const val BLUETOOTH_DEVICE_MAC = "bluetoothDeviceMacAddress"

    const val ERROR_CODE = -1
    const val SUCCEED_CODE = 200
}