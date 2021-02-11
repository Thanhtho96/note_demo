package com.tt.noteapplication.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var viewModel: VM

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun viewModelClass(): Class<VM>

    protected abstract fun handleViewState()

    protected abstract fun setTheme()

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())

        viewModel = ViewModelProvider(this).get(viewModelClass())
        initView()
        initData()
    }
}