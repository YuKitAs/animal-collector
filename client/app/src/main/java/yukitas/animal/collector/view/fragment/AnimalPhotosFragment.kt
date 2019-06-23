package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_ID
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.view.activity.EditAnimalActivity
import yukitas.animal.collector.view.activity.EditAnimalPhotoActivity
import java.util.*

class AnimalPhotosFragment : PhotosFragment() {
    private val TAG = AnimalPhotosFragment::class.java.simpleName
    private lateinit var animal: Animal

    override fun setPhotos() {
        animalId = activity.intent!!.extras!!.getString(ARG_ANIMAL_ID)!!
        Log.d(TAG, "Selected animal: $animalId")

        disposable.add(apiService.getAnimalById(animalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    animal = it

                    textCollectionName.text = it.name.toUpperCase()

                    setEditButtonListener()
                    setDeleteButtonListener()
                }, {
                    Log.e(TAG, "Some errors occurred: $it")
                }))

        disposable.add(
                apiService.getPhotosByAnimal(animalId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            photosAdapter.photos = it
                        })
    }

    override fun startEditPhotoActivity(photoId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.ARG_PHOTO_ID, photoId)
        bundle.putString(Constants.ARG_ANIMAL_ID, animalId)

        val intent = Intent(activity, EditAnimalPhotoActivity::class.java).apply {
            putExtras(bundle)
        }
        activity.startActivity(intent)
    }

    override fun setEditButtonListener() {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCreating", false)
            bundle.putString(ARG_ANIMAL_ID, animal.id)
            bundle.putString(Constants.ARG_ANIMAL_NAME, animal.name)
            bundle.putStringArrayList(Constants.ARG_ANIMAL_TAGS, ArrayList(animal.tags))

            val intent = Intent(activity, EditAnimalActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    override fun setDeleteButtonListener() {
        binding.btnDeleteCollection.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.apply {
                setMessage(String.format(getString(R.string.message_delete_confirm), "animal"))
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    val animalId = animal.id
                    Log.d(TAG, "Deleting animal '$animalId'")

                    disposable.add(
                            apiService.deleteAnimal(animalId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Log.d(TAG, "Deleted animal '$animalId'")
                                        activity.onBackPressed()
                                    })
                }
                setNegativeButton(R.string.btn_confirm_negative) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.show()
        }
    }
}