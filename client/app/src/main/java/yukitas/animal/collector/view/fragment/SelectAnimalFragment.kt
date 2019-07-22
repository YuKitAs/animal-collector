package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_select_collection.*
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.view.activity.CreateAnimalActivity
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter

class SelectAnimalFragment : SelectCollectionFragment() {
    private val TAG = SelectAnimalFragment::class.java.simpleName

    // all animals
    private var animals: List<Animal> = emptyList()

    private var newAnimalId: String? = null

    private val RESULT_CREATE_ANIMAL = 3

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCollection.text = getString(yukitas.animal.collector.R.string.btn_new_animal)

        setList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAnimalId = data.getStringExtra(Constants.ARG_ANIMAL_ID)
                Log.d(TAG, "Newly created animal id: $newAnimalId")
            }
            setList()
        }
    }

    override fun setList() {
        val selectedAnimalIds = selectionViewModel.selectedAnimalIds

        disposable.add(
                apiService.getAllAnimals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            this.animals = animals

                            val multiSelectAnimalList = multiSelectListCollection as ListView
                            multiSelectAnimalList.adapter = CollectionArrayAdapter(activity,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    ArrayList(animals))

                            if (!selectedAnimalIds.isNullOrEmpty()) {
                                Log.d(TAG, "Selected animals: $selectedAnimalIds")
                                selectItemsByCollectionIds(multiSelectAnimalList, selectedAnimalIds)
                            }

                            newAnimalId?.let {
                                selectItemByCollectionId(multiSelectAnimalList, it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                            it.printStackTrace()
                        }))
    }

    override fun createNewCollection() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())

        startActivityForResult(Intent(activity, CreateAnimalActivity::class.java),
                RESULT_CREATE_ANIMAL)
    }

    override fun confirmSelectedCollections() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())

        activity.supportFragmentManager.beginTransaction()
                .replace(yukitas.animal.collector.R.id.fragment_edit_photo_container,
                        EditPhotoMainFragment())
                .commit()
    }
}