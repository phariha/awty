package edu.uw.ischool.phariha.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private lateinit var messageText: EditText
    private lateinit var phoneText: EditText
    private lateinit var timeText: EditText
    private lateinit var startButton: Button
    private var isStarted : Boolean = false
    private val SMS_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageText = findViewById(R.id.textInput)
        phoneText = findViewById(R.id.editTextPhone)
        timeText = findViewById(R.id.editTextTime)
        startButton = findViewById(R.id.startButton)

        startButton.isEnabled = false

        messageText.addTextChangedListener(textWatcher)
        phoneText.addTextChangedListener(textWatcher)
        timeText.addTextChangedListener(textWatcher)

        startButton.setOnClickListener() {
            isStarted = !isStarted
            if (isStarted) {
                startButton.text = "Stop"
                checkAndRequestSmsPermission()
                setupAlarmManager()
            } else {
                startButton.text = "Start"
                val intent = Intent(this, MessageService::class.java)
                stopService(intent)
            }
        }

    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            This also isn't used
        }

        override fun afterTextChanged(s: Editable?) {
//            This isn't used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val allFieldsFilled = (messageText.text.isNotEmpty()) &&
                    (phoneText.text.isNotEmpty()) &&
                    (timeText.text.isNotEmpty()) &&
                    ((timeText.text.toString().toIntOrNull() ?: 0) > 0)
            startButton.isEnabled = allFieldsFilled

        }
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupAlarmManager()
                } else {
                    Log.e("MainActivity", "SMS permission denied")
                    Toast.makeText(this, "SMS permission denied. Messages will not be sent.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAlarmManager() {
        val intent = Intent(this, MessageService::class.java)
        intent.putExtra("message", messageText.text.toString())
        intent.putExtra("phone", phoneText.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val interval = timeText.text.toString().toLong() * 60 * 1000
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            interval,
            pendingIntent
        )
    }
}