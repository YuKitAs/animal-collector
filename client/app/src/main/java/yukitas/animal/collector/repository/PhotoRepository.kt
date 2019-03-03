package yukitas.animal.collector.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.networking.ApiService

object PhotoRepository {
    private val TAG = PhotoRepository::class.java.simpleName
    private val apiService by lazy { ApiService.create() }

    fun fetchPhotosByAlbumId(albumId: String): LiveData<List<Photo>> {
        val photos: MutableLiveData<List<Photo>> = MutableLiveData()
        apiService.getPhotosByAlbum(albumId).enqueue(object : Callback<List<Photo>> {
            override fun onResponse(call: Call<List<Photo>>?, response: Response<List<Photo>>?) {
                if (response!!.isSuccessful) {
                    photos.value = response.body()!!
                    Log.d(TAG, "Fetched photos by album $albumId: ${photos.value}")
                } else {
                    Log.e(TAG, "Response failed")
                }
            }

            override fun onFailure(call: Call<List<Photo>>?, t: Throwable?) {
                Log.e(TAG, "Fetching photos failed", t)
            }
        })

        return photos
    }
}