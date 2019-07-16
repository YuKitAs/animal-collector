package yukitas.animal.collector.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAlbumRequest
import yukitas.animal.collector.networking.ApiService
import java.util.stream.Collectors

/**
 * Create an Album for an arbitrary Category
 */
class CreateAlbumActivity : AppCompatActivity() {
    private val TAG = CreateAlbumActivity::class.java.simpleName
    private lateinit var categoryId: String

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        setSupportActionBar(toolbar)

        setCategoryList()
        setSaveButtonListener()
        setCancelButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setCategoryList() {
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
                            Log.e(TAG, "Some errors occurred while fetching all categories: $it")
                        }))
    }

    private fun setSaveButtonListener() {
        btnSaveAlbum.setOnClickListener {
            Log.d(TAG,
                    "Creating album for category '$categoryId' with name '${inputAlbumName.text}'")

            disposable.add(
                    apiService.createAlbum(categoryId,
                            SaveAlbumRequest(inputAlbumName.text.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { album ->
                                Log.d(TAG, "Created album: $album")

                                val data = Intent().apply {
                                    putExtra(Constants.ARG_ALBUM_ID, album.id)
                                }
                                setResult(Activity.RESULT_OK, data)
                                finish()
                            })
        }
    }

    private fun setCancelButtonListener() {
        btnCancelAlbumCreation.setOnClickListener {
            finish()
        }
    }
}