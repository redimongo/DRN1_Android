package com.drn1.drn1_player.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.drn1.drn1_player.*
import com.drn1.drn1_player.DataHolder
import com.drn1.drn1_player.MediaCore
import com.flurry.android.FlurryAgent
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.squareup.picasso.Picasso
import java.util.HashMap
import android.net.Uri








class Mplayer : Fragment(), View.OnClickListener {

    private lateinit var player: SimpleExoPlayer
    lateinit var SG: TextView
    lateinit var AG: TextView
    lateinit var MEDIAIMAGE: ImageView
    var Artist: String? = null
    var Song: String? = null
    var currentTrack: String? = null



    override fun onAttach(context: Context) {
        super.onAttach(context)
        player = SimpleExoPlayer.Builder(context).build()
        Song = DataHolder.get_Score()
    }

    override fun onResume() {
        super.onResume()
        Song = DataHolder.get_Song()
        updateSong(DataHolder.get_Song())

    }


    override fun onStart() {

        super.onStart()
       // println("FIRST START " + DataHolder.get_Media())
        if(!player.isPlaying){
            println("button pressed & Playing")
            play()
        }
        else{
            println("PLAYER WONT PLAY BECAUSE IT ALREADY IS PLAYING")
        }


    }


    override fun onStop() {
        super.onStop()
       // player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        //player.stop()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
    }

    private fun play(){
        currentTrack = DataHolder.get_Media()
        println("I RAN PLAYER " + currentTrack)

        val mediaItem: MediaItem = MediaItem.fromUri(DataHolder.get_Media())

        player.setMediaItem(mediaItem)
        player.prepare()

        player.play()
    }

    private fun changeTrack(){
        if(currentTrack != DataHolder.get_Media())
        {

            player.stop()
            player.removeMediaItem(0)
                        //player = null
            val mediaItem: MediaItem = MediaItem.fromUri(DataHolder.get_Media())
            currentTrack = DataHolder.get_Media()
            player.setMediaItem(mediaItem)
            player.prepare()

            player.play()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //val context = inflater.context
        //binding = MplayerBinding.inflate(com.drn1.drn1.R.layout.fragment_mplayer)

        //val rooView: View = inflater.inflate(R.layout.fragment_mplayer, container, false)
        val root: View = inflater.inflate(R.layout.fragment_mplayer, container, false)
        val btn = root.findViewById(R.id.mPlayPP) as ImageButton
        SG = root.findViewById(R.id.song) as TextView
        AG = root.findViewById(R.id.artist) as TextView
        MEDIAIMAGE = root.findViewById(R.id.MediaPlayerImage)
        //val tv: View = root.findViewById(R.id.song)
        //(tv as TextView).text = "Fragment #$Song"

        //SG.setText(Song)

        root.findViewById<ImageView>(R.id.MediaPlayerImage).setOnClickListener {
               if(DataHolder.get_Media_Type() == "radio" && DataHolder.get_AdStichrURL() != "") {
                   val browserIntent =
                       Intent(Intent.ACTION_VIEW, Uri.parse(DataHolder.get_AdStichrURL()))
                   startActivity(browserIntent)
               }

        }


        if(player.isPlaying){
            println("button pressed & Paused")
            btn.setBackgroundResource(0)
            btn.setImageResource(R.drawable.exo_icon_circular_play);


        }else {
            println("button pressed & Playing")
            btn.setBackgroundResource(0)
            btn.setImageResource(R.drawable.exo_icon_pause)
        }

        btn.setOnClickListener {
           if(player.isPlaying){
                println("button pressed & Paused")
                btn.setBackgroundResource(0)
                btn.setImageResource(R.drawable.exo_icon_circular_play);

                player.stop()
            }else {
                println("button pressed & Playing")
                btn.setBackgroundResource(0)
                btn.setImageResource(R.drawable.exo_icon_pause)
                play()
            }

            //This function allows us to talk to the Main Activity (note that you can only talk to the activity that has the fragment embedded into its Activity.
            //(activity as MainActivity).bbtn()

        }




        // Toast.makeText(mContext, "THIS IS SAMPLE TOAST", Toast.LENGTH_SHORT).show()
        // Inflate the layout for this fragment
        return root

    }

    fun updateSong(song: String) {
        SG.setText(Song)

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if(SG.text != DataHolder.get_Song()) {
                    SG.setText(DataHolder.get_Song())
                    AG.setText(DataHolder.get_Artist())

                    val articleParams: MutableMap<String, String> = HashMap()
                    articleParams["artist"] = DataHolder.get_Artist()
                    articleParams["Song"] = DataHolder.get_Song()
                    FlurryAgent.logEvent("Track_Change", articleParams);

                    Picasso.get().load(DataHolder.get_MediaPlayerImage()).fit().into(MEDIAIMAGE)
                    changeTrack()
                }
                handler.postDelayed(this, 1000)
            }
        })


    }

    override fun onClick(v: View?) {
        //TODO("Not yet implemented")
    }


}
