package edu.uw.ischool.phariha.awty

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MessageService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i("test", "toast is being made")
        val message = intent?.getStringExtra("message") ?: ""
        val phoneNumber = intent?.getStringExtra("phone") ?: ""
        Toast.makeText(context, "($phoneNumber): $message", Toast.LENGTH_SHORT).show()
    }
}