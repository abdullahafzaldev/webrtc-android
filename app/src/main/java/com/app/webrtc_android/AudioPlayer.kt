package com.app.webrtc_android

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord

import android.media.AudioTrack
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AudioPlayer {


    var SAMPLE_RATE = 48000
    var minBufferSize = 0
    private var player: AudioTrack? = null
    private var minBufferSizeInBytes = 0
    private var minBufferSizeInShort = 0
    private var shortData: ShortArray? = null
    var previous: Long = 0


    @RequiresApi(Build.VERSION_CODES.M)
    fun initialize(){

        minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        minBufferSizeInBytes = 1024 * 2 * 2
        minBufferSizeInShort = minBufferSizeInBytes / 2



        player = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .build()
    }


    fun play (shortData: ShortArray){
        Log.d("TAGArray", "play: $shortData ")
        CoroutineScope(Dispatchers.IO).launch {
           val a =  player!!.write(shortData, 0, shortData.size)
            Log.d("TAGArray", "player retyrmn: ${a}")
        }


    }

    fun playSound(){
        player!!.play()
    }
}