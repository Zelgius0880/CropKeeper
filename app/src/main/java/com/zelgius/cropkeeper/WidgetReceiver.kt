package com.zelgius.cropkeeper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(WidgetReceiver::class.java.name, intent.action?:"No action")
    }
}