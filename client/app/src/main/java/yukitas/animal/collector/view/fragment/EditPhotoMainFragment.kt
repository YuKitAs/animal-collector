package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.dto.SavePhotoRequest
import yukitas.animal.collector.viewmodel.SelectionViewModel
import java.util.stream.Collectors

class EditPhotoMainFragment : BaseFragment() {
    private val TAG = EditPhotoMainFragment::class.java.simpleName

    private var isCreating = true
    private lateinit var photoId: String
    private lateinit var selectionViewModel: SelectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectionViewModel = activity?.run {
            ViewModelProviders.of(this)[SelectionViewModel::class.java]
        } ?: throw Exception("Invalid EditPhotoActivity")

        selectionViewModel.selectedAlbums.observe(this, Observer<List<Album>> { albums ->
            updateSelectedAlbums(albums)
        })

        selectionViewModel.selectedAnimals.observe(this, Observer<List<Animal>> { animals ->
            updateSelectedAnimals(animals)
        })
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        photoId = activity.intent.getStringExtra(Constants.ARG_PHOTO_ID)
        isCreating = activity.intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)

        return inflater.inflate(R.layout.fragment_edit_photo_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (selectionViewModel.selectedAlbums.value == null) {
            initSelectedAlbums()
        }

        if (selectionViewModel.selectedAnimals.value == null) {
            initSelectedAnimals()
        }

        if (!isCreating) {
            labelEditPhoto.setText(R.string.label_update_photo)
            inputPhotoDesc.setText(activity.intent.getStringExtra(ARG_PHOTO_DESC))
        } else {
            labelEditPhoto.setText(R.string.label_create_photo)
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

    private fun initSelectedAlbums() {
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

                                    // update selected albums
                                    selectionViewModel.selectAlbums(arrayListOf(it))
                                }) {
                                    Log.e(TAG, "Some errors occurred: $it")
                                    it.printStackTrace()
                                })
            }
        } else {
            // show photo albums
            disposable.add(apiService.getAlbumsByPhoto(photoId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ albums ->
                        textSelectedAlbums.text = if (albums.isEmpty()) getString(
                                R.string.text_none) else albums.stream().map { album -> album.name }.collect(
                                Collectors.toList()).joinToString(", ")

                        // update selected albums
                        selectionViewModel.selectAlbums(albums)
                    }, {
                        Log.e(TAG,
                                "Some errors occurred when fetching albums of photo $photoId: $it")
                        it.printStackTrace()
                    }))
        }
    }

    private fun initSelectedAnimals() {
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

                                    // update selected animals
                                    selectionViewModel.selectAnimals(arrayListOf(it))
                                }) {
                                    Log.e(TAG, "Some errors occurred: $it")
                                    it.printStackTrace()
                                })
            }
        } else {
            // show photo animals
            disposable.add(apiService.getAnimalsByPhoto(photoId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ animals ->
                        textSelectedAnimals.text = if (animals.isEmpty()) getString(
                                R.string.text_none) else animals.stream().map { animal -> animal.name }.collect(
                                Collectors.toList()).joinToString(", ")

                        // update selected animals
                        selectionViewModel.selectAnimals(animals)
                    }, {
                        Log.e(TAG,
                                "Some errors occurred when fetching animals of photo $photoId: $it")
                        it.printStackTrace()
                    }))
        }
    }

    @SuppressLint("CheckResult")
    private fun updateSelectedAlbums(albums: List<Album>?) {
        Log.d(TAG, "Updating selected albums with: $albums")

        if (albums.isNullOrEmpty()) {
            textSelectedAlbums.text = getString(R.string.text_none)
            return
        }

        val selectedAlbums: MutableList<Album> = ArrayList()
        // retrieve selected albums by ids for validation
        Single.zip(albums.stream().map { album ->
            apiService.getAlbumById(album.id)
        }.collect(Collectors.toList()).asIterable()) { obj -> obj }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach { album ->
                        selectedAlbums.add(album as Album)
                    }

                    textSelectedAlbums.text = if (selectedAlbums.isEmpty()) getString(
                            R.string.text_none) else selectedAlbums.stream().map { album -> album.name }.collect(
                            Collectors.toList()).joinToString(", ")
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                    it.printStackTrace()
                }
    }

    @SuppressLint("CheckResult")
    private fun updateSelectedAnimals(animals: List<Animal>?) {
        Log.d(TAG, "Updating selected animals with $animals")

        if (animals.isNullOrEmpty()) {
            textSelectedAnimals.text = getString(R.string.text_none)
            return
        }

        val selectedAnimals: MutableList<Animal> = ArrayList()
        // retrieve selected animals by ids for validation
        Single.zip(animals.stream().map { animal ->
            apiService.getAnimalById(animal.id)
        }.collect(Collectors.toList()).asIterable()) { obj -> obj }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach { animal ->
                        selectedAnimals.add(animal as Animal)
                    }

                    textSelectedAnimals.text = if (selectedAnimals.isEmpty()) getString(
                            R.string.text_none) else selectedAnimals.stream().map { animal -> animal.name }.collect(
                            Collectors.toSet()).joinToString(", ")
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                    it.printStackTrace()
                }
    }

    private fun savePhoto() {
        val selectedAlbumIds = selectionViewModel.selectedAlbumIds
        val selectedAnimalIds = selectionViewModel.selectedAnimalIds

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
        val selectedAlbumCategories = selectionViewModel.selectedAlbums.value.orEmpty().stream()
                .filter { album ->
                    albumIds.contains(album.id)
                }.map { album -> album.category.id }.collect(Collectors.toSet())

        val selectedAnimalCategories = selectionViewModel.selectedAnimals.value.orEmpty().stream()
                .filter { animal ->
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