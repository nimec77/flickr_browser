package ru.talkinglessons.flickrbrowser

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.talkinglessons.flickrbrowser.data.providers.DownloadStatus
import ru.talkinglessons.flickrbrowser.data.providers.GetRawData
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        val getRawData = GetRawData()
        ioScope.launch {
            val result = getRawData.download(FLICKR_API)
//            onDownloadComplete(result.first, result.second)
        }

        Log.d(TAG, "onCreate ends")
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
        } else {
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is: $data")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val FLICKR_API = "https://api.flickr.com/services/feeds/photos_public.gne?tags=android,oreo" +
                "&format=json&nojsoncallback=1"
    }
}