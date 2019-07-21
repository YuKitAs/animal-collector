package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_select_collection.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_SELECTED_ALBUM_IDS
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.view.activity.CreateAlbumActivity
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter
import java.util.stream.Collectors

class SelectAlbumsFragment : SelectCollectionFragment() {
    private val TAG = SelectAlbumsFragment::class.java.simpleName

    // all albums
    private var albums: List<Album> = emptyList()
    // albums fetched by photo
    private var photoAlbums: List<Album> = emptyList()

    private var newAlbumId: String? = null

    private val RESULT_CREATE_ALBUM = 2

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCollection.text = getString(R.string.btn_new_album)

        if (!isCreating) {
            disposable.add(apiService.getAlbumsByPhoto(photoId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ albums ->
                        Log.d(TAG,
                                "Fetched albums for photo $photoId: ${albums.stream().map { album -> album.name }.collect(
                                        Collectors.toList())}")
                        photoAlbums = albums

                        // only set lists after all albums are fetched, in order to pre-select photo albums
                        setList()
                    }, {
                        Log.e(TAG,
                                "Some errors occurred when fetching albums of photo $photoId: $it")
                        it.printStackTrace()
                    }))
        } else {
            setList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ALBUM && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAlbumId = data.getStringExtra(Constants.ARG_ALBUM_ID)
            }
            setList()
        }
    }

    override fun setList() {
        val selectedAlbumIds = activity.intent.getStringArrayListExtra(
                ARG_SELECTED_ALBUM_IDS).orEmpty()

        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums

                            val multiSelectAlbumList = multiSelectListCollection as ListView
                            multiSelectAlbumList.adapter = CollectionArrayAdapter(activity,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    ArrayList(albums))

                            if (selectedAlbumIds.isNotEmpty()) {
                                Log.d(TAG, "Selecting selected albums: $selectedAlbumIds")
                                selectItemsByCollectionIds(multiSelectAlbumList,
                                        selectedAlbumIds.toSet())
                            } else {
                                if (isCreating) {
                                    selectCurrentAlbum(multiSelectAlbumList)
                                } else {
                                    selectPhotoAlbums(multiSelectAlbumList)
                                }
                            }

                            newAlbumId?.let {
                                selectItemByCollectionId(multiSelectAlbumList, it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all albums: $it")
                            it.printStackTrace()
                        }))
    }

    private fun selectCurrentAlbum(multiSelectList: ListView) {
        activity.intent.getStringExtra(Constants.ARG_ALBUM_ID)?.let {
            Log.d(TAG, "Current album: $it")
            selectItemByCollectionId(multiSelectList, it)
        }
    }

    private fun selectPhotoAlbums(multiSelectList: ListView) {
        selectItemsByCollectionIds(multiSelectList,
                photoAlbums.stream().map { album -> album.id }.collect(
                        Collectors.toSet()))
    }

    override fun createNewCollection() {
        // save currently selected items
        activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                getSelectedCollectionIds((multiSelectListCollection as ListView)))

        startActivityForResult(Intent(activity, CreateAlbumActivity::class.java),
                RESULT_CREATE_ALBUM)
    }

    override fun confirmSelectedCollections() {
        // save currently selected items
        activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                getSelectedCollectionIds((multiSelectListCollection as ListView)))

        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container, EditPhotoMainFragment())
                .commit()
    }
}