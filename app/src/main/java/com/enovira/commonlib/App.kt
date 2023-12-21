package com.enovira.commonlib


import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.io.File
import java.lang.ref.WeakReference

/**
 * 全局应用类
 */
class App : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore
    lateinit var eventVM: EventViewModel

    companion object {
        private lateinit var instance: App
        private lateinit var context: WeakReference<Context>
        fun instance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = WeakReference(applicationContext)
        mViewModelStore = ViewModelStore()
        eventVM = ViewModelProvider(this)[EventViewModel::class.java]
    }

    fun getContext(): Context {
        return context.get()!!
    }

    override val viewModelStore: ViewModelStore
        get() = mViewModelStore
}