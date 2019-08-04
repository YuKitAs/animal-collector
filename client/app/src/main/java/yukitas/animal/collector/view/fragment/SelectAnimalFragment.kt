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
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.view.activity.CreateAnimalActivity
import yukitas.animal.collector.view.activity.EditAnimalActivity
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter

class SelectAnimalFragment : SelectCollectionFragment() {
    private val TAG = SelectAnimalFragment::class.java.simpleName

    // all animals
    private var animals: List<Animal> = emptyList()

    private lateinit var categoryId: String
    private var animalsInCategory = false

    private var newAnimalId: String? = null

    private val RESULT_CREATE_ANIMAL = 3

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments == null || arguments.getString(
                        Constants.ARG_DETECTED_CATEGORY).isNullOrBlank()) {
            animalsInCategory = false

            labelSelectCollection.text = getString(R.string.label_select_animals)
            setList()
        } else {
            animalsInCategory = true

            val categoryName = arguments.getString(Constants.ARG_DETECTED_CATEGORY)!!

            labelSelectCollection.text = String.format(
                    getString(R.string.label_select_animals_in_category), categoryName)
            getCategoryAndSetList(categoryName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAnimalId = data.getStringExtra(Constants.ARG_ANIMAL_ID)
                Log.d(TAG, "Newly created animal id: $newAnimalId")
            }

            if (animalsInCategory) {
                getAnimalsAndSetList(categoryId)
            } else {
                setList()
            }
        }
    }

    override fun setList() {
        disposable.add(
                apiService.getAllAnimals()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            this.animals = animals
                            setListForAnimals(animals)
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all animals: $it")
                            it.printStackTrace()
                        }))
    }

    private fun getCategoryAndSetList(detectedCategory: String) {
        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ categories ->
                            categories.stream().filter { category ->
                                category.name.equals(detectedCategory, ignoreCase = true)
                            }.findAny().ifPresent { category ->
                                categoryId = category.id
                                getAnimalsAndSetList(categoryId)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all categories: $it")
                            it.printStackTrace()
                        })
        )
    }

    private fun getAnimalsAndSetList(categoryId: String) {
        disposable.add(
                apiService.getAnimalsByCategory(categoryId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ animals ->
                            setListForAnimals(animals)
                        }, {
                            Log.e(TAG,
                                    "Some errors occurred while fetching animals by category $categoryId: $it")
                            it.printStackTrace()
                        })
        )
    }

    private fun setListForAnimals(animals: List<Animal>) {
        val sortedAnimals = animals.sortedBy { it.category.name }.sortedBy { it.name }

        val multiSelectAnimalList = multiSelectListCollection as ListView
        multiSelectAnimalList.adapter = CollectionArrayAdapter(activity,
                android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1,
                ArrayList(sortedAnimals))

        val selectedAnimalIds = selectionViewModel.selectedAnimalIds
        if (!selectedAnimalIds.isNullOrEmpty()) {
            Log.d(TAG, "Selected animals: $selectedAnimalIds")
            selectItemsByCollectionIds(multiSelectAnimalList, selectedAnimalIds)
        }

        newAnimalId?.let {
            selectItemByCollectionId(multiSelectAnimalList, it)
        }
    }

    override fun createNewCollection() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())

        if (animalsInCategory) {
            val bundle = Bundle().apply {
                putString(Constants.ARG_CATEGORY_ID, categoryId)
            }
            startActivityForResult(Intent(activity, EditAnimalActivity::class.java).apply {
                putExtras(bundle)
            }, RESULT_CREATE_ANIMAL)
        } else {
            startActivityForResult(Intent(activity, CreateAnimalActivity::class.java),
                    RESULT_CREATE_ANIMAL)
        }
    }

    override fun confirmSelectedCollections() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())

        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container,
                        EditPhotoMainFragment())
                .commit()
    }
}