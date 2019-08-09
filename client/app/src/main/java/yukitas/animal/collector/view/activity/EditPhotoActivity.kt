package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.fragment.EditPhotoMainFragment
import yukitas.animal.collector.view.fragment.SelectAnimalFragment


/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    private var isCreating = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        isCreating = intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)
        if (isCreating) {
            showDialog(savedInstanceState)
        } else {
            if (savedInstanceState == null) {
                attachEditPhotoFragment()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val photoId = intent.getStringExtra(Constants.ARG_PHOTO_ID)

        if (isCreating) {
            disposable.add(apiService.deletePhoto(photoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d(TAG, "Deleted unsaved photo '$photoId'")
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun showDialog(savedInstanceState: Bundle?) {
        val detectedCategory = intent.getStringExtra(Constants.ARG_DETECTED_CATEGORY)
        if (detectedCategory.isNullOrBlank() || detectedCategory == "UNKNOWN") {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage(R.string.message_detected_category_unknown)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    if (savedInstanceState == null) {
                        attachEditPhotoFragment()
                    }
                }
            }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage(String.format(getString(R.string.message_detected_category_confirm),
                        detectedCategory))
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    if (savedInstanceState == null) {
                        val fragment = SelectAnimalFragment()
                        fragment.arguments = Bundle().apply {
                            putString(Constants.ARG_DETECTED_CATEGORY, detectedCategory)
                        }
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_edit_photo_container, fragment)
                                .commit()
                    }
                }
                setNegativeButton(R.string.btn_confirm_negative_no) { _, _ ->
                    if (savedInstanceState == null) {
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_edit_photo_container, SelectAnimalFragment())
                                .commit()
                    }
                }
            }
            builder.show()
        }
    }

    private fun attachEditPhotoFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container,
                        EditPhotoMainFragment())
                .commit()
    }
}