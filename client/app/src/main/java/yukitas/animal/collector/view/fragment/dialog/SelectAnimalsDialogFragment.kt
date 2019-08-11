package yukitas.animal.collector.view.fragment.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_select_collection.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_NAME
import yukitas.animal.collector.common.Constants.Companion.RESULT_CREATE_ANIMAL
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter

/**
 * Select from all animals or animals in a specific (recognized) category
 */
class SelectAnimalsDialogFragment : SelectCollectionDialogFragment() {
    private val TAG = SelectAnimalsDialogFragment::class.java.simpleName

    // all animals
    private var animals: List<Animal> = emptyList()

    private lateinit var category: Category
    private var animalsInCategory = false

    private var newAnimalId: String? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        labelAddCollection.text = getString(R.string.label_add_animal)

        layoutAddCollection.setOnClickListener {
            createNewCollection()
        }

        btnSaveSelection.setOnClickListener {
            confirmSelectedCollections()
            dialog.dismiss()
        }

        btnCloseDialog.setOnClickListener { dialog.dismiss() }

        if (arguments == null || arguments.getString(
                        ARG_CATEGORY_NAME).isNullOrBlank()) {
            Log.d(TAG, "Selecting from all animals")
            animalsInCategory = false

            labelSelectCollection.text = getString(R.string.label_select_animals)
            setList()
        } else {
            animalsInCategory = true

            val categoryName = arguments.getString(ARG_CATEGORY_NAME)!!

            Log.d(TAG, "Selecting from animals in category: $categoryName")

            labelSelectCollection.text = String.format(
                    getString(R.string.label_select_animals_in_category),
                    categoryName)
            getCategoryAndSetList(categoryName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAnimalId = data.getStringExtra(Constants.ARG_ANIMAL_ID)
                Log.d(TAG, "Newly created animal id: $newAnimalId")
            }

            // update list with new animal
            if (animalsInCategory) {
                getAnimalsByCategoryAndSetList(category.id)
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

    override fun createNewCollection() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())

        if (animalsInCategory) {
            val createAnimalDialog = CreateAnimalDialogFragment()
            createAnimalDialog.setTargetFragment(this, RESULT_CREATE_ANIMAL)
            createAnimalDialog.arguments = Bundle().apply {
                putString(ARG_CATEGORY_ID, category.id)
                putString(ARG_CATEGORY_NAME, category.name)
            }
            createAnimalDialog.show(activity.supportFragmentManager,
                    CreateAnimalDialogFragment::class.java.simpleName)
        } else {
            val createAnimalDialog = CreateAnimalDialogFragment()
            createAnimalDialog.setTargetFragment(this, RESULT_CREATE_ANIMAL)
            createAnimalDialog.show(activity.supportFragmentManager,
                    CreateAnimalDialogFragment::class.java.simpleName)
        }
    }

    override fun confirmSelectedCollections() {
        selectionViewModel.selectAnimals(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Animal>())
    }

    private fun getCategoryAndSetList(recognizedCategory: String) {
        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ categories ->
                            categories.stream().filter { category ->
                                category.name.equals(recognizedCategory, ignoreCase = true)
                            }.findAny().ifPresent { category ->
                                this.category = category
                                getAnimalsByCategoryAndSetList(category.id)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all categories: $it")
                            it.printStackTrace()
                        })
        )
    }

    private fun getAnimalsByCategoryAndSetList(categoryId: String) {
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
}