package yukitas.animal.collector.view.fragment.dialog

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.*
import kotlinx.android.synthetic.main.dialog_create_album.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAlbumRequest

class CreateAlbumDialogFragment : CreateCollectionDialogFragment() {
    private val TAG = CreateAlbumDialogFragment::class.java.simpleName

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
                            .subscribe({ album ->
                                Log.d(TAG, "Created album: $album")

                                dialog.dismiss()

                                activity.intent.putExtra(Constants.ARG_ALBUM_ID, album.id)
                                targetFragment.onActivityResult(targetRequestCode,
                                        Activity.RESULT_OK, activity.intent)
                            }, {
                                Log.e(TAG, "Cannot create album. Some errors occurred: $it")
                                it.printStackTrace()

                                Toast.makeText(activity,
                                        getString(R.string.message_server_error),
                                        Toast.LENGTH_SHORT).show()
                            }))
        }

        view.btnCloseDialog.setOnClickListener { dialog.dismiss() }

        return view
    }
}