package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.*
import kotlinx.android.synthetic.main.dialog_create_album.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAlbumRequest
import yukitas.animal.collector.networking.ApiService

class CreateAlbumDialogFragment : CreateCollectionDialogFragment() {
    private val TAG = CreateAlbumDialogFragment::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_album, container,
                false)

        setCategoryList()

        view.btnSaveAlbum.setOnClickListener {
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

                                dialog.dismiss()

                                targetFragment.onActivityResult(targetRequestCode,
                                        Activity.RESULT_OK, activity.intent)
                            })
        }

        view.btnCloseDialog.setOnClickListener { dialog.dismiss() }

        return view
    }
}