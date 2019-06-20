package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_album.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.model.dto.SaveAlbumRequest
import yukitas.animal.collector.networking.ApiService

/**
 * Create or update an Album
 */
class EditAlbumActivity : AppCompatActivity() {
    private val TAG = EditAlbumActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_album)
        setSupportActionBar(toolbar)

        val isCreating = intent.getBooleanExtra("isCreating", true)
        if (isCreating) {
            labelEditAlbum.text = getString(R.string.label_create_album)

            btnSaveAlbum.setOnClickListener {
                Log.d(TAG, "Creating album with name '${inputAlbumName.text}'")

                disposable.add(
                        apiService.createAlbum(intent.getStringExtra(Constants.ARG_CATEGORY_ID),
                                SaveAlbumRequest(inputAlbumName.text.toString()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { album ->
                                    Log.d(TAG, "Created album: $album")
                                    // return to MainActivity
                                    finish()
                                })
            }
        } else {
            labelEditAlbum.text = getString(R.string.label_update_album)

            btnSaveAlbum.setOnClickListener {
                Log.d(TAG, "Updating album with name '${inputAlbumName.text}'")

                disposable.add(
                        apiService.updateAlbum(intent.getStringExtra(ARG_ALBUM_ID),
                                SaveAlbumRequest(inputAlbumName.text.toString()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    Log.d(TAG, "Updated album")
                                    // return to MainActivity
                                    finish()
                                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}