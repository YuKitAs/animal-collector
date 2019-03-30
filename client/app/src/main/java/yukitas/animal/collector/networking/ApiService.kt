package yukitas.animal.collector.networking

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.model.Photo

interface ApiService {
    @GET("categories")
    fun getCategories(): Call<List<Category>>

    @GET("categories/{categoryId}/albums")
    fun getAlbumsByCategory(@Path("categoryId") categoryId: String): Call<List<Album>>

    @GET("albums/{albumId}/photos")
    fun getPhotosByAlbum(@Path("albumId") albumId: String): Call<List<Photo>>

    @GET("animals/{animalId}/photos")
    fun getPhotosByAnimal(@Path("animalId") animalId: String): Call<List<Photo>>

    @GET("photos/{photoId}")
    fun getPhotoById(@Path("photoId") id: String): Call<Photo>

    @GET("categories/{categoryId}/animals")
    fun getAnimalsByCategory(@Path("categoryId") categoryId: String): Call<List<Animal>>

    @GET("photos/{photoId}/animals")
    fun getAnimalsByPhoto(@Path("photoId") photoId: String): Call<List<Animal>>

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