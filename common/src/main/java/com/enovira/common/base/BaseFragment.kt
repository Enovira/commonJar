package com.enovira.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * Fragment基类
 * @author Enovira6208
 * @date 2023/09/13
 */
abstract class BaseFragment<VM : ViewModel, DB : ViewDataBinding> : Fragment() {

    private var _binding: DB? = null
    val binding: DB get() = _binding!!
    lateinit var viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = createViewModel()
        initView()
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}