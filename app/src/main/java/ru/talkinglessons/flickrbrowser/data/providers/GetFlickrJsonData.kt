package ru.talkinglessons.flickrbrowser.data.providers

import android.util.Log
import org.json.JSONObject
import ru.talkinglessons.flickrbrowser.domain.entities.Photo
import java.lang.Exception

class GetFlickrJsonData(private val listener: OnDataAvailable) {

    interface OnDataAvailable {
        fun onDataAvailable(data: List<Photo>)
        fun onError(exception: Exception)
    }

    fun toJson(dataString: String) {
        val photoList = ArrayList<Photo>()
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

                val photoObject = Photo(title, author, authorId, link, tags, photoUrl)

                photoList.add(photoObject)
                Log.d(TAG, "toJson $photoObject")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "toJson: Error processing Json data ${e.message}")
            listener.onError(e)
            return
        } finally {
            Log.d(TAG, "toJson ends")
        }

        listener.onDataAvailable(photoList)
    }

    companion object {
        private const val TAG = "GetFlickJsonData"
    }
}