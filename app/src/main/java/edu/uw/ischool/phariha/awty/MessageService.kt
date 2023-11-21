package edu.uw.ischool.phariha.awty

import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest

class MessageService : IntentService("MessageService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.i("MessageService", "onHandleIntent called")

        val message = intent?.getStringExtra("message") ?: ""
        val phoneNumber = intent?.getStringExtra("phone") ?: ""

        Log.i("MessageService", "Received message: $message to phone number: $phoneNumber")

        // You can continue with the rest of your logic here
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendSMS(phoneNumber, message)
        } else {
            Log.e("MessageService", "SMS permission not granted")
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.i("MessageService", "SMS sent to $phoneNumber: $message")
        } catch (e: Exception) {
            Log.e("MessageService", "Failed to send SMS", e)
        }
    }
}
