package com.drn1.drn1_player


import android.Manifest
import android.app.AlertDialog
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings

import com.drn1.drn1_player.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import android.content.pm.PackageManager
import android.graphics.Color

import androidx.core.content.ContextCompat

import androidx.core.app.ActivityCompat

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.flurry.android.FlurryAgent
import android.R

import com.flurry.android.marketing.FlurryMarketingOptions
import com.flurry.android.marketing.messaging.FlurryMessagingListener
import com.flurry.android.marketing.FlurryMarketingModule
import com.flurry.android.marketing.messaging.notification.FlurryMessage






class MainActivity : AppCompatActivity() {

    var flurryMessagingListener: FlurryMessagingListener = object : FlurryMessagingListener {
        override fun onNotificationReceived(flurryMessage: FlurryMessage): Boolean {
            return false
        }

        override fun onNotificationClicked(flurryMessage: FlurryMessage): Boolean {
            return false
        }

        override fun onNotificationCancelled(flurryMessage: FlurryMessage) {}
        override fun onTokenRefresh(s: String) {
            println("TOKEN "+s)
        }
        override fun onNonFlurryNotificationReceived(o: Any) {}
    }

   // val player: SimpleExoPlayer by lazy { SimpleExoPlayer.Builder(this).build()}
    private lateinit var binding: ActivityMainBinding
    private var locationManager : LocationManager? = null

    val MY_PERMISSIONS_REQUEST_LOCATION = 99


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        DataHolder.set_Media("https://api.drn1.com.au:9000/station/DRN1?uuid=" +
        Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ))

        Thread.sleep(1000L)

        if(DataHolder.get_Uuid() == "null"){
            DataHolder.set_Uuid(
                Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
        }

      // val myFlurryMessagingListener: FlurryMessagingListener = MyFlurryMessagingListener(this)
        val flurryMessagingOptions = FlurryMarketingOptions.Builder()
            .setupMessagingWithAutoIntegration()
            .withDefaultNotificationChannelId("DRN1")
            .withDefaultNotificationIconResourceId(R.drawable.ic_dialog_alert)
            //.withDefaultNotificationIconAccentColor()
            .withFlurryMessagingListener(flurryMessagingListener)
            .build()
        val marketingModule = FlurryMarketingModule(flurryMessagingOptions)



        FlurryAgent.Builder()
            .withLogEnabled(true)
            .withModule(marketingModule)
            .build(this, "49SSQHX3B8S66HQY9BBV")






        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)

        binding.recyclingViewMain.layoutManager = LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclingViewDRN1Shows.layoutManager = LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclingViewUnitedShows.layoutManager = LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclingViewLifeShows.layoutManager = LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false)


        /* val mediaItem: MediaItem = MediaItem.fromUri("http://stream.radiomedia.com.au:8003/stream")

         player.setMediaItem(mediaItem)
         player.prepare()

         player.play()
 */


      //  runThread("http://stream.radiomedia.com.au:8003/stream")
        fetchStationJson()
        fetchProgramJson("DRN1")
        fetchProgramJson("DRN1United")
        fetchProgramJson("1Life")
        val Mhandler = Handler(Looper.getMainLooper())
        Mhandler.post(object : Runnable {
            override fun run() {
                MediaCore().nowplaying()
                Mhandler.postDelayed(this, 5000)
            }
        })
        }


/* CHECK LOCATIOON*/


    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this, R.style.Theme_Material_Dialog_Alert)
                    .setTitle("This App Requires Location Services")
                    .setMessage("DRN1 uses your location for:\n - Competitions\n - Fuel Prices\n - Programs & Stations.\n\nPlease note that the app may play up if we don't have your location.")
                    .setPositiveButton("ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .setCancelable(false)
                    .create()
                    .show()




            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    //("myTag", "Security Exception, no location available")
                }
            }
        }
    }
    /* END LOACTION CHDECK*/





    /// LOCATION LISTENER

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            //https://api.drn1.com.au:9000/listener?uuid=\(MusicPlayer.uuid ?? "")&lat=\(latitude)&long=\(longitude)&speed=\(speed)"
            val url = "https://api.drn1.com.au:9000/listener?uuid=" + DataHolder.get_Uuid() + "&lat=" + location.latitude + "&long=" +location.longitude
            println(url)
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //DO THAT
                    println("LOCATION FAILED TO UPDATED "+ location.longitude + ":" + location.latitude)
                }

                override fun onResponse(call: Call, response: Response) {
                    //DO THIS
                    println("LOCATION UPDATED " + DataHolder.get_Uuid() + " : "+ location.longitude + ":" + location.latitude)

                }
            })

        // thetext.text = ("" + location.longitude + ":" + location.latitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    /// END LOCATION LISTER




    fun fetchProgramJson(programName:String){
        //https://api.drn1.com.au/api-access/programs/

        val url = "https://api.drn1.com.au/api-access/programs/"+programName
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                fetchProgramJson(programName)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val body = response.body!!.string()
                    val gson = GsonBuilder().create()
                    val programfeed = gson.fromJson(body, ProgramFeed::class.java)
                    println("THE PROGRAMS COME FROM " + programName)


                    Thread {
                        runOnUiThread {
                            when {
                                programName.equals("DRN1", true) -> binding.recyclingViewDRN1Shows.adapter = MainProgramAdapter(programfeed)
                                programName.equals("DRN1United", true) -> binding.recyclingViewUnitedShows.adapter = MainProgramAdapter(programfeed)
                                programName.equals("1Life", true) -> binding.recyclingViewLifeShows.adapter = MainProgramAdapter(programfeed)


                            }
                            //   binding.recyclingViewUnitedShows.adapter = MainProgramAdapter(programfeed)


                        }


                    }.start()
                    println(body)
                }
            }
        })
    }

    //Fetch Stations
    fun fetchStationJson(){


        val url = "https://api.drn1.com.au/station/allstations"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                fetchStationJson()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val body = response.body!!.string()
                    val gson = GsonBuilder().create()
                    val stationfeed = gson.fromJson(body, StationsFeed::class.java)


                    Thread {
                        runOnUiThread {
                            binding.recyclingViewMain.adapter = MainStationAdapter(stationfeed)
                        }
                    }.start()
                    println(body)
                }
            }
        })
    }

    //END FETCH STATION
}