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
import yukitas.animal.collector.model.Album
import java.util.stream.Collectors

class EditAnimalPhotoActivity : EditPhotoActivity() {
    private val TAG = EditAnimalPhotoActivity::class.java.simpleName
    private lateinit var animalId: String
    private var albums: List<Album> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animalId = intent.getStringExtra(Constants.ARG_ANIMAL_ID)

        setLabels()
        setAlbums()
    }

    override fun onResume() {
        super.onResume()

        if (albums.isNotEmpty()) {
            setAlbums()
        }
    }

    private fun setLabels() {
        btnAddCollection.text = getString(R.string.btn_new_album)
        labelCollection.text = getString(R.string.label_select_albums)

        disposable.add(
                apiService.getAnimalById(animalId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animal ->
                            categoryId = animal.category.id
                            textCollectionName.text = animal.name
                        }, {
                            Log.e(TAG,
                                    "Some errors occurred while fetching animal '$animalId': $it")
                        })
        )
    }

    private fun setAlbums() {
        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums
                            multiselectionCollection.adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
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
            val selectedAlbumPositions = multiselectionCollection.checkedItemPositions

            val selectedAlbumIds: ArrayList<String> = ArrayList()
            var albumInCategory = false

            for (i in 0 until selectedAlbumPositions.size()) {
                if (selectedAlbumPositions.valueAt(i)) {
                    val selectedAlbum = albums[selectedAlbumPositions.keyAt(i)]
                    Log.d(TAG, "Selected album: $selectedAlbum")

                    selectedAlbumIds.add(selectedAlbum.id)

                    if (selectedAlbum.category.id == categoryId) {
                        albumInCategory = true
                    }
                }
            }

            if (!albumInCategory) {
                Log.w(TAG,
                        "No album selected in the current category '$categoryId'")
                Toast.makeText(this, getString(R.string.warning_select_albums),
                        Toast.LENGTH_LONG).show()
            } else {
                updatePhoto(listOf(animalId), selectedAlbumIds)
            }
        }
    }
}