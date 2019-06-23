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
import yukitas.animal.collector.model.Album
import java.util.stream.Collectors

class EditAnimalPhotoActivity : EditPhotoActivity() {
    private val TAG = EditAnimalPhotoActivity::class.java.simpleName
    private var albums: List<Album> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnAddCollection.text = getString(R.string.btn_new_album)
    }

    override fun onResume() {
        super.onResume()

        if (albums.isNotEmpty()) {
            setCollectionList()
        }
    }

    override fun setLabels() {
        val animalId = intent.getStringExtra(Constants.ARG_ANIMAL_ID)
        disposable.add(
                apiService.getAnimalById(animalId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animal ->
                            categoryId = animal.category.id
                            textCollectionName.text = animal.name
                            labelSelectCollection.text = getString(R.string.label_select_album)
                        }, {
                            Log.e(TAG,
                                    "Some errors occurred while fetching animal '$animalId': $it")
                        })
        )
    }

    override fun setCollectionList() {
        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums
                            dropdownCollection.adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    albums.stream().map { album -> album.name }.collect(
                                            Collectors.toList()).toTypedArray())
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all albums: $it")
                        }))
    }

    override fun setAddButtonListener() {
        btnAddCollection.setOnClickListener {
            startActivity(Intent(this, CreateAlbumActivity::class.java))
        }
    }

    override fun setSaveButtonListener() {
        btnSavePhoto.setOnClickListener {
            val selectedAlbum = albums[dropdownCollection.selectedItemPosition]
            if (selectedAlbum.category.id != categoryId) {
                Log.w(TAG,
                        "No album selected in the current category '$categoryId'")
            } else {
                Log.d(TAG, "Selected album: ${selectedAlbum.id}")
                updatePhoto(listOf(intent.getStringExtra(Constants.ARG_ANIMAL_ID)),
                        listOf(selectedAlbum.id))
            }
        }
    }
}