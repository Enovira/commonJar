package com.enovira.common.view

import android.view.View

/**
 * 防止快速点击事件(内置点击间隔)
 * @author Enovira
 * @date 2023/09/13
 */
abstract class PreventClickListener: View.OnClickListener {

    private val period: Long = 600 // 内置点击间隔600ms
    private var lastClickTime: Long = 0 // 记录最后一次点击时间

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > period) {
            lastClickTime = currentTime
            onPreventFastClickListener(v)
        }
    }

    abstract fun onPreventFastClickListener(view: View?)
}