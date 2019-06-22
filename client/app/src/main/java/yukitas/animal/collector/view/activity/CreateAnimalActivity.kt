package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_animal.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.model.dto.SaveAnimalRequest
import yukitas.animal.collector.networking.ApiService
import java.util.stream.Collectors

/**
 * Create an Animal for an arbitrary Category
 */
class CreateAnimalActivity : AppCompatActivity() {
    private val TAG = CreateAnimalActivity::class.java.simpleName
    private lateinit var categoryId: String

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_animal)
        setSupportActionBar(toolbar)

        setCategoriesDropdown()
        setSaveButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setCategoriesDropdown() {
        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ categories ->
                            dropdownCategory.adapter = ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    categories.stream().map { category -> category.name }.collect(
                                            Collectors.toList()).toTypedArray())

                            dropdownCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                                            position: Int, id: Long) {
                                    Log.d(TAG,
                                            "Selected: ${dropdownCategory.selectedItem}")
                                    val selectedCategory = categories[position]
                                    Log.d(TAG,
                                            "Selected category id: ${selectedCategory.id}")
                                    categoryId = selectedCategory.id
                                }
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while getting all categories: $it")
                        }))
    }

    private fun setSaveButtonListener() {
        btnSaveAnimal.setOnClickListener {
            Log.d(TAG,
                    "Creating animal for category '$categoryId' with name '${inputAnimalName.text}'")

            disposable.add(
                    apiService.createAnimal(categoryId,
                            SaveAnimalRequest(inputAnimalName.text.toString(),
                                    parseTagsFromText(inputAnimalTags.text)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { animal ->
                                Log.d(TAG, "Created animal: $animal")
                                finish()
                            })
        }
    }

    private fun parseTagsFromText(tags: Editable): List<String> {
        return tags.split("\\s+".toRegex()).map { it.trim() }
    }
}