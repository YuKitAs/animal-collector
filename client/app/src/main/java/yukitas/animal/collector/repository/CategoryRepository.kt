package yukitas.animal.collector.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.networking.ApiService

object CategoryRepository {
    private val TAG = CategoryRepository::class.java.simpleName
    private val apiService by lazy { ApiService.create() }

    fun fetchCategories(): LiveData<List<Category>> {
        val categories: MutableLiveData<List<Category>> = MutableLiveData()
        apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>?, response: Response<List<Category>>?) {
                if (response!!.isSuccessful) {
                    categories.value = response.body().orEmpty()
                    Log.d(TAG, "Fetched categories: ${categories.value}")
                } else {
                    Log.e(TAG, "Response failed")
                }
            }

            override fun onFailure(call: Call<List<Category>>?, t: Throwable?) {
                Log.e(TAG, "Fetching categories failed", t)
            }
        })

        return categories
    }
}