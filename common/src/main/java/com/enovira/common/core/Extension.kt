package com.enovira.common.core

import com.google.gson.Gson
import android.view.View
import com.enovira.common.view.PreventClickListener
import java.io.InputStream

private val gson: Gson by lazy { Gson() }

/**
 * 为View注册防快速重复点击的点击事件
 */
fun View.setPreventClickListener(listener: PreventClickListener) {
    setOnClickListener(listener)
}

/**
 * 快速转Json字符串
 */
fun Any.toJson(): String {
    return try {
        gson.toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
        "this object cannot cast to json string"
    }
}

/**
 * 输出日志(快速定位)
 */
fun xLog(msg: String) {
    try {
        //通过JDK自带的StackTraceElement类获取调用信息栈，用于最后输出
        //此处Thread.currentThread().getStackTrace()获取的是StackTraceElement数组，里面保存的信息是各个调用信息
        val stackTraceElements = Thread.currentThread().stackTrace
        val index = 3 //多层嵌套，此处第三层为使用此方法的层次
        if (stackTraceElements.isNotEmpty() && stackTraceElements.size >= index) {
            stackTraceElements[index].let {
                println(
                    StringBuffer().append("(").append(it.fileName)
                        .append(":").append(it.lineNumber)
                        .append(")#").append(it.methodName)
                        .append(": ").append(msg)
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 读取IO流，返回读取长度，设置内部计时防止阻塞
 * @return 0 或者 实际读取的长度
 */
fun InputStream.tryRead(byteArray: ByteArray, maxLength: Int = byteArray.size, timeout: Long = 50): Int {
    val time = System.currentTimeMillis()
    while (available() < maxLength && System.currentTimeMillis() - time < timeout) {
        Thread.sleep(1)
    }
    val length = if (available() >= maxLength) maxLength else available()
    return read(byteArray, 0, length)
}