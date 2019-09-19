package yukitas.animal.collector.view.fragment.dialog

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_album.*
import kotlinx.android.synthetic.main.dialog_create_album.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAlbumRequest
import yukitas.animal.collector.networking.ApiService

class EditAlbumDialogFragment : DialogFragment() {
    private val TAG = EditAlbumDialogFragment::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_album, container,
                false)

        view.inputAlbumName.setText(activity.intent.getStringExtra(Constants.ARG_ALBUM_NAME))

        view.btnSaveAlbum.setOnClickListener {
            Log.d(TAG, "Updating album with name '${inputAlbumName.text}'")

            disposable.add(
                    apiService.updateAlbum(activity.intent.getStringExtra(Constants.ARG_ALBUM_ID),
                            SaveAlbumRequest(inputAlbumName.text.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d(TAG, "Updated album")
                                Toast.makeText(context,
                                        getString(R.string.message_update_album_success),
                                        Toast.LENGTH_SHORT).show()

                                dialog.dismiss()

                                targetFragment.onActivityResult(targetRequestCode,
                                        Activity.RESULT_OK, activity.intent)
                            }, {
                                Log.e(TAG, "Cannot update album. Some errors occurred: $it")
                                it.printStackTrace()

                                Toast.makeText(activity,
                                        getString(R.string.message_server_error),
                                        Toast.LENGTH_SHORT).show()
                            }))
        }

        view.btnCloseDialog.setOnClickListener { dialog.dismiss() }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}