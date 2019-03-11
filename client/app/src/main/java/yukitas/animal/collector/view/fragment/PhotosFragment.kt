package yukitas.animal.collector.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.view.adapter.PhotosAdapter
import yukitas.animal.collector.viewmodel.PhotoViewModel

class PhotosFragment : Fragment() {
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var photosAdapter: PhotosAdapter
    lateinit var viewMode: ViewMode

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_photos, container, false)
        val gridView = view.findViewById<GridView>(R.id.grid_photos)
        photosAdapter = PhotosAdapter(context)
        gridView.adapter = photosAdapter

        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        when (viewMode) {
            ViewMode.ALBUM -> {
                val albumId = activity.intent!!.extras!!.getString(ARG_ALBUM_ID)!!
                photoViewModel.getPhotosByAlbum(albumId).observe(this, Observer { photos ->
                    photos?.let {
                        photosAdapter.photos = it
                    }
                })
            }
            ViewMode.ANIMAL -> {
                val animalId = activity.intent!!.extras!!.getString(ARG_ANIMAL_ID)!!
                photoViewModel.getPhotosByAnimal(animalId).observe(this, Observer { photos ->
                    photos?.let {
                        photosAdapter.photos = it
                    }
                })
            }
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString(ARG_PHOTO_ID, photosAdapter.photos[position].id)
            activity.intent.putExtras(bundle)

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PhotoDetailFragment())
                    .addToBackStack("photos")
                    .commit()
        }

        return view
    }
}