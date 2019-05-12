package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.databinding.FragmentAlbumsBinding
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.activity.EditAlbumActivity
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AlbumsAdapter
import java.util.*
import java.util.stream.Collectors

class AlbumsFragment : Fragment() {
    private val TAG = AlbumsFragment::class.java.simpleName

    private lateinit var binding: FragmentAlbumsBinding
    private lateinit var albumsAdapter: AlbumsAdapter
    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_albums, container, false)
        albumsAdapter = AlbumsAdapter(context)
        binding.gridAlbums.adapter = albumsAdapter

        setAlbums()
        setAddButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setAlbums()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
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
        val albumThumbnailMaps: List<Maybe<Map<String, Photo?>>> = albums.stream().map { album ->
            apiService.getAlbumThumbnail(album.id).map { photo ->
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
                    // update albums
                    albumsAdapter.albums = albums
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAlbumListener() {
        binding.gridAlbums.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            val album = albumsAdapter.albums[position]
            bundle.putString(ARG_ALBUM_ID, album.id)
            bundle.putBoolean("isCreating", true)

            val intent = Intent(activity, PhotoActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    private fun setAddButtonListener() {
        binding.btnAddAlbum.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.ARG_CATEGORY_ID, arguments.getString(ARG_CATEGORY_ID))

            val intent = Intent(activity, EditAlbumActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }
}