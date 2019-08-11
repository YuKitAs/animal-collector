package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.view.btnCloseDialog
import kotlinx.android.synthetic.main.dialog_edit_animal.*
import kotlinx.android.synthetic.main.dialog_edit_animal.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAnimalRequest
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.utility.tagsFromText

/**
 * Create or update an animal in a specific category
 */
class EditAnimalDialogFragment : DialogFragment() {
    private val TAG = EditAnimalDialogFragment::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_animal, container,
                false)

        view.inputAnimalName.setText(activity.intent.getStringExtra(Constants.ARG_ANIMAL_NAME))
        view.inputAnimalTags.setText(
                activity.intent.getStringArrayExtra(Constants.ARG_ANIMAL_TAGS).joinToString(
                        " "))

        view.btnSaveAnimal.setOnClickListener {
            val animalName = inputAnimalName.text.toString()
            val animalTags = tagsFromText(inputAnimalTags.text)

            Log.d(TAG,
                    "Updating animal with name '$animalName' and tags '$animalTags'")

            disposable.add(
                    apiService.updateAnimal(activity.intent.getStringExtra(Constants.ARG_ANIMAL_ID),
                            SaveAnimalRequest(animalName, animalTags))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                Log.d(TAG, "Updated animal")
                                Toast.makeText(context,
                                        getString(R.string.message_update_animal_success),
                                        Toast.LENGTH_SHORT).show()

                                dialog.dismiss()

                                targetFragment.onActivityResult(targetRequestCode,
                                        Activity.RESULT_OK, activity.intent)
                            })
        }


        view.btnCloseDialog.setOnClickListener { dialog.dismiss() }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}