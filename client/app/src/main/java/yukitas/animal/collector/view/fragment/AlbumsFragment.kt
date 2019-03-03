package yukitas.animal.collector.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yukitas.animal.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AlbumsAdapter
import yukitas.animal.collector.viewmodel.AlbumViewModel

class AlbumsFragment : Fragment() {
    private lateinit var binding: yukitas.animal.databinding.FragmentAlbumsBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumsAdapter: AlbumsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_albums, container, false)
        albumsAdapter = AlbumsAdapter(context)
        binding.gridAlbums.adapter = albumsAdapter

        albumViewModel = ViewModelProviders.of(this).get(AlbumViewModel::class.java)
        albumViewModel.getAlbumsByCategory(arguments.getString(ARG_CATEGORY_ID)!!).observe(this, Observer { albums ->
            albums?.let {
                albumsAdapter.albums = it
                binding.gridAlbums.setOnItemClickListener { _, _, position, _ ->
                    val intent = Intent(activity, PhotoActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(ARG_ALBUM_ID, albumsAdapter.albums[position].id)
                    intent.putExtras(bundle)
                    activity.startActivity(intent)
                }
            }
        })

        return binding.root
    }
}