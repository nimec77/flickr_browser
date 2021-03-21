package ru.talkinglessons.flickrbrowser.presentation.activities

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.talkinglessons.flickrbrowser.R

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        activateToolbar(true)
        Log.d(TAG, "onCreate: ends")
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}