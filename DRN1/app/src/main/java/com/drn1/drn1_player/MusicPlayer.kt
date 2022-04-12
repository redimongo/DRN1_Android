package com.drn1.drn1_player

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


class MediaCore {
     fun nowplaying(){
        println("Media Core "+ DataHolder.get_Media_Type())

         if(DataHolder.get_Media_Type() == "radio") {
             //https://api.drn1.com.au:9000/nowplaying/\(stationurl)?uuid=\(MusicPlayer.uuid!)
             println("https://api.drn1.com.au:9000/nowplaying/" + DataHolder.get_Score() + "?uuid=" + DataHolder.get_Uuid())


             val url = "https://api.drn1.com.au:9000/nowplaying/" + DataHolder.get_Score() + "?uuid=" + DataHolder.get_Uuid()
             val request = Request.Builder().url(url).build()
             val client = OkHttpClient()
             client.newCall(request).enqueue(object : Callback {
                 override fun onFailure(call: Call, e: IOException) {
                     e.printStackTrace()
                 }

                 override fun onResponse(call: Call, response: Response) {
                     response.use {
                         if (!response.isSuccessful) throw IOException("Unexpected code $response")

                         for ((name, value) in response.headers) {
                             println("$name: $value")
                         }

                         val body = response.body!!.string()
                         println(body)
                         val gson = GsonBuilder().create()
                         val nowplayingFeed = gson.fromJson(body, NowPlayingJson::class.java)
                         println("no':" + nowplayingFeed)

                         DataHolder.set_Artist(nowplayingFeed.data[0].track.artist)
                         DataHolder.set_Song(nowplayingFeed.data[0].track.title)
                         DataHolder.set_MediaPlayerImage(nowplayingFeed.data[0].track.imageurl)
                         DataHolder.set_AdstichrType(nowplayingFeed.data[0].track.type)
                         if(nowplayingFeed.data[0].track?.url.isNullOrBlank()) {
                             DataHolder.set_AdStichrURL("")
                         }
                         else{
                             DataHolder.set_AdStichrURL(nowplayingFeed.data[0].track.url)
                         }
                     }


                 }
             })
         }
         }
}

class MusicPlayer  : AppCompatActivity() {
    // val player: SimpleExoPlayer by lazy { SimpleExoPlayer.Builder(this).build()}

   /* companion object {

        fun play(){
                playMedia()
            }

        private fun playMedia(){
            //currentTrack = DataHolder.get_Media()
            val mediaItem: MediaItem = MediaItem.fromUri(DataHolder.get_Media())

            MusicPlayer()?.player.setMediaItem(mediaItem)
            MusicPlayer()?.player.prepare()

            MusicPlayer()?.player.play()
        }

        fun executeSystem() {
                println("Inside Fragment executed.")
            }
    }*/


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }




}