package ru.talkinglessons.flickrbrowser

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.talkinglessons.flickrbrowser.data.providers.DownloadStatus
import ru.talkinglessons.flickrbrowser.data.providers.GetFlickrJsonData
import ru.talkinglessons.flickrbrowser.data.providers.GetRawData
import ru.talkinglessons.flickrbrowser.domain.entities.Photo
import ru.talkinglessons.flickrbrowser.presentation.adapters.FlickrRecyclerViewAdapter
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), GetFlickrJsonData.OnDataAvailable {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = flickrRecyclerViewAdapter
        val getRawData = GetRawData()
        ioScope.launch {
            val uri = createUri(FLICKR_API, "android,11", "en", true)
            val result = getRawData.download(uri)
            onDownloadComplete(result.first, result.second)
        }

        Log.d(TAG, "onCreate ends")
    }

    private fun createUri(baseURL: String, searchCriteria: String, lang: String, mathAll: Boolean): String {
        Log.d(TAG, "createUri starts")

        return Uri.parse(baseURL)
                .buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", if (mathAll) "ALL" else "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, "onOptionItemSelected called")
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDownloadComplete(status: DownloadStatus, data: String) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called, data is $data")

            val getFlickrJsonData = GetFlickrJsonData(this)
            getFlickrJsonData.toJson(data)
        } else {
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG, "onDataAvailable called")
        mainScope.launch {
            flickrRecyclerViewAdapter.loadNewData(data)
        }
        Log.d(TAG, "onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, "onError called with ${exception.message}")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val FLICKR_API = "https://api.flickr.com/services/feeds/photos_public.gne"
    }

}