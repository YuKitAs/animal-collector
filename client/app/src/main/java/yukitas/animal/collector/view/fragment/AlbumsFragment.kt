package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.databinding.FragmentAlbumsBinding
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AlbumsAdapter
import yukitas.animal.collector.viewmodel.AlbumViewModel
import java.util.*
import java.util.stream.Collectors


class AlbumsFragment : Fragment() {
    private val TAG = AlbumsFragment::class.java.simpleName

    private lateinit var binding: FragmentAlbumsBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumsAdapter: AlbumsAdapter
    private val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_albums, container, false)
        albumsAdapter = AlbumsAdapter(context)
        binding.gridAlbums.adapter = albumsAdapter

        albumViewModel = ViewModelProviders.of(this).get(AlbumViewModel::class.java)

        setAlbums()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setAlbums() {
        disposable.add(
                ApiService.create().getAlbumsByCategory(arguments.getString(ARG_CATEGORY_ID)!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setThumbnails))

        setAlbumListener()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("CheckResult")
    private fun setThumbnails(albums: List<Album>) {
        val albumThumbnailMaps: List<Single<Map<String, Photo?>>> = albums.stream().map { album ->
            ApiService.create().getAlbumThumbnail(album.id).map { photo ->
                Collections.singletonMap(album.id, photo)
            }
        }.collect(Collectors.toList())

        Single.zip(albumThumbnailMaps.asIterable()) { obj -> obj }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var albumThumbnailMap: Map<String, Photo?> = emptyMap()
                    it.forEach { albumThumbnail ->
                        albumThumbnailMap = albumThumbnailMap.plus(
                                albumThumbnail as Map<String, Photo?>)
                    }

                    albums.forEach { album -> album.thumbnail = albumThumbnailMap[album.id] }
                    albumsAdapter.albums = albums
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAlbumListener() {
        binding.gridAlbums.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(activity, PhotoActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ARG_ALBUM_ID, albumsAdapter.albums[position].id)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}