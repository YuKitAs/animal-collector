package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.view.btnCloseDialog
import kotlinx.android.synthetic.main.dialog_create_animal.*
import kotlinx.android.synthetic.main.dialog_create_animal.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAnimalRequest
import yukitas.animal.collector.utility.tagsFromText

class CreateAnimalDialogFragment : CreateCollectionDialogFragment() {
    private val TAG = CreateAnimalDialogFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_animal, container,
                false)

        setCategoryList()

        view.btnSaveAnimal.setOnClickListener {
            Log.d(TAG,
                    "Creating animal for category '$categoryId' with name '${inputAnimalName.text}'")

            disposable.add(
                    apiService.createAnimal(categoryId,
                            SaveAnimalRequest(inputAnimalName.text.toString(),
                                    tagsFromText(inputAnimalTags.text)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { animal ->
                                Log.d(TAG, "Created animal: $animal")

                                dialog.dismiss()

                                activity.intent.putExtra(Constants.ARG_ANIMAL_ID, animal.id)
                                targetFragment.onActivityResult(targetRequestCode,
                                        Activity.RESULT_OK, activity.intent)
                            })
        }

        view.btnCloseDialog.setOnClickListener { dialog.dismiss() }

        return view
    }
}