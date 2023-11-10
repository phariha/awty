package edu.uw.ischool.phariha.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var messageText: EditText
    private lateinit var phoneText: EditText
    private lateinit var timeText: EditText
    private lateinit var startButton: Button
    private var isStarted : Boolean = false
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
            val intent = Intent(this, MessageService::class.java)
            intent.putExtra("message", messageText.text.toString())
            intent.putExtra("phone", phoneText.text.toString())

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (isStarted) {
                startButton.text = "Stop"
                val interval = phoneText.text.toString().toLong() * 60 * 1000
                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, interval, pendingIntent)
            } else {
                startButton.text = "Start"
                alarmManager.cancel(pendingIntent)
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
}