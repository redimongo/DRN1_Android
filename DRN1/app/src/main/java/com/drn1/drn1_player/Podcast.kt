package com.drn1.drn1_player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.drn1.drn1_player.databinding.PodcastBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


class PodcastActivity : AppCompatActivity() {
    private var shortURL: String? = null

/*    companion object {
        fun execute()
        { println("Executing from inside a companion object") }
        fun executeSystem(){ println("Inside Fragment Podcast executed.")}
    }*/

    private lateinit var binding: PodcastBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = PodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.podcastRecycling.layoutManager = LinearLayoutManager(this)

        // binding.podcastRecycling.adapter = podcastDetailAdapter()

        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val title = intent.getStringExtra(ProgramViewHolder.PODCAST_NAME_KEY)
        supportActionBar?.title = title
        //binding.podcastRecycling.setBackgroundColor(Color.RED)
        binding.podcastRecycling.setVisibility(View.GONE)
        binding.progressBar.setVisibility(View.VISIBLE)




        fetchJSON()
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
        return true
    }*/


    private fun fetchJSON() {
        val podurl = intent.getStringExtra(ProgramViewHolder.PODCAST_SHORT_NAME_KEY)
        var podcastURL = "https://api.drn1.com.au/api-access/programs/" + podurl

        val client = OkHttpClient()
        val request = Request.Builder().url(podcastURL).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                val gson = GsonBuilder().create()
                val podcastepisodes = gson.fromJson(body, PodcastFeed::class.java)

                Thread {
                    runOnUiThread {
                        binding.podcastRecycling.adapter = PodcastEpisodeAdapter(podcastepisodes)
                        binding.podcastRecycling.setVisibility(View.VISIBLE)
                        binding.progressBar.setVisibility(View.GONE)
                    }
                }.start()
                //  println(body)
            }

            override fun onFailure(call: Call, e: IOException) {

                Thread {
                    runOnUiThread {
                        binding.podcastRecycling.setVisibility(View.GONE)
                        binding.progressBar.setVisibility(View.VISIBLE)
                    }
                }.start()
                fetchJSON()
            }


        })

    }

    /*  private class podcastDetailAdapter: RecyclerView.Adapter<podcastControlViewHolder>(){
          override fun getItemCount(): Int {
              return 5
          }

          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): podcastControlViewHolder {
             val layoutInflater = LayoutInflater.from(parent.context)
             val customView = layoutInflater.inflate(R.layout.podcast_details, parent, false)




  //            val redView = View(parent.context)
  //            redView.setBackgroundColor(Color.BLUE)
  //            redView.minimumHeight = 50

              /*
              //THIS IS NOT MEANT TO BE IN HERE - REMOVE
              val intent = Intent(parent.context, MusicPlayer::class.java)
              //intent.putExtra(ProgramViewHolder.PODCAST_NAME_KEY, p?.title )
              parent.context.startActivity(intent)

              //END
              */
              return podcastControlViewHolder(customView)
          }

          override fun onBindViewHolder(holder: podcastControlViewHolder, position: Int) {

          }

      }

      private class podcastControlViewHolder(val PodcastView : View): RecyclerView.ViewHolder(PodcastView) {

      }
  */

}
