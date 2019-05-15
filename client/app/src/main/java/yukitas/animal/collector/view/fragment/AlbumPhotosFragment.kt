package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.view.activity.EditAlbumActivity

class AlbumPhotosFragment : PhotosFragment() {
    private val TAG = AlbumPhotosFragment::class.java.simpleName
    private lateinit var album: Album

    override fun setPhotos() {
        val albumId = activity.intent!!.extras!!.getString(ARG_ALBUM_ID)!!

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

        disposable.add(
                apiService.getPhotosByAlbum(albumId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            photosAdapter.photos = it
                        })
    }

    override fun setEditButtonListener() {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCreating", false)
            bundle.putString(ARG_ALBUM_ID, album.id)
            bundle.putString(Constants.ARG_ALBUM_NAME, album.name)

            val intent = Intent(activity, EditAlbumActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    override fun setDeleteButtonListener() {
        binding.btnDeleteCollection.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.apply {
                setMessage("Are you sure you want to delete this album?")
                setPositiveButton(R.string.label_confirm_positive
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
                setNegativeButton(R.string.label_confirm_negative
                ) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.show()
        }
    }
}