package com.drn1.drn1_player

import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flurry.android.FlurryAgent
import java.util.*
import kotlin.concurrent.schedule


class PodcastEpisodeAdapter(val podcastFeed: PodcastFeed): RecyclerView.Adapter<EpisodeViewHolder>() {

    //Number of Items
    override fun getItemCount(): Int {
        return podcastFeed.programs[0].episode.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        //How do we even create a view?
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.podcast_details, parent, false)
        return EpisodeViewHolder(cellForRow)

    }

    override fun onBindViewHolder(
        holder: EpisodeViewHolder,
        position: Int
    ) {
        //  val StationName = findViewById(R.id.textView_station_name)

        val ProgramName = podcastFeed.programs[0].episode.get(position)
        holder.view.findViewById<TextView>(R.id.episodeTitle).text = ProgramName.title
        //holder?.view?.findViewById<TextView>(R.id.episodeDescription)?.text = ProgramName.description
        holder.view.findViewById<TextView>(R.id.episodeDescription).text = ProgramName.summary
        holder.podcast = ProgramName
        holder.podcastCore = podcastFeed.programs[0]
    }

}

class EpisodeViewHolder(val view: View, var podcast: EpisodeFeed? = null, var podcastCore: PodcastProgram? = null): RecyclerView.ViewHolder(view) {

    init{
        view.setOnClickListener {



            DataHolder.set_Media_Type("podcast")


            val articleParams: MutableMap<String, String> = HashMap()
            articleParams["podcast"] = podcastCore?.title.toString()
            articleParams["Episode_title"] = podcast?.title.toString()
            FlurryAgent.logEvent("Track_Change", articleParams)

            Timer().schedule(1000) {
                DataHolder.set_MediaPlayerImage(podcastCore?.icon.toString())
                DataHolder.set_Song(podcast?.title.toString())
                DataHolder.set_Artist(podcastCore?.title.toString())
                DataHolder.set_Media("https://dts.podtrac.com/redirect.mp3/" + podcast?.enclosureurl.toString())
            }

            val i = Intent(view.context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            view.context.startActivity(i)

        }

    }
}