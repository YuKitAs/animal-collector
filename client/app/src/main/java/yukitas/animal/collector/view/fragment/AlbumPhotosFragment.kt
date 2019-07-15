package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.view.activity.EditAlbumActivity
import yukitas.animal.collector.view.activity.EditPhotoActivity

class AlbumPhotosFragment : PhotosFragment() {
    private val TAG = AlbumPhotosFragment::class.java.simpleName

    private lateinit var album: Album

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        albumId = activity.intent!!.extras!!.getString(Constants.ARG_ALBUM_ID)!!
        Log.d(TAG, "Selected album: $albumId")

        disposable.add(apiService.getAlbumById(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    album = it

                    textCollectionName.text = it.name.toUpperCase()

                    setEditButtonListener()
                    setDeleteButtonListener()
                }, {
                    Log.e(TAG, "Some errors occurred: $it")
                }))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setPhotos() {
        disposable.add(
                apiService.getPhotosByAlbum(albumId, THUMBNAIL_SIDE_LENGTH, THUMBNAIL_SIDE_LENGTH)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            photosAdapter.photos = it
                        })
    }

    override fun startEditPhotoActivity(photoId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.ARG_PHOTO_ID, photoId)
        bundle.putString(Constants.ARG_ALBUM_ID, albumId)

        val intent = Intent(activity, EditPhotoActivity::class.java).apply {
            putExtras(bundle)
        }
        activity.startActivity(intent)
    }

    override fun setEditButtonListener() {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.ARG_IS_CREATING, false)
            bundle.putString(Constants.ARG_ALBUM_ID, album.id)
            bundle.putString(Constants.ARG_ALBUM_NAME, album.name)

            val intent = Intent(activity, EditAlbumActivity::class.java).apply {
                putExtras(bundle)
            }

            activity.startActivity(intent)
        }
    }

    override fun setDeleteButtonListener() {
        binding.btnDeleteCollection.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.apply {
                setTitle(String.format(getString(R.string.message_delete_confirm), "album"))
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    val albumId = album.id
                    Log.d(TAG, "Deleting album '$albumId'")

                    disposable.add(
                            apiService.deleteAlbum(albumId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Log.d(TAG, "Deleted album '$albumId'")
                                        activity.onBackPressed()
                                    })
                }
                setNegativeButton(R.string.btn_confirm_negative
                ) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.show()
        }
    }
}
