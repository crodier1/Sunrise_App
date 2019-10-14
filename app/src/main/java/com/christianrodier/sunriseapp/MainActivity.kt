package com.christianrodier.sunriseapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    var zip: String? = null
    var apiKey = "5da73b78d73f631d8a18d27035e182f7"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnSunset.setOnClickListener {
            verifyZip()
        }


    }

    private fun verifyZip() {
        var verified = true
        if (etZip.text.isBlank()){

            Toast.makeText(this, "Please enter a zip code", Toast.LENGTH_SHORT).show()
            verified = false

        }

        if (etZip.text.length < 5){
            Toast.makeText(this, "Please enter a 5 digit zip code", Toast.LENGTH_SHORT).show()
            verified = false
        }

        if (verified){

            zip = etZip.text.toString()

            GetSunset()

        }


    }



    protected fun GetSunset(){



        val url = "https://api.openweathermap.org/data/2.5/weather?zip=$zip,us&appid=$apiKey"

        Log.d("url", url)


        MyAsyncTask().execute(url)
    }

    inner class MyAsyncTask: AsyncTask<String, String, String>(){


        override fun onPreExecute() {
           // super.onPreExecute()

        }

        override fun doInBackground(vararg params: String?): String {
            try {

                var url = URL(params[0])

                val urlConnect = url.openConnection() as HttpURLConnection

                urlConnect.connectTimeout = 7000

                var inString = ConvertStreamToString(urlConnect.inputStream)

                publishProgress(inString)

            }catch (ex: Exception){
            }

            return ""
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onProgressUpdate(vararg values: String?) {
           // super.onProgressUpdate(*values)


            try {

                var json = JSONObject(values[0])



                var sys = json.getJSONObject("sys")


                var sunrise = sys.getLong("sunrise")


                val sdf = SimpleDateFormat("h:mm a")
                val date = java.util.Date(sunrise * 1000)

                tvWeatherText.text = "Sunrise at: ${sdf.format(date)}"


                etZip.text.clear()


            }catch (ex: Exception){


            }

        }


        override fun onPostExecute(result: String?) {
          //  super.onPostExecute(result)

        }


    }

    private fun ConvertStreamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))

        var line:String
        var AllString = ""

        try {

            do {
                line = bufferReader.readLine()
                if (line != null){
                    AllString += line
                }
            } while (line != null)

            inputStream.close()

        }catch (ex: Exception){}

        return AllString

    }

}
