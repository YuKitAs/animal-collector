package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_photo_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_DESC
import yukitas.animal.collector.common.Constants.Companion.ARG_SELECTED_ALBUM_IDS
import yukitas.animal.collector.common.Constants.Companion.ARG_SELECTED_ANIMAL_IDS
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.dto.SavePhotoRequest
import java.util.stream.Collectors

class EditPhotoMainFragment : BaseFragment() {
    private val TAG = EditPhotoMainFragment::class.java.simpleName

    private var selectedAlbumIds: List<String> = emptyList()
    private var selectedAlbums: MutableList<Album> = ArrayList()

    private var selectedAnimalIds: List<String> = emptyList()
    private var selectedAnimals: MutableList<Animal> = ArrayList()

    private var isCreating = true

    private lateinit var photoId: String

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        photoId = activity.intent.getStringExtra(Constants.ARG_PHOTO_ID)
        isCreating = activity.intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)

        return inflater.inflate(R.layout.fragment_edit_photo_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSelectedAlbums()
        setSelectedAnimals()

        if (!isCreating) {
            inputPhotoDesc.setText(activity.intent.getStringExtra(ARG_PHOTO_DESC))
        }

        btnSelectAlbums.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_edit_photo_container, SelectAlbumsFragment())
                    .commit()
        }

        btnSelectAnimals.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_edit_photo_container, SelectAnimalFragment())
                    .commit()
        }

        btnSavePhoto.setOnClickListener {
            savePhoto()
        }

        btnCancelPhotoEdit.setOnClickListener {
            if (isCreating) {
                deletePhoto()
            }

            activity.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    @SuppressLint("CheckResult")
    private fun setSelectedAlbums() {
        selectedAlbumIds = activity.intent.getStringArrayListExtra(ARG_SELECTED_ALBUM_IDS).orEmpty()

        if (selectedAlbumIds.isNotEmpty()) {
            // retrieve selected albums by ids for validation
            Single.zip(selectedAlbumIds.stream().map { albumId ->
                apiService.getAlbumById(albumId)
            }.collect(Collectors.toList()).asIterable()) { obj -> obj }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.forEach { album ->
                            selectedAlbums.add(album as Album)
                        }

                        textSelectedAlbums.text = selectedAlbums.stream().map { album -> album.name }.collect(
                                Collectors.toList()).joinToString(", ")
                    }) {
                        Log.e(TAG, "Some errors occurred: $it")
                    }
        } else {
            if (isCreating) {
                activity.intent.getStringExtra(Constants.ARG_ALBUM_ID)?.let {
                    // show current album
                    disposable.add(
                            apiService.getAlbumById(
                                    activity.intent.getStringExtra(Constants.ARG_ALBUM_ID))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        textSelectedAlbums.text = it.name

                                        selectedAlbumIds = arrayListOf(it.id)
                                        activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                                                selectedAlbumIds as ArrayList)
                                    }) {
                                        Log.e(TAG, "Some errors occurred: $it")
                                    })
                }
            } else {
                // show photo albums
                disposable.add(apiService.getAlbumsByPhoto(photoId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            textSelectedAlbums.text = albums.stream().map { album -> album.name }.collect(
                                    Collectors.toList()).joinToString(", ")

                            selectedAlbumIds = albums.stream().map { album -> album.id }.collect(
                                    Collectors.toList())

                            activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                                    selectedAlbumIds as ArrayList)
                        }, {
                            Log.e(TAG,
                                    "Some errors occurred when fetching albums of photo $photoId: $it")
                            it.printStackTrace()
                        }))
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setSelectedAnimals() {
        selectedAnimalIds = activity.intent.getStringArrayListExtra(
                ARG_SELECTED_ANIMAL_IDS).orEmpty()

        if (selectedAnimalIds.isNotEmpty()) {
            // retrieve selected animals by ids for validation
            Single.zip(selectedAnimalIds.stream().map { animalId ->
                apiService.getAnimalById(animalId)
            }.collect(Collectors.toList()).asIterable()) { obj -> obj }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.forEach { animal ->
                            selectedAnimals.add(animal as Animal)
                        }

                        textSelectedAnimals.text = selectedAnimals.stream().map { animal -> animal.name }.collect(
                                Collectors.toSet()).joinToString(", ")
                    }) {
                        Log.e(TAG, "Some errors occurred: $it")
                    }
        } else {
            if (isCreating) {
                activity.intent.getStringExtra(Constants.ARG_ANIMAL_ID)?.let {
                    // show current animal
                    disposable.add(
                            apiService.getAnimalById(
                                    activity.intent.getStringExtra(Constants.ARG_ANIMAL_ID))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        textSelectedAnimals.text = it.name

                                        selectedAnimalIds = arrayListOf(it.id)
                                        activity.intent.putExtra(ARG_SELECTED_ANIMAL_IDS,
                                                selectedAnimalIds as ArrayList)
                                    }) {
                                        Log.e(TAG, "Some errors occurred: $it")
                                    })
                }
            } else {
                // show photo animals
                disposable.add(apiService.getAnimalsByPhoto(photoId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            textSelectedAnimals.text = animals.stream().map { album -> album.name }.collect(
                                    Collectors.toList()).joinToString(", ")

                            selectedAnimalIds = animals.stream().map { animal -> animal.id }.collect(
                                    Collectors.toList())
                            activity.intent.putExtra(ARG_SELECTED_ANIMAL_IDS,
                                    selectedAnimalIds as ArrayList)
                        }, {
                            Log.e(TAG,
                                    "Some errors occurred when fetching animals of photo $photoId: $it")
                            it.printStackTrace()
                        }))
            }
        }
    }

    private fun savePhoto() {
        if (validSelections(selectedAlbumIds, selectedAnimalIds)) {
            updatePhoto(selectedAlbumIds, selectedAnimalIds)
        }
    }

    private fun validSelections(albumIds: List<String>, animalIds: List<String>): Boolean {
        // verify if no animal is selected
        if (animalIds.isEmpty()) {
            Toast.makeText(activity, getString(R.string.warning_select_no_animal),
                    Toast.LENGTH_LONG).show()
            return false
        }

        // verify if selected albums and animals have no intersected category
        val selectedAlbumCategories = selectedAlbums.stream().filter { album ->
            albumIds.contains(album.id)
        }.map { album -> album.category.id }.collect(Collectors.toSet())

        val selectedAnimalCategories = selectedAnimals.stream().filter { animal ->
            animalIds.contains(animal.id)
        }.map { animal -> animal.category.id }.collect(Collectors.toSet())

        if (selectedAlbumCategories.isNotEmpty() && selectedAlbumCategories.intersect(
                        selectedAnimalCategories).isEmpty()) {
            Toast.makeText(activity, getString(R.string.warning_select_invalid_collections),
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
                            activity.finish()
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