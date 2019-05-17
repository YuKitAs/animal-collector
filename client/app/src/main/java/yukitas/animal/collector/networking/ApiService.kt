package yukitas.animal.collector.networking

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.model.dto.SaveAlbumRequest
import yukitas.animal.collector.model.dto.SaveAnimalRequest
import yukitas.animal.collector.model.dto.SavePhotoRequest
import yukitas.animal.collector.model.dto.SavePhotoResponse

interface ApiService {
    /**
     * GET
     */
    @GET("categories")
    fun getCategories(): Observable<List<Category>>

    @GET("categories/{categoryId}/albums")
    fun getAlbumsByCategory(@Path("categoryId") categoryId: String): Observable<List<Album>>

    @GET("albums/{id}")
    fun getAlbumById(@Path("id") id: String): Single<Album>

    @GET("albums/{albumId}/photos")
    fun getPhotosByAlbum(@Path("albumId") albumId: String): Observable<List<Photo>>

    @GET("albums/{albumId}/photos/latest")
    fun getAlbumThumbnail(@Path("albumId") albumId: String): Maybe<Photo>

    @GET("animals/{animalId}/photos")
    fun getPhotosByAnimal(@Path("animalId") animalId: String): Observable<List<Photo>>

    @GET("animals/{animalId}/photos/latest")
    fun getAnimalThumbnail(@Path("animalId") animalId: String): Maybe<Photo>

    @GET("photos/{photoId}")
    fun getPhotoById(@Path("photoId") id: String): Single<Photo>

    @GET("categories/{categoryId}/animals")
    fun getAnimalsByCategory(@Path("categoryId") categoryId: String): Observable<List<Animal>>

    @GET("animals/{id}")
    fun getAnimalById(@Path("id") id: String): Single<Animal>

    @GET("photos/{photoId}/animals")
    fun getAnimalsByPhoto(@Path("photoId") photoId: String): Observable<List<Animal>>

    /**
     * POST
     */
    @POST("categories/{categoryId}/albums")
    fun createAlbum(@Path(
            "categoryId") categoryId: String, @Body album: SaveAlbumRequest): Single<Album>

    @POST("categories/{categoryId}/animals")
    fun createAnimal(@Path(
            "categoryId") categoryId: String, @Body animal: SaveAnimalRequest): Single<Animal>

    @Multipart
    @POST("photos")
    fun createPhoto(@Part photo: MultipartBody.Part): Single<SavePhotoResponse>

    /**
     * PUT
     */
    @PUT("albums/{albumId}")
    fun updateAlbum(@Path("albumId") albumId: String, @Body album: SaveAlbumRequest): Completable

    @PUT("animals/{animalId}")
    fun updateAnimal(@Path(
            "animalId") animalId: String, @Body animal: SaveAnimalRequest): Completable

    @PUT("photos/{photoId}")
    fun updatePhoto(@Path("photoId") photoId: String, @Body photo: SavePhotoRequest): Completable

    /**
     * DELETE
     */
    @DELETE("albums/{albumId}")
    fun deleteAlbum(@Path("albumId") albumId: String): Completable

    @DELETE("animals/{animalId}")
    fun deleteAnimal(@Path("animalId") animalId: String): Completable

    @DELETE("photos/{photoId}")
    fun deletePhoto(@Path("photoId") photoId: String): Completable


    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.178.51:8080/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}