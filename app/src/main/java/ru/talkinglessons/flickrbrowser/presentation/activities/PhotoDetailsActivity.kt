package ru.talkinglessons.flickrbrowser.presentation.activities

import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_photo_details.*
import ru.talkinglessons.flickrbrowser.R
import ru.talkinglessons.flickrbrowser.domain.entities.Photo

class PhotoDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)
        activateToolbar(true)

        val photo = intent.extras?.getParcelable<Photo>(PHOTO_TRANSFER)!!

        photo_title.text = getString(R.string.photo_title_text, photo.title)
        photo_tags.text = getString(R.string.photo_tags_text, photo.tags)
        photo_author.text = photo.author
        Picasso.get()
            .load(photo.link)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(photo_image)
    }
}