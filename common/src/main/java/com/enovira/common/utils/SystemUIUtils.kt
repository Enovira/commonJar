package com.enovira.common.utils

import android.view.View
import android.view.Window

object SystemUIUtils {
    /**
     * 隐藏底部虚拟按钮(被唤起后设置的标记失效，建议放在onResume当中)
     * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 隐藏底部导航栏
     * View.SYSTEM_UI_FLAG_LAYOUT_STABLE 界面内容在导航栏后面，避免挤压界面UI
     * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 沉浸模式
     * View.SYSTEM_UI_FLAG_FULLSCREEN 全屏模式(头部状态栏和底部导航栏)
     */
    fun hideBottomVirtualButton(window: Window) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}