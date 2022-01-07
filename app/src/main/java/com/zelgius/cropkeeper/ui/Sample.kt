package com.zelgius.cropkeeper.ui

import androidx.compose.runtime.Composable

class Sample<T> (private val initializer: @Composable () -> T) {
    private var privateSample: T? = null
    val value
    @Composable
    get() = privateSample ?: run {
        initializer().also { privateSample = it }
    }

}