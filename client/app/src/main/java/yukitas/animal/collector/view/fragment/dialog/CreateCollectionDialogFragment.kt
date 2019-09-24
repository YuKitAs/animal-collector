package yukitas.animal.collector.view.fragment.dialog

import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.*
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.CategoryArrayAdapter
import java.util.stream.Collectors

abstract class CreateCollectionDialogFragment : DialogFragment() {
    private val TAG = CreateCollectionDialogFragment::class.java.simpleName

    protected lateinit var categoryId: String

    protected val apiService by lazy { ApiService.create(Constants.BASE_URL) }
    protected val disposable = CompositeDisposable()

    protected fun setCategoryList() {
        val defaultCategoryId = arguments?.getString(Constants.ARG_CATEGORY_ID)

        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ categories ->
                            Log.d(TAG,
                                    "Fetched categories: ${categories.stream().map { category -> category.name }.collect(
                                            Collectors.toList())}")

                            val categoryList = dialog.dropdownCategory as Spinner
                            val categoryAdapter = CategoryArrayAdapter(activity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    android.R.id.text1,
                                    ArrayList(categories))
                            categoryList.adapter = categoryAdapter

                            for (i in 0 until categoryAdapter.count) {
                                if (categoryAdapter.getItem(i).id == defaultCategoryId) {
                                    Log.d(TAG,
                                            "Default category: ${categoryAdapter.getItem(i).name}")
                                    categoryList.setSelection(i)
                                }
                            }

                            categoryList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                            Log.e(TAG, "Cannot get all categories. Some errors occurred: $it")
                            it.printStackTrace()
                        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}