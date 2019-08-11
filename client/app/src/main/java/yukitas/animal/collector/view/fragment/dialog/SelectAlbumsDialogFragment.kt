package yukitas.animal.collector.view.fragment.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_select_collection.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.RESULT_CREATE_ALBUM
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter

/**
 * Select from all albums
 */
class SelectAlbumsDialogFragment : SelectCollectionDialogFragment() {
    private val TAG = SelectAlbumsDialogFragment::class.java.simpleName

    // all albums
    private var albums: List<Album> = emptyList()

    private var newAlbumId: String? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        labelSelectCollection.text = getString(R.string.label_select_albums)
        labelAddCollection.text = getString(R.string.label_add_album)

        setList()

        layoutAddCollection.setOnClickListener {
            createNewCollection()
        }

        btnSaveSelection.setOnClickListener {
            confirmSelectedCollections()
            dialog.dismiss()
        }

        btnCloseDialog.setOnClickListener { dialog.dismiss() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ALBUM && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                newAlbumId = data.getStringExtra(Constants.ARG_ALBUM_ID)
            }

            // update list with new album
            setList()
        }
    }

    override fun setList() {
        disposable.add(
                apiService.getAllAlbums()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ albums ->
                            this.albums = albums

                            val sortedAlbums = albums.sortedBy { it.category.name }.sortedBy { it.name }

                            val multiSelectAlbumList = multiSelectListCollection as ListView
                            multiSelectAlbumList.adapter = CollectionArrayAdapter(activity,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1,
                                    ArrayList(sortedAlbums))

                            val selectedAlbumIds = selectionViewModel.selectedAlbumIds
                            if (!selectedAlbumIds.isNullOrEmpty()) {
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
        selectionViewModel.selectAlbums(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Album>())

        val createAlbumDialog = CreateAlbumDialogFragment()
        createAlbumDialog.setTargetFragment(this, RESULT_CREATE_ALBUM)
        createAlbumDialog.show(activity.supportFragmentManager,
                CreateAlbumDialogFragment::class.java.simpleName)
    }

    override fun confirmSelectedCollections() {
        selectionViewModel.selectAlbums(getSelectedCollections(
                (multiSelectListCollection as ListView)).filterIsInstance<Album>())
    }
}