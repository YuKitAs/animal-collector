package yukitas.animal.collector.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_photo.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import java.util.stream.Collectors

class EditAlbumPhotoActivity: EditPhotoActivity() {
    private val TAG = EditAlbumPhotoActivity::class.java.simpleName
    private var animals: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnAddCollection.text = getString(R.string.btn_new_animal)
    }

    override fun onResume() {
        super.onResume()

        if (animals.isNotEmpty()) {
            setCollectionList()
        }
    }

    override fun setLabels() {
        val albumId = intent.getStringExtra(Constants.ARG_ALBUM_ID)
        disposable.add(
                apiService.getAlbumById(albumId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ album ->
                            categoryId = album.category.id
                            textCollectionName.text = album.name
                            labelSelectCollection.text = getString(R.string.label_select_animal)
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching album '$albumId': $it")
                        })
        )
    }

    override fun setCollectionList() {
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
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                        }))
    }

    override fun setAddButtonListener() {
        btnAddCollection.setOnClickListener {
            startActivity(Intent(this, CreateAnimalActivity::class.java))
        }
    }

    override fun setSaveButtonListener() {
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
}