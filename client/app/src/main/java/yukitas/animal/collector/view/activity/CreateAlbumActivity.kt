package yukitas.animal.collector.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.CreateAlbumRequest
import yukitas.animal.collector.networking.ApiService

class CreateAlbumActivity : AppCompatActivity() {
    private val TAG = CreateAlbumActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        setSupportActionBar(toolbar)

        btnCreateAlbum.setOnClickListener {
            Log.d(TAG, "Creating album with name '${inputAlbumName.text}'")

            disposable.add(
                    apiService.createAlbum(intent.getStringExtra(Constants.ARG_CATEGORY_ID),
                            CreateAlbumRequest(inputAlbumName.text.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { album -> Log.d(TAG, "Created album: $album") })

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}