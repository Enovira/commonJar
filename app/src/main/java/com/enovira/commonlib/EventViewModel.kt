package com.enovira.commonlib

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 全局事件
 */
class EventViewModel: ViewModel() {

    var bluetoothEvent: BluetoothEvent = BluetoothEvent()

    /** 蓝牙全局事件 */
    class BluetoothEvent: ViewModel() {
        val stateChanged: MutableLiveData<Boolean> = MutableLiveData()
    }
}