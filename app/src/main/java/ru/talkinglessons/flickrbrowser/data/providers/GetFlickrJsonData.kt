package ru.talkinglessons.flickrbrowser.data.providers

import org.json.JSONObject
import ru.talkinglessons.flickrbrowser.domain.entities.Photo
import java.lang.Exception

class GetFlickrJsonData(private val listener: OnDataAvailable) {

    interface OnDataAvailable {
        fun onDataAvailable(data: List<Photo>)
        fun onError(exception: Exception)
    }

    fun toJson(dataString: String) {
        try {
            val jsonData = JSONObject(dataString)
            val itemsArray = jsonData.getJSONArray("items")

            for (i in 0 until itemsArray.length()) {
                val jsonPhoto = itemsArray.getJSONObject(i)
                val title = jsonPhoto.getString("title")
                val author = jsonPhoto.getString("author")
                val authorId = jsonPhoto.getString("author_id")
                val tags = jsonPhoto.getString("tags")
                val jsonMedia = jsonPhoto.getJSONObject("media")
                val photoUrl = jsonMedia.getString("m")
                val link = photoUrl.replaceFirst("_m.jpg", "_b.jpg")
            }
        } catch (e: Exception) {

        }
    }

    companion object {
        private const val TAG = "GetFlickJsonData"
    }
}