package yukitas.animal.collector.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.PhotosAdapter

abstract class PhotosFragment : Fragment() {
    lateinit var binding: yukitas.animal.collector.databinding.FragmentPhotosBinding
    lateinit var photosAdapter: PhotosAdapter
    val apiService by lazy { ApiService.create() }
    val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photos, container, false)
        val gridView = binding.gridPhotos
        photosAdapter = PhotosAdapter(context)
        gridView.adapter = photosAdapter

        setPhotos()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString(ARG_PHOTO_ID, photosAdapter.photos[position].id)
            activity.intent.putExtras(bundle)

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PhotoDetailFragment())
                    .addToBackStack("photos")
                    .commit()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // refresh fragment
        setPhotos()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    abstract fun setPhotos()

    abstract fun setEditButtonListener()

    abstract fun setDeleteButtonListener()
}