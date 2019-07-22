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

class SelectAlbumsFragment : SelectCollectionFragment() {
    private val TAG = SelectAlbumsFragment::class.java.simpleName

    // all albums
    private var albums: List<Album> = emptyList()

    private var newAlbumId: String? = null

    private val RESULT_CREATE_ALBUM = 2

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCollection.text = getString(R.string.btn_new_album)

        setList()
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
                                Log.d(TAG, "Selected albums: $selectedAlbumIds")
                                selectItemsByCollectionIds(multiSelectAlbumList, selectedAlbumIds)
                            }

                            newAlbumId?.let {
                                selectItemByCollectionId(multiSelectAlbumList, it)
                            }
                        }, {
                            Log.e(TAG, "Some errors occurred while fetching all albums: $it")
                            it.printStackTrace()
                        }))
    }

    override fun createNewCollection() {
        // save currently selected album ids
        activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                getSelectedCollectionIds((multiSelectListCollection as ListView)))

        startActivityForResult(Intent(activity, CreateAlbumActivity::class.java),
                RESULT_CREATE_ALBUM)
    }

    override fun confirmSelectedCollections() {
        // save currently selected album ids
        activity.intent.putExtra(ARG_SELECTED_ALBUM_IDS,
                getSelectedCollectionIds((multiSelectListCollection as ListView)))

        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_edit_photo_container, EditPhotoMainFragment())
                .commit()
    }
}