package yukitas.animal.collector.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import yukitas.animal.collector.AnimalCollectorApplication
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_NAME
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_NAME
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.PhotosAdapter

class PhotosFragment : Fragment() {
    private lateinit var photosAdapter: PhotosAdapter
    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_photos, container, false)
        val gridView = view.findViewById<GridView>(R.id.grid_photos)
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

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setPhotos() {
        when (AnimalCollectorApplication.currentViewMode) {
            ViewMode.ALBUM -> {
                disposable.add(
                        apiService.getPhotosByAlbum(
                                activity.intent!!.extras!!.getString(ARG_ALBUM_ID)!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    photosAdapter.photos = it
                                    textCollectionName.text = activity.intent!!.extras!!.getString(
                                            ARG_ALBUM_NAME)!!.toUpperCase()
                                })
            }
            ViewMode.ANIMAL -> {
                disposable.add(
                        apiService.getPhotosByAnimal(
                                activity.intent!!.extras!!.getString(ARG_ANIMAL_ID)!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    photosAdapter.photos = it
                                    textCollectionName.text = activity.intent!!.extras!!.getString(
                                            ARG_ANIMAL_NAME)!!.toUpperCase()
                                })
            }
        }
    }
}