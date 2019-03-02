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
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AlbumsAdapter
import yukitas.animal.collector.viewmodel.AlbumViewModel

private const val ARG_CATEGORY_ID = "categoryId"

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
        binding.gridAlbums.setOnItemClickListener { _, _, _, _ ->
            val intent = Intent(activity, PhotoActivity::class.java)
            activity.startActivity(intent)
        }

        albumViewModel = ViewModelProviders.of(this).get(AlbumViewModel::class.java)
        albumViewModel.getAlbumsByCategory(arguments.getString(ARG_CATEGORY_ID)!!).observe(this, Observer { albums ->
            albums?.let {
                albumsAdapter.albums = it
            }
        })

        return binding.root
    }
}