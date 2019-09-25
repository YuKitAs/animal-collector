package yukitas.animal.collector.networking

import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Category

private const val CATEGORY_CAT_ID = "00000000-0000-0000-0000-000000000000"
private const val CATEGORY_CAT_NAME = "cat"
private const val CATEGORY_DOG_ID = "00000000-0000-0000-0000-000000000001"
private const val CATEGORY_DOG_NAME = "dog"
private const val ALBUM_CAT1_ID = "00000000-0000-0000-0001-000000000000"
private const val ALBUM_CAT2_ID = "00000000-0000-0000-0002-000000000000"

class ApiServiceTest {
    private val baseUrl = "/"
    private val mockWebServer = MockWebServer()
    private val httpUrl = mockWebServer.url(baseUrl)

    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = ApiService.create(httpUrl.toString())
    }

    @Test
    fun getCategories() {
        val categoriesResponse = this.javaClass::class.java.getResource(
                "/data/categories.json")!!.readText()
        mockWebServer.enqueue(MockResponse().setBody(categoriesResponse))

        val observer = TestObserver<List<Category>>()
        apiService.getCategories().subscribe(observer)

        observer.assertValue(listOf(Category(CATEGORY_CAT_ID, CATEGORY_CAT_NAME),
                Category(CATEGORY_DOG_ID, CATEGORY_DOG_NAME)))
    }

    @Test
    fun getAlbumsByCategory() {
        val albumsResponse = this.javaClass::class.java.getResource(
                "/data/albums-by-category.json")!!.readText()
        mockWebServer.enqueue(MockResponse().setBody(albumsResponse))

        val observer = TestObserver<List<Album>>()
        apiService.getAlbumsByCategory(CATEGORY_CAT_ID).subscribe(observer)

        observer.assertValue(
                listOf(Album(ALBUM_CAT1_ID, Category(CATEGORY_CAT_ID, CATEGORY_CAT_NAME),
                        "cat-album-1", null),
                        Album(ALBUM_CAT2_ID, Category(CATEGORY_CAT_ID, CATEGORY_CAT_NAME),
                                "cat-album-2", null)))
    }
}