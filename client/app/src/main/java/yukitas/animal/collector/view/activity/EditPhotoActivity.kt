package yukitas.animal.collector.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_IS_CREATING
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_DESC
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.dto.SavePhotoRequest
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter
import java.util.stream.Collectors


/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName

    // all available albums/animals
    private var albums: List<Album> = emptyList()
    private var animals: List<Animal> = emptyList()

    // albums/animals of the existing photo
    private var photoAlbums: List<Album> = emptyList()
    private var photoAnimals: List<Animal> = emptyList()

    // newly created album/animal
    private var newAlbumId: String? = null
    private var newAnimalId: String? = null

    private var isCreating = true

    private lateinit var photoId: String

    private val RESULT_CREATE_ALBUM = 2
    private val RESULT_CREATE_ANIMAL = 3

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        photoId = intent.getStringExtra(Constants.ARG_PHOTO_ID)
        isCreating = intent.getBooleanExtra(ARG_IS_CREATING, true)
        if (!isCreating) {
            inputPhotoDesc.setText(intent.getStringExtra(ARG_PHOTO_DESC))

            disposable.add(Observable.zip<List<Album>, List<Animal>, Unit>(
                    apiService.getAlbumsByPhoto(photoId),
                    apiService.getAnimalsByPhoto(photoId),
                    BiFunction { albums, animals ->
                        setPhotoAlbumsAndAnimals(albums, animals)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        // only set lists after all albums and animals are fetched, in order to pre-select photo albums/animals
                        setAlbumList()
                        setAnimalList()
                        setSaveButtonListener()
                    }, {
                        Log.e(TAG,
                                "Some errors occurred when fetching albums and animals of photo $photoId: $it")
                        it.printStackTrace()
                    }))
        } else {
            setAlbumList()
            setAnimalList()
            setSaveButtonListener()
        }

        setAddButtonListener()
        setCancelButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (isCreating) {
            deletePhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ALBUM && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAlbumId = data.getStringExtra(Constants.ARG_ALBUM_ID)
            }
            setAlbumList()
        } else if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAnimalId = data.getStringExtra(Constants.ARG_ANIMAL_ID)
            }
            setAnimalList()
        }
    }

    private fun setPhotoAlbumsAndAnimals(albums: List<Album>, animals: List<Animal>) {
        Log.d(TAG,
                "Fetched albums for photo $photoId: ${albums.stream().map { album -> album.name }.collect(
                        Collectors.toList())}")
        photoAlbums = albums

        Log.d(TAG,
                "Fetched animals for photo $photoId: ${animals.stream().map { animal -> animal.name }.collect(
                        Collectors.toList())}")
        photoAnimals = animals
    }

    private fun setAlbumList() {
        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums

                            val multiSelectAlbumList = multiselectionAlbum as ListView
                            multiSelectAlbumList.adapter = CollectionArrayAdapter(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    ArrayList(albums))

                            if (isCreating) {
                                selectCurrentAlbum(multiSelectAlbumList)
                            } else {
                                selectPhotoAlbums(multiSelectAlbumList)
                            }

                            newAlbumId?.let {
                                selectItemByCollectionId(multiSelectAlbumList, it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all albums: $it")
                            it.printStackTrace()
                        }))
    }

    private fun setAnimalList() {
        disposable.add(
                apiService.getAllAnimals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            this.animals = animals

                            val multiSelectAnimalList = multiselectionAnimal as ListView
                            multiSelectAnimalList.adapter = CollectionArrayAdapter(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    ArrayList(animals))

                            if (isCreating) {
                                selectCurrentAnimal(multiSelectAnimalList)
                            } else {
                                selectPhotoAnimals(multiSelectAnimalList)
                            }

                            newAnimalId?.let {
                                selectItemByCollectionId(multiSelectAnimalList, it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                            it.printStackTrace()
                        }))
    }

    private fun selectCurrentAlbum(multiSelectList: ListView) {
        intent.getStringExtra(Constants.ARG_ALBUM_ID)?.let {
            Log.d(TAG, "Current album: $it")
            selectItemByCollectionId(multiSelectList, it)
        }
    }

    private fun selectCurrentAnimal(multiSelectList: ListView) {
        intent.getStringExtra(Constants.ARG_ANIMAL_ID)?.let {
            Log.d(TAG, "Current animal: $it")
            selectItemByCollectionId(multiSelectList, it)
        }
    }

    private fun selectPhotoAlbums(multiSelectList: ListView) {
        selectItemsByCollectionIds(multiSelectList,
                photoAlbums.stream().map { album -> album.id }.collect(
                        Collectors.toSet()))
    }

    private fun selectPhotoAnimals(multiselection: ListView) {
        selectItemsByCollectionIds(multiselection,
                photoAnimals.stream().map { animal -> animal.id }.collect(
                        Collectors.toSet()))
    }

    private fun selectItemByCollectionId(multiselection: ListView, id: String) {
        val adapter = multiselection.adapter as CollectionArrayAdapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).id == id) {
                Log.d(TAG, "Select item ${adapter.getItem(i).name}")
                multiselection.setItemChecked(i, true)
            }
        }
    }

    private fun selectItemsByCollectionIds(multiselection: ListView, ids: Set<String>) {
        val adapter = multiselection.adapter as CollectionArrayAdapter
        for (i in 0 until adapter.count) {
            if (ids.contains(adapter.getItem(i).id)) {
                Log.d(TAG, "Select item ${adapter.getItem(i).name}")
                multiselection.setItemChecked(i, true)
            }
        }
    }

    private fun setSaveButtonListener() {
        btnSavePhoto.setOnClickListener {
            val selectedAlbumIds = getSelectedCollectionIds((multiselectionAlbum as ListView))
            val selectedAnimalIds = getSelectedCollectionIds((multiselectionAnimal as ListView))

            if (validSelections(selectedAlbumIds, selectedAnimalIds)) {
                updatePhoto(selectedAlbumIds, selectedAnimalIds)
            }
        }
    }

    private fun setAddButtonListener() {
        btnAddAlbum.setOnClickListener {
            startActivityForResult(Intent(this, CreateAlbumActivity::class.java),
                    RESULT_CREATE_ALBUM)
        }

        btnAddAnimal.setOnClickListener {
            startActivityForResult(Intent(this, CreateAnimalActivity::class.java),
                    RESULT_CREATE_ANIMAL)
        }
    }

    private fun setCancelButtonListener() {
        btnCancelPhotoEdit.setOnClickListener {
            if (isCreating) {
                deletePhoto()
            }

            finish()
        }
    }

    private fun getSelectedCollectionIds(multiSelectList: ListView): ArrayList<String> {
        val selectedCollectionPositions = multiSelectList.checkedItemPositions

        return ArrayList<String>().apply {
            for (i in 0 until selectedCollectionPositions.size()) {
                if (selectedCollectionPositions.valueAt(i)) {
                    val selectedCollection = (multiSelectList.adapter as CollectionArrayAdapter).getItem(
                            selectedCollectionPositions.keyAt(i))
                    Log.d(TAG, "Selected collection: ${selectedCollection.name}")
                    add(selectedCollection.id)
                }
            }
        }
    }

    private fun validSelections(albumIds: List<String>, animalIds: List<String>): Boolean {
        // verify if no animal is selected
        if (animalIds.isEmpty()) {
            Toast.makeText(this, getString(R.string.warning_select_no_animal),
                    Toast.LENGTH_LONG).show()
            return false
        }

        // verify if selected albums and animals have no intersected category
        val selectedAlbumCategories = albums.stream().filter { album ->
            albumIds.contains(album.id)
        }.map { album -> album.category.id }.collect(Collectors.toSet())

        val selectedAnimalCategories = animals.stream().filter { animal ->
            animalIds.contains(animal.id)
        }.map { animal -> animal.category.id }.collect(Collectors.toSet())

        if (selectedAlbumCategories.isNotEmpty() && selectedAlbumCategories.intersect(
                        selectedAnimalCategories).isEmpty()) {
            Toast.makeText(this, getString(R.string.warning_select_invalid_collections),
                    Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun updatePhoto(albumIds: List<String>, animalIds: List<String>) {
        Log.d(TAG, "Updating photo '$photoId' with albums '$albumIds' and animals '$animalIds'")

        disposable.add(
                apiService.updatePhoto(
                        photoId,
                        SavePhotoRequest(albumIds, animalIds, inputPhotoDesc.text.toString()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d(TAG, "Updated photo '$photoId'")
                            finish()
                        }, {
                            Log.e(TAG, "Some errors occurred while updating photo '$photoId': $it")
                            it.printStackTrace()
                        }))
    }

    private fun deletePhoto() {
        disposable.add(apiService.deletePhoto(photoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d(TAG, "Deleted unsaved photo '$photoId'")
                })
    }
}