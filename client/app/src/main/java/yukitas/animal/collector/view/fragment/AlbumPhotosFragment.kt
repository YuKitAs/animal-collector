package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.PHOTO_THUMBNAIL_SIDE_LENGTH
import yukitas.animal.collector.common.Constants.RESULT_EDIT_ALBUM
import yukitas.animal.collector.view.activity.EditPhotoActivity
import yukitas.animal.collector.view.fragment.dialog.EditAlbumDialogFragment

class AlbumPhotosFragment : PhotosFragment() {
    private val TAG = AlbumPhotosFragment::class.java.simpleName

    private lateinit var albumId: String
    private lateinit var albumName: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        albumId = activity.intent.getStringExtra(Constants.ARG_ALBUM_ID)!!
        albumName = activity.intent.getStringExtra(Constants.ARG_ALBUM_NAME)!!

        Log.d(TAG, "Selected album: $albumId")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textCollectionName.text = albumName.toUpperCase()
    }

    override fun onResume() {
        super.onResume()

        updateAlbum()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_EDIT_ALBUM && resultCode == Activity.RESULT_OK) {
            onResume()
        }
    }

    override fun setPhotos() {
        disposable.add(
                apiService.getPhotosByAlbum(albumId, PHOTO_THUMBNAIL_SIDE_LENGTH, PHOTO_THUMBNAIL_SIDE_LENGTH)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            photosAdapter.photos = it
                        }, {
                            Log.e(TAG,
                                    "Cannot get photos by album '$albumId'. Some errors occurred: $it")
                            it.printStackTrace()
                        }))
    }

    override fun startEditPhotoActivity(photoId: String, recognitionEnabled: Boolean,
                                        recognizedCategory: String?) {
        val bundle = Bundle().apply {
            putString(Constants.ARG_PHOTO_ID, photoId)
            putString(Constants.ARG_ALBUM_ID, albumId)
            putBoolean(Constants.FLAG_RECOGNITION_ENABLED, recognitionEnabled)

            if (!recognizedCategory.isNullOrBlank()) {
                putString(Constants.ARG_CATEGORY_NAME, recognizedCategory)
            }
        }

        val intent = Intent(activity, EditPhotoActivity::class.java).apply {
            putExtras(bundle)
        }
        activity.startActivity(intent)
    }

    override fun editCollection() {
        val editAlbumDialog = EditAlbumDialogFragment()
        editAlbumDialog.setTargetFragment(this, RESULT_EDIT_ALBUM)
        editAlbumDialog.show(activity.supportFragmentManager,
                EditAlbumDialogFragment::class.java.simpleName)
    }

    override fun deleteCollection() {
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle(String.format(getString(R.string.message_delete_confirm), "album"))
            setPositiveButton(R.string.btn_confirm_positive
            ) { _, _ ->
                Log.d(TAG, "Deleting album '$albumId'")

                disposable.add(
                        apiService.deleteAlbum(albumId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Log.d(TAG, "Deleted album '$albumId'")
                                    Toast.makeText(activity,
                                            getString(R.string.message_delete_album_success),
                                            Toast.LENGTH_SHORT).show()
                                    activity.onBackPressed()
                                }, {
                                    Log.e(TAG,
                                            "Cannot delete album '$albumId'. Some errors occurred: $it")
                                    it.printStackTrace()

                                    Toast.makeText(activity,
                                            getString(R.string.message_server_error),
                                            Toast.LENGTH_SHORT).show()
                                }))
            }
            setNegativeButton(R.string.btn_confirm_negative
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.show()
    }

    private fun updateAlbum() {
        disposable.add(apiService.getAlbumById(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    albumName = it.name

                    textCollectionName.text = albumName.toUpperCase()
                }, {
                    Log.e(TAG, "Cannot get album '$albumId'. Some errors occurred: $it")
                    it.printStackTrace()
                }))
    }
}
