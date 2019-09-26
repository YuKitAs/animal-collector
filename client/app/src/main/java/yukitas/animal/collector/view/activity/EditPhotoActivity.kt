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
import yukitas.animal.collector.common.Constants.ARG_CATEGORY_NAME
import yukitas.animal.collector.common.Constants.BASE_URL
import yukitas.animal.collector.common.Constants.FLAG_CATEGORY_CONFIRMED
import yukitas.animal.collector.common.Constants.FLAG_RECOGNITION_ENABLED
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.fragment.EditPhotoFragment

private const val CATEGORY_UNKNOWN = "unknown"

/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create(BASE_URL) }
    private val disposable = CompositeDisposable()

    private var isCreating = true

    private val argumentsMap = HashMap<String, Boolean>()
    private var recognizedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            isCreating = intent.getBooleanExtra(Constants.FLAG_IS_CREATING, true)
            if (isCreating) {
                if (intent.getBooleanExtra(FLAG_RECOGNITION_ENABLED, false)) {
                    argumentsMap[FLAG_RECOGNITION_ENABLED] = true
                    checkRecognizedCategory()
                } else {
                    argumentsMap[FLAG_RECOGNITION_ENABLED] = false
                    attachEditPhotoFragment()
                }
            } else {
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

    private fun checkRecognizedCategory() {
        recognizedCategory = intent.getStringExtra(ARG_CATEGORY_NAME)

        if (recognizedCategory.isNullOrBlank()) {
            // this should not happen!
            Log.w(TAG, "Enabled recognition but no category has been returned!")
            attachEditPhotoFragment()
            return
        }

        if (recognizedCategory.equals(CATEGORY_UNKNOWN, ignoreCase = true)) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage(R.string.message_recognized_category_unknown)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    attachEditPhotoFragment()
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
                    argumentsMap[FLAG_CATEGORY_CONFIRMED] = true
                    attachEditPhotoFragment()
                }
                setNegativeButton(R.string.btn_confirm_negative_no) { _, _ ->
                    argumentsMap[FLAG_CATEGORY_CONFIRMED] = false
                    attachEditPhotoFragment()
                }
            }
            builder.show()
        }
    }

    private fun attachEditPhotoFragment() {
        val fragment = EditPhotoFragment()
        val bundle = Bundle()

        argumentsMap.forEach { (key, value) ->
            bundle.putBoolean(key, value)
        }

        recognizedCategory?.let {
            bundle.putString(ARG_CATEGORY_NAME, it)
        }

        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container, fragment)
                .commit()
    }
}