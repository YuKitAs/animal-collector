package yukitas.animal.collector.view.fragment

import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.*
import yukitas.animal.collector.networking.ApiService
import java.util.stream.Collectors

abstract class CreateCollectionDialogFragment : DialogFragment() {
    private val TAG = CreateCollectionDialogFragment::class.java.simpleName

    protected lateinit var categoryId: String

    protected val apiService by lazy { ApiService.create() }
    protected val disposable = CompositeDisposable()

    protected fun setCategoryList() {
        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ categories ->
                            dialog.dropdownCategory.adapter = ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    categories.stream().map { category -> category.name.capitalize() }.collect(
                                            Collectors.toList()).toTypedArray())

                            dialog.dropdownCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                            Log.e(TAG, "Some errors occurred while fetching all categories: $it")
                        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}