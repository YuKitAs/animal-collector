package yukitas.animal.collector.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_photo.*
import kotlinx.android.synthetic.main.listview_collection_multiselection.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import java.util.stream.Collectors


class EditAlbumPhotoActivity : EditPhotoActivity() {
    private val TAG = EditAlbumPhotoActivity::class.java.simpleName
    private lateinit var albumId: String
    private var animals: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        albumId = intent.getStringExtra(Constants.ARG_ALBUM_ID)

        setLabels()
        setAnimals()
    }

    override fun onResume() {
        super.onResume()

        if (animals.isNotEmpty()) {
            setAnimals()
        }
    }

    private fun setLabels() {
        btnAddCollection.text = getString(R.string.btn_new_animal)
        labelCollection.text = getString(R.string.label_select_animals)

        disposable.add(
                apiService.getAlbumById(albumId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ album ->
                            categoryId = album.category.id
                            textCollectionName.text = album.name
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching album '$albumId': $it")
                        })
        )
    }

    private fun setAnimals() {
        disposable.add(
                apiService.getAllAnimals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            this.animals = animals
                            multiselectionCollection.adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
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
            val selectedAnimalPositions = multiselectionCollection.checkedItemPositions

            val selectedAnimalIds: ArrayList<String> = ArrayList()
            var animalInCategory = false

            for (i in 0 until selectedAnimalPositions.size()) {
                if (selectedAnimalPositions.valueAt(i)) {
                    val selectedAnimal = animals[selectedAnimalPositions.keyAt(i)]
                    Log.d(TAG, "Selected animal: $selectedAnimal")

                    selectedAnimalIds.add(selectedAnimal.id)

                    if (selectedAnimal.category.id == categoryId) {
                        animalInCategory = true
                    }
                }
            }

            if (!animalInCategory) {
                Log.w(TAG,
                        "No animal selected in the current category '$categoryId'")
                Toast.makeText(this, getString(R.string.warning_select_animals),
                        Toast.LENGTH_LONG).show()
            } else {
                updatePhoto(selectedAnimalIds, listOf(albumId))
            }
        }
    }
}