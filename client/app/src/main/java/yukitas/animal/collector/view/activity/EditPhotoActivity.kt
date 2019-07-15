package yukitas.animal.collector.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
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
import java.util.stream.Collectors


/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName
    private var albums: List<Album> = emptyList()
    private var animals: List<Animal> = emptyList()
    private var albumsOfPhoto: List<Album> = emptyList()
    private var animalsOfPhoto: List<Animal> = emptyList()
    private var newAlbumName: String? = null
    private var newAnimalName: String? = null
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
                        setAlbumsAndAnimalsOfPhoto(albums, animals)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
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
        // the names should be set before resetting the lists so that they can be selected as well
        if (requestCode == RESULT_CREATE_ALBUM && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAlbumName = data.getStringExtra(Constants.ARG_ALBUM_NAME)
            }
            setAlbumList()
        } else if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAnimalName = data.getStringExtra(Constants.ARG_ANIMAL_NAME)
            }
            setAnimalList()
        }
    }

    private fun setAlbumsAndAnimalsOfPhoto(albums: List<Album>, animals: List<Animal>) {
        Log.d(TAG,
                "Fetched albums for photo $photoId: ${albums.stream().map { album -> album.name }.collect(
                        Collectors.toList())}")
        albumsOfPhoto = albums

        Log.d(TAG,
                "Fetched animals for photo $photoId: ${animals.stream().map { animal -> animal.name }.collect(
                        Collectors.toList())}")
        animalsOfPhoto = animals
    }

    private fun setAlbumList() {
        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums
                            (multiselectionAlbum as ListView).adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    albums.stream().map { album -> album.name }.collect(
                                            Collectors.toList()).toTypedArray())

                            if (isCreating) {
                                selectCurrentAlbum()
                            } else {
                                selectAlbumsOfPhoto()
                            }

                            newAlbumName?.let {
                                selectItemByName((multiselectionAlbum as ListView), it)
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
                            (multiselectionAnimal as ListView).adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    animals.stream().map { animal -> animal.name }.collect(
                                            Collectors.toList()).toTypedArray())

                            if (isCreating) {
                                selectCurrentAnimal()
                            } else {
                                selectAnimalsOfPhoto()
                            }

                            newAnimalName?.let {
                                selectItemByName((multiselectionAnimal as ListView), it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                            it.printStackTrace()
                        }))
    }

    private fun selectCurrentAlbum() {
        val albumId = intent.getStringExtra(Constants.ARG_ALBUM_ID)
        Log.d(TAG, "Current album: $albumId")
        if (albumId != null) {
            val album = albums.find { album ->
                album.id == albumId
            }
            if (album != null) {
                selectItemByName((multiselectionAlbum as ListView), album.name)
            }
        }
    }

    private fun selectCurrentAnimal() {
        val animalId = intent.getStringExtra(Constants.ARG_ANIMAL_ID)
        Log.d(TAG, "Current animal: $animalId")
        if (animalId != null) {
            val animal = animals.find { animal ->
                animal.id == animalId
            }
            if (animal != null) {
                selectItemByName((multiselectionAnimal as ListView), animal.name)
            }
        }
    }

    private fun selectItemByName(multiselection: ListView, name: String) {
        val adapter = multiselection.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == name) {
                Log.d(TAG, "Select item ${adapter.getItem(i)}")
                multiselection.setItemChecked(i, true)
            }
        }
    }

    private fun selectAlbumsOfPhoto() {
        selectItemsByNames((multiselectionAlbum as ListView),
                albumsOfPhoto.stream().map { album -> album.name }.collect(
                        Collectors.toSet()))
    }

    private fun selectAnimalsOfPhoto() {
        selectItemsByNames((multiselectionAnimal as ListView),
                animalsOfPhoto.stream().map { animal -> animal.name }.collect(
                        Collectors.toSet()))
    }

    private fun selectItemsByNames(multiselection: ListView, names: Set<String>) {
        val adapter = multiselection.adapter
        for (i in 0 until adapter.count) {
            if (names.contains(adapter.getItem(i))) {
                Log.d(TAG, "Select item ${adapter.getItem(i)}")
                multiselection.setItemChecked(i, true)
            }
        }
    }

    private fun setSaveButtonListener() {
        btnSavePhoto.setOnClickListener {
            val selectedAlbumIds = getSelectedAlbumIds()
            val selectedAnimalIds = getSelectedAnimals()

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

    private fun deletePhoto() {
        disposable.add(apiService.deletePhoto(photoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d(TAG, "Deleted unsaved photo '$photoId'")
                })
    }

    private fun getSelectedAlbumIds(): ArrayList<String> {
        val selectedAlbumPositions = (multiselectionAlbum as ListView).checkedItemPositions

        return ArrayList<String>().apply {
            for (i in 0 until selectedAlbumPositions.size()) {
                if (selectedAlbumPositions.valueAt(i)) {
                    val selectedAlbum = albums[selectedAlbumPositions.keyAt(i)]
                    Log.d(TAG, "Selected album: ${selectedAlbum.name}")

                    add(selectedAlbum.id)
                }
            }
        }
    }

    private fun getSelectedAnimals(): ArrayList<String> {
        val selectedAnimalPositions = (multiselectionAnimal as ListView).checkedItemPositions

        return ArrayList<String>().apply {
            for (i in 0 until selectedAnimalPositions.size()) {
                if (selectedAnimalPositions.valueAt(i)) {
                    val selectedAnimal = animals[selectedAnimalPositions.keyAt(i)]
                    Log.d(TAG, "Selected animal: ${selectedAnimal.name}")

                    add(selectedAnimal.id)
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
}