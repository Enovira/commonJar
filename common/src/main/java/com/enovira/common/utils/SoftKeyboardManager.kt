package com.enovira.common.utils;

import android.app.Instrumentation
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 软键盘工具类
 * @date 2023/09/22
 */
class SoftKeyboardManager {
    companion object {
        val instance by lazy { SoftKeyboardManager() }
    }

    fun hideSoftKeyboard(context: Context, v: View?) {
        v?.run {
            context.getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    fun hideSoftKeyboard() {
        Thread {
            // 此方法不能在主线程中调用
            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        }.start()
    }
}