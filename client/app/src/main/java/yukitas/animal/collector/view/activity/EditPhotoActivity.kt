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
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_NAME
import yukitas.animal.collector.common.Constants.Companion.CATEGORY_UNKNOWN
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.fragment.EditPhotoMainFragment


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
            attachFragment(savedInstanceState)
        } else {
            if (savedInstanceState == null) {
                attachEditPhotoFragment(null)
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

    private fun attachFragment(savedInstanceState: Bundle?) {
        val recognizedCategory = intent.getStringExtra(ARG_CATEGORY_NAME)

        if (recognizedCategory.isNullOrBlank()) {
            if (savedInstanceState == null) {
                attachEditPhotoFragment(null)
            }
            return
        }

        if (recognizedCategory == CATEGORY_UNKNOWN) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage(R.string.message_recognized_category_unknown)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    if (savedInstanceState == null) {
                        attachEditPhotoFragment(null)
                    }
                }
            }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle(String.format(getString(R.string.message_recognized_category_confirm),
                        recognizedCategory))
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    if (savedInstanceState == null) {
                        attachEditPhotoFragment(recognizedCategory)
                    }
                }
                setNegativeButton(R.string.btn_confirm_negative_no) { _, _ ->
                    if (savedInstanceState == null) {
                        attachEditPhotoFragment(null)
                    }
                }
            }
            builder.show()
        }
    }

    private fun attachEditPhotoFragment(recognizedCategory: String?) {
        val fragment = EditPhotoMainFragment()
        recognizedCategory?.let {
            fragment.arguments = Bundle().apply {
                putString(ARG_CATEGORY_NAME, recognizedCategory)
            }
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container, fragment)
                .commit()
    }
}