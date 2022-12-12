package com.app.webrtc_android

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.webrtc_android.AudioInit.Companion.shortData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.math.sin


class MainActivity : AppCompatActivity() {


    val audioInit = AudioInit()
    val audioPlayer = AudioPlayer()
    lateinit var job : Job
    var meetingRunning = false

    var isPermissionTrue = false

    private val BITRATE = 44100.0

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }

    private var recordingThread: Thread? = null


    private val audioManager: AudioManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission(
            Manifest.permission.RECORD_AUDIO,
            STORAGE_PERMISSION_CODE
        )
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        start.setOnClickListener {

            audioInit.initialize()
            audioInit.startRecording()
            meetingRunning = true
            startRecordingAndSendData()
            //Log.d("TAGArray", "start: ${arr}")
        }

        stop.setOnClickListener {
            meetingRunning = false
            job.cancel()
            audioInit.stopRecording()
            audioPlayer.initialize()
            audioPlayer.play(shortData!!)
            //  startRecordingAndSendData(false)
        }

        play.setOnClickListener {
//           val t = generateToneSine(20000.0 , 10000)
//            t!!.play()
            audioPlayer.playSound()


        }


    }

    // Function to check and request permission
    fun checkPermission(permission: String, requestCode: Int) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRecordingAndSendData() {
       job =   CoroutineScope(Dispatchers.Default).launch {
            while (meetingRunning) {
                val data = audioInit.getRecordedDataShort()
                Log.d("TAGArray", "startRecordingAndSendData: $data")

            }
        }

    }


    private val MILLIS_IN_SECOND = 10000.0
    fun generateToneSine(freqHz: Double, durationMs: Int): AudioTrack? {
       // val count = (BITRATE * 2.0 * (durationMs / MILLIS_IN_SECOND)).toInt() and 1.inv()
        val count = (BITRATE * 2.0 * (durationMs / MILLIS_IN_SECOND)).toInt() and 2.inv()
        val samples = ShortArray(count)
        var i = 0
        while (i < count) {
            val sample = (sin(2 * Math.PI * i / (BITRATE / freqHz)) * 0x7FFF).toInt().toShort() // MAGIC_NUMBER
            samples[i] = sample
            samples[i + 1] = sample
            i += 2
        }
        val track = AudioTrack(
            AudioManager.STREAM_MUSIC, BITRATE.toInt(),
            AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
            count * (java.lang.Short.SIZE / 8), AudioTrack.MODE_STATIC
        ) // MAGIC_NUMBER
        track.write(samples, 0, count)
        return track
    }
}






