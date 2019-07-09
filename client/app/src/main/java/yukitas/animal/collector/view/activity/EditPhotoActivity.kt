package yukitas.animal.collector.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
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
    private lateinit var photoId: String
    private var albums: List<Album> = emptyList()
    private var animals: List<Animal> = emptyList()

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        photoId = intent.getStringExtra(Constants.ARG_PHOTO_ID)

        setAlbumList()
        setAnimalList()
        setAddButtonListener()
        setSaveButtonListener()
    }

    override fun onResume() {
        super.onResume()

        if (albums.isNotEmpty()) {
            setAlbumList()
        }

        if (animals.isNotEmpty()) {
            setAnimalList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
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

                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all albums: $it")
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

                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                        }))
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
            startActivity(Intent(this, CreateAlbumActivity::class.java))
        }

        btnAddAnimal.setOnClickListener {
            startActivity(Intent(this, CreateAnimalActivity::class.java))
        }
    }

    private fun getSelectedAlbumIds(): ArrayList<String> {
        val selectedAlbumPositions = (multiselectionAlbum as ListView).checkedItemPositions

        return ArrayList<String>().apply {
            for (i in 0 until selectedAlbumPositions.size()) {
                if (selectedAlbumPositions.valueAt(i)) {
                    val selectedAlbum = albums[selectedAlbumPositions.keyAt(i)]
                    Log.d(TAG, "Selected album: $selectedAlbum")

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
                    Log.d(TAG, "Selected animal: $selectedAnimal")

                    add(selectedAnimal.id)
                }
            }
        }
    }

    private fun validSelections(albumIds: List<String>, animalIds: List<String>): Boolean {
        // verify if no collection selected
        if (albumIds.isEmpty() || animalIds.isEmpty()) {
            Toast.makeText(this, getString(R.string.warning_select_empty_collections),
                    Toast.LENGTH_LONG).show()
            return false
        }

        // verify if selected collections have no intersection
        val selectedAlbumCategories = albums.stream().filter { album ->
            albumIds.contains(album.id)
        }.map { album -> album.category.id }.collect(Collectors.toSet())

        val selectedAnimalCategories = animals.stream().filter { animal ->
            animalIds.contains(animal.id)
        }.map { animal -> animal.category.id }.collect(Collectors.toSet())

        if (selectedAlbumCategories.intersect(selectedAnimalCategories).isEmpty()) {
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
                        }))
    }
}