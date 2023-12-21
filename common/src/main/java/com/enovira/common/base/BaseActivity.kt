package com.enovira.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * Activity基类
 * @author Enovira
 * @date 2023/09/13
 */
abstract class BaseActivity<VM: ViewModel, DB: ViewDataBinding>: AppCompatActivity() {

    private var _binding: DB? = null
    val binding: DB get() = _binding!!
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        viewModel = createViewModel()
        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()

    private fun createViewModel(): VM {
        return ViewModelProvider(this)[getJvmClazz(this)]
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VM> getJvmClazz(obj: Any): VM {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}