package com.zelgius.cropkeeper.ui

import android.content.Context

fun Context.getStringByName(resIdName: String?): String? =
    resIdName?.let {
        with(resources.getIdentifier(it, "string", packageName)) {
            if (this == 0) null
            else getString(this)
        }
    }

