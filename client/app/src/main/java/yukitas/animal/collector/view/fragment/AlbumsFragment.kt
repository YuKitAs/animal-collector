package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_NAME
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.common.Constants.Companion.RESULT_CREATE_ALBUM
import yukitas.animal.collector.databinding.FragmentAlbumsBinding
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AlbumsAdapter
import yukitas.animal.collector.view.fragment.dialog.CreateAlbumDialogFragment
import java.util.*
import java.util.stream.Collectors

class AlbumsFragment : CollectionFragment() {
    private val TAG = AlbumsFragment::class.java.simpleName

    private lateinit var binding: FragmentAlbumsBinding
    private lateinit var albumsAdapter: AlbumsAdapter

    private var isActionButtonOpen = false
    private var shouldUpdateOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shouldUpdateOnResume = false
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_albums, container, false)
        albumsAdapter = AlbumsAdapter(context)
        binding.gridAlbums.adapter = albumsAdapter

        if (!shouldUpdateOnResume) {
            setAlbums()
        }

        resetActionMenu()
        setAddButtonListener()
        setActionButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (shouldUpdateOnResume) {
            Log.v(TAG, "Updating albums onResume")
            setAlbums()
        } else {
            shouldUpdateOnResume = true
        }

        resetActionMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ALBUM && resultCode == Activity.RESULT_OK) {
            onResume()
        }
    }

    private fun setAlbums() {
        val categoryId = arguments.getString(ARG_CATEGORY_ID)!!

        Log.d(TAG, "Retrieving albums for category $categoryId")

        disposable.add(
                apiService.getAlbumsByCategory(categoryId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setThumbnails))

        setAlbumListener()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("CheckResult")
    private fun setThumbnails(albums: List<Album>) {
        if (albums.isEmpty()) {
            albumsAdapter.albums = albums
            return
        }

        val albumThumbnailMaps: List<Maybe<Map<String, Photo?>>> = albums.stream().map { album ->
            apiService.getAlbumThumbnail(album.id, THUMBNAIL_SIDE_LENGTH,
                    THUMBNAIL_SIDE_LENGTH).map { photo ->
                Collections.singletonMap(album.id, photo)
            }.defaultIfEmpty(Collections.singletonMap(album.id, null as Photo?))
        }.collect(Collectors.toList())

        Maybe.zip(albumThumbnailMaps.asIterable()) { obj -> obj }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var albumThumbnailMap: Map<String, Photo?> = emptyMap()
                    it.forEach { albumThumbnail ->
                        albumThumbnailMap = albumThumbnailMap.plus(
                                albumThumbnail as Map<String, Photo?>)
                    }
                    albums.forEach { album ->
                        album.thumbnail = albumThumbnailMap[album.id]
                        Log.d(TAG,
                                "Set thumbnail ${albumThumbnailMap[album.id]} for album ${album.name}")
                    }
                    // update albums (sorted by last modified)
                    albumsAdapter.albums = albums.reversed()
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAlbumListener() {
        binding.gridAlbums.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            val album = albumsAdapter.albums[position]
            bundle.putString(ARG_ALBUM_ID, album.id)
            bundle.putString(ARG_ALBUM_NAME, album.name)

            val intent = Intent(activity, PhotoActivity::class.java).apply {
                putExtras(bundle)
            }
            activity.startActivity(intent)
        }
    }

    private fun setAddButtonListener() {
        binding.btnAddAlbum.setOnClickListener {
            val createAlbumDialog = CreateAlbumDialogFragment()
            createAlbumDialog.setTargetFragment(this, RESULT_CREATE_ALBUM)
            createAlbumDialog.show(activity.supportFragmentManager,
                    CreateAlbumDialogFragment::class.java.simpleName)
        }
    }


    private fun setActionButtonListener() {
        binding.btnAction.setOnClickListener {
            if (!isActionButtonOpen) {
                openActionMenu()
            } else {
                closeActionMenu()
            }
        }
    }

    private fun resetActionMenu() {
        binding.btnAction.visibility = View.VISIBLE
        closeActionMenu()
    }

    private fun closeActionMenu() {
        isActionButtonOpen = false

        binding.btnAddPhoto.animate().translationY(0f)
        binding.btnAddAlbum.animate().translationY(0f)

        binding.btnAddPhoto.visibility = View.INVISIBLE
        binding.btnAddAlbum.visibility = View.INVISIBLE
    }

    private fun openActionMenu() {
        isActionButtonOpen = true

        binding.btnAddPhoto.visibility = View.VISIBLE
        binding.btnAddAlbum.visibility = View.VISIBLE

        binding.btnAddPhoto.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        binding.btnAddAlbum.animate().translationY(
                -resources.getDimension(R.dimen.standard_130))
    }
}