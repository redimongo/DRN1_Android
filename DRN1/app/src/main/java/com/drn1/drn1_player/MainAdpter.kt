package com.drn1.drn1_player

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flurry.android.FlurryAgent
import com.squareup.picasso.Picasso
import java.util.HashMap


class MainStationAdapter(val stationsFeed: StationsFeed): RecyclerView.Adapter<StationViewHolder>() {

    //Number of Items
    override fun getItemCount(): Int {
        return stationsFeed.data.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
       //How do we even create a view?
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.station_row, parent, false)
        return StationViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
      //  val StationName = findViewById(R.id.textView_station_name)
        val stationName = stationsFeed.data.get(position)
        //holder?.view?.findViewById<TextView>(R.id.textView_station_name)?.text = StationName.name
        val stationImageView = holder.view.findViewById<ImageView>(R.id.imageView_station)
        Picasso.get().load(stationName.imageurl).fit().into(stationImageView)
        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(stationImageView);

        holder.station = stationName
    }

}



class MainProgramAdapter(val programFeed: ProgramFeed): RecyclerView.Adapter<ProgramViewHolder>() {

    //Number of Items
    override fun getItemCount(): Int {
        return programFeed.programs.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        //How do we even create a view?
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.podcast_mainscreenlayout, parent, false)
        return ProgramViewHolder(cellForRow)

    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        //  val StationName = findViewById(R.id.textView_station_name)
        val programName = programFeed.programs.get(position)
        //holder?.view?.findViewById<TextView>(R.id.textView_station_name)?.text = ProgramName.title
        val stationImageView = holder.view.findViewById<ImageView>(R.id.imageView_station)
        Picasso.get().load(programName.icon).fit().into(stationImageView)
        holder.podcast = programName
        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(stationImageView);
    }

}


class StationViewHolder(val view: View,var station: Data? = null): RecyclerView.ViewHolder(view) {



    init {
        view.setOnClickListener {
            val articleParams: MutableMap<String, String> = HashMap()
            articleParams["name"] = station?.name.toString()
            FlurryAgent.logEvent("Station_Change", articleParams)

            DataHolder.set_Media_Type("radio")
            DataHolder.set_Score(station?.name.toString())
            DataHolder.set_Media(station?.listenlive.toString() + "?uuid=" + DataHolder.get_Uuid())
            MediaCore().nowplaying()
        }
    }

}

class ProgramViewHolder(val view: View, var podcast: Program? = null): RecyclerView.ViewHolder(view) {
    companion object{
        val PODCAST_NAME_KEY = "PODCAST"
        val PODCAST_SHORT_NAME_KEY = "POSTCAST_URL"
    }
    init{
        view.setOnClickListener {
            println("TESTING PROGRAM CLICK")



            val intent = Intent(view.context, PodcastActivity::class.java)
            intent.putExtra(PODCAST_NAME_KEY, podcast?.title )
            intent.putExtra(PODCAST_SHORT_NAME_KEY, podcast?.url )
            view.context.startActivity(intent)
        }

    }
}


