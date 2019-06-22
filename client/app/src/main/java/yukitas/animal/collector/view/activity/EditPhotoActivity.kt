package yukitas.animal.collector.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.dto.SavePhotoRequest
import yukitas.animal.collector.networking.ApiService
import java.util.stream.Collectors


/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName
    private lateinit var categoryId: String
    private lateinit var photoId: String
    private var animals: List<Animal> = emptyList()

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        photoId = intent.getStringExtra(Constants.ARG_PHOTO_ID)

        btnAddCollection.text = getString(R.string.btn_new_animal)

        val albumId = intent.getStringExtra(Constants.ARG_ALBUM_ID)
        disposable.add(
                apiService.getAlbumById(albumId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ album ->
                            categoryId = album.category.id
                            textCollectionName.text = album.name
                            labelSelectCollection.text = getString(R.string.label_select_animal)
                        }, {
                            Log.e(TAG, "Some errors occurred while getting album '$albumId': $it")
                        })
        )

        setAnimalsDropdown()
        setAddButtonListener()
        setSaveButtonListener()
    }

    override fun onResume() {
        super.onResume()

        if (animals.isNotEmpty()) {
            setAnimalsDropdown()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setAnimalsDropdown() {
        disposable.add(
                apiService.getAllAnimals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            this.animals = animals
                            dropdownCollection.adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    animals.stream().map { animal -> animal.name }.collect(
                                            Collectors.toList()).toTypedArray())
                        }, {
                            Log.e(TAG, "Some errors occurred while getting all animals: $it")
                        }))
    }

    private fun setAddButtonListener() {
        btnAddCollection.setOnClickListener {
            startActivity(Intent(this, CreateAnimalActivity::class.java))
        }
    }

    private fun setSaveButtonListener() {
        btnSavePhoto.setOnClickListener {
            val selectedAnimal = animals[dropdownCollection.selectedItemPosition]
            if (selectedAnimal.category.id != categoryId) {
                Log.w(TAG,
                        "No animal selected in the current category '$categoryId'")
            } else {
                Log.d(TAG, "Selected animal: ${selectedAnimal.id}")
                updatePhoto(listOf(selectedAnimal.id),
                        listOf(intent.getStringExtra(Constants.ARG_ALBUM_ID)))
            }
        }
    }

    private fun updatePhoto(animalIds: List<String>, albumIds: List<String>) {
        Log.d(TAG, "Updating photo '$photoId' with animals '$animalIds' and albums '$albumIds'")

        disposable.add(
                apiService.updatePhoto(
                        photoId,
                        SavePhotoRequest(animalIds, albumIds, inputPhotoDesc.text.toString()))
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