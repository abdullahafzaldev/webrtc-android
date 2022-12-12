package com.app.webrtc_android

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class AudioInit {

    var SAMPLE_RATE = 48000
    var minBufferSize = 0
    private var audioRecord: AudioRecord? = null
    private var minBufferSizeInBytes = 0
    private var minBufferSizeInShort = 0

    var previous: Long = 0
    companion object{
        var shortData: ShortArray? = null
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    fun initialize () {
        minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)

        minBufferSizeInBytes = 1024 * 2 * 2
        minBufferSizeInShort = minBufferSizeInBytes / 2

        shortData = ShortArray(minBufferSizeInShort)


        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .build()



    }

    fun startRecording() {
        Log.d("TAGArray", "start")
        audioRecord!!.startRecording()
    }

    fun getRecordedDataShort(): ShortArray? {
        val start = System.nanoTime() / 1000000
        audioRecord!!.read(shortData!!, 0, minBufferSizeInShort)
        previous = start
        val shortDataStero = ShortArray(minBufferSizeInShort / 2)
        var j = 0
        var i = 1
        while (i < shortData!!.size) {
            shortDataStero[j] = shortData!![i]
            j++
            i = i + 2
           // Log.d("TAGArray", "getRecordedDataShort: ${shortDataStero}")
        }
        return shortDataStero
    }

    fun stopRecording() {
        Log.d("TAGArray", "stop")
        try {
            audioRecord!!.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            audioRecord!!.release() /*    public byte[] getRecordedData() {
        long start = System.nanoTime() / 1000000;
        byte[] byteData = new byte[minBufferSizeInBytes];
        int readData = audioRecord.read(byteData, 0, minBufferSizeInBytes);
        previous = start;
        return byteData;
    }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}