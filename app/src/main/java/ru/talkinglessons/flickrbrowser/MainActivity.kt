package ru.talkinglessons.flickrbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.talkinglessons.flickrbrowser.data.providers.DownloadStatus
import ru.talkinglessons.flickrbrowser.data.providers.GetFlickrJsonData
import ru.talkinglessons.flickrbrowser.data.providers.GetRawData
import ru.talkinglessons.flickrbrowser.domain.entities.Photo
import ru.talkinglessons.flickrbrowser.presentation.activities.*
import ru.talkinglessons.flickrbrowser.presentation.activities.FLICKR_QUERY
import ru.talkinglessons.flickrbrowser.presentation.activities.PHOTO_TRANSFER
import ru.talkinglessons.flickrbrowser.presentation.adapters.FlickrRecyclerViewAdapter
import ru.talkinglessons.flickrbrowser.presentation.listeners.RecyclerItemClickListener

class MainActivity : BaseActivity(), GetFlickrJsonData.OnDataAvailable,
    RecyclerItemClickListener.OnRecyclerClickListener {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activateToolbar(false)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_view, this))
        recycler_view.adapter = flickrRecyclerViewAdapter

        Log.d(TAG, "onCreate ends")
    }

    private fun createUri(
        baseURL: String,
        searchCriteria: String,
        lang: String,
        mathAll: Boolean
    ): String {
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
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDownloadComplete(status: DownloadStatus, data: String) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called")

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

    override fun onResume() {
        Log.d(TAG, "onResume starts")
        super.onResume()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val queryResult = sharedPreferences.getString(FLICKR_QUERY, "")!!

        if (queryResult.isNotEmpty()) {
            val getRawData = GetRawData()
            ioScope.launch {
                val uri = createUri(FLICKR_API, queryResult, "en", true)
                val result = getRawData.download(uri)
                onDownloadComplete(result.first, result.second)
            }
        }

        Log.d(TAG, "onResume ends")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, "onItemClick: starts")
        Toast.makeText(this, "Normal tap at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, "onItemLongClick: starts")
//        Toast.makeText(this, "Long tap at position $position", Toast.LENGTH_SHORT).show()
        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if (photo != null) {
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra(PHOTO_TRANSFER, photo)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val FLICKR_API = "https://api.flickr.com/services/feeds/photos_public.gne"
    }

}