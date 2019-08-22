package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.RESULT_EDIT_ANIMAL
import yukitas.animal.collector.view.activity.EditPhotoActivity
import yukitas.animal.collector.view.fragment.dialog.EditAnimalDialogFragment

class AnimalPhotosFragment : PhotosFragment() {
    private val TAG = AnimalPhotosFragment::class.java.simpleName

    private lateinit var animalId: String
    private lateinit var animalName: String
    private lateinit var animalTags: List<String>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        animalId = activity.intent.getStringExtra(Constants.ARG_ANIMAL_ID)!!
        animalName = activity.intent.getStringExtra(Constants.ARG_ANIMAL_NAME)!!
        animalTags = activity.intent.getStringArrayExtra(Constants.ARG_ANIMAL_TAGS).asList()

        Log.d(TAG, "Selected animal: $animalId")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textCollectionName.text = animalName.toUpperCase()
    }

    override fun onResume() {
        super.onResume()

        updateAnimal()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_EDIT_ANIMAL && resultCode == Activity.RESULT_OK) {
            onResume()
        }
    }

    override fun setPhotos() {
        disposable.add(
                apiService.getPhotosByAnimal(animalId, THUMBNAIL_SIDE_LENGTH, THUMBNAIL_SIDE_LENGTH)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            photosAdapter.photos = it
                        })
    }

    override fun startEditPhotoActivity(photoId: String, recognitionEnabled: Boolean,
                                        recognizedCategory: String?) {
        val bundle = Bundle().apply {
            putString(Constants.ARG_PHOTO_ID, photoId)
            putString(Constants.ARG_ANIMAL_ID, animalId)
            putBoolean(Constants.FLAG_RECOGNITION_ENABLED, recognitionEnabled)

            if (!recognizedCategory.isNullOrBlank()) {
                putString(Constants.ARG_CATEGORY_NAME, recognizedCategory)
            }
        }

        val intent = Intent(activity, EditPhotoActivity::class.java).apply {
            putExtras(bundle)
        }
        activity.startActivity(intent)
    }

    override fun editCollection() {
        val editAnimalDialog = EditAnimalDialogFragment()
        editAnimalDialog.setTargetFragment(this, RESULT_EDIT_ANIMAL)
        editAnimalDialog.show(activity.supportFragmentManager,
                EditAnimalDialogFragment::class.java.simpleName)
    }

    override fun deleteCollection() {
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle(String.format(getString(R.string.message_delete_confirm),
                    "animal"))
            setMessage(R.string.message_delete_confirm_animal)
            setPositiveButton(R.string.btn_confirm_positive
            ) { _, _ ->
                Log.d(TAG, "Deleting animal '$animalId'")

                disposable.add(
                        apiService.deleteAnimal(animalId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    Log.d(TAG, "Deleted animal '$animalId'")
                                    Toast.makeText(activity,
                                            getString(R.string.message_delete_animal_success),
                                            Toast.LENGTH_SHORT).show()
                                    activity.onBackPressed()
                                })
            }
            setNegativeButton(R.string.btn_confirm_negative) { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.show()
    }

    private fun updateAnimal() {
        disposable.add(apiService.getAnimalById(animalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    animalName = it.name
                    animalTags = it.tags

                    textCollectionName.text = animalName.toUpperCase()
                }, {
                    Log.e(TAG, "Some errors occurred: $it")
                }))
    }
}