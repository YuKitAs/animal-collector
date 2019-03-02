package yukitas.animal.collector.networking

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import yukitas.animal.collector.model.Category

interface ApiService {
    @GET("categories")
    fun getCategories(): Call<List<Category>>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.178.51:8080/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}