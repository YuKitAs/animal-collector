package yukitas.animal.collector.view.activity

import android.os.Bundle
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


/**
 * Create or update a Photo
 */
class EditPhotoActivity : AppCompatActivity() {
    private val TAG = EditPhotoActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_edit_photo_container, EditPhotoMainFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val photoId = intent.getStringExtra(Constants.ARG_PHOTO_ID)

        if (intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)) {
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
}