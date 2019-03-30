package yukitas.animal.collector.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.networking.ApiService

object AnimalRepository {
    private val TAG = AnimalRepository::class.java.simpleName
    private val apiService by lazy { ApiService.create() }

    fun fetchAnimalsByCategory(categoryId: String): LiveData<List<Animal>> {
        val animals: MutableLiveData<List<Animal>> = MutableLiveData()
        apiService.getAnimalsByCategory(categoryId).enqueue(object : Callback<List<Animal>> {
            override fun onResponse(call: Call<List<Animal>>?, response: Response<List<Animal>>?) {
                if (response!!.isSuccessful) {
                    animals.value = response.body()!!
                    Log.d(TAG, "Fetched animals by category $categoryId: ${animals.value}")
                } else {
                    Log.e(TAG, "Response failed")
                }
            }

            override fun onFailure(call: Call<List<Animal>>?, t: Throwable?) {
                Log.e(TAG, "Fetching animals by category $categoryId failed", t)
            }
        })

        return animals
    }

    fun fetchAnimalsByPhoto(photoId: String): LiveData<List<Animal>> {
        val animals: MutableLiveData<List<Animal>> = MutableLiveData()
        apiService.getAnimalsByPhoto(photoId).enqueue(object : Callback<List<Animal>> {
            override fun onResponse(call: Call<List<Animal>>?, response: Response<List<Animal>>?) {
                if (response!!.isSuccessful) {
                    animals.value = response.body()!!
                    Log.d(TAG, "Fetched animals by photo $photoId: ${animals.value}")
                } else {
                    Log.e(TAG, "Response failed")
                }
            }

            override fun onFailure(call: Call<List<Animal>>?, t: Throwable?) {
                Log.e(TAG, "Fetching animals by category $photoId failed", t)
            }
        })

        return animals
    }
}