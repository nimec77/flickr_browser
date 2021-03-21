package ru.talkinglessons.flickrbrowser.presentation.activities

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.talkinglessons.flickrbrowser.R

internal const val FLICKR_QUERY = "FLICK_QUERY"
internal const val PHOTO_TRANSFER = "PHOTO_TRANSFER"

open class BaseActivity : AppCompatActivity() {

    internal fun activateToolbar(enableHome: Boolean) {
        Log.d(TAG, "activateToolbar")

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(enableHome)
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}