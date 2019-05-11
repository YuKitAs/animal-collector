package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_TAGS
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.activity.EditAlbumActivity
import yukitas.animal.collector.view.activity.EditAnimalActivity
import yukitas.animal.collector.view.adapter.PhotosAdapter
import java.util.*

class PhotosFragment : Fragment() {
    private val TAG = PhotosFragment::class.java.simpleName

    private lateinit var binding: yukitas.animal.collector.databinding.FragmentPhotosBinding
    private lateinit var photosAdapter: PhotosAdapter
    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

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

    private fun setPhotos() {
        when (AnimalCollectorApplication.currentViewMode) {
            ViewMode.ALBUM -> {
                val albumId = activity.intent!!.extras!!.getString(ARG_ALBUM_ID)!!

                disposable.add(apiService.getAlbumById(albumId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            textCollectionName.text = it.name.toUpperCase()
                            setEditAlbumButtonListener(it)
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))

                disposable.add(
                        apiService.getPhotosByAlbum(albumId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    photosAdapter.photos = it
                                })
            }
            ViewMode.ANIMAL -> {
                val animalId = activity.intent!!.extras!!.getString(ARG_ANIMAL_ID)!!

                disposable.add(apiService.getAnimalById(animalId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            textCollectionName.text = it.name.toUpperCase()
                            setEditAnimalButtonListenr(it)
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))

                disposable.add(
                        apiService.getPhotosByAnimal(animalId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    photosAdapter.photos = it
                                })
            }
        }
    }

    private fun setEditAlbumButtonListener(album: Album) {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCreating", false)
            bundle.putString(ARG_ALBUM_ID, album.id)
            bundle.putString(ARG_ALBUM_NAME, album.name)

            val intent = Intent(activity, EditAlbumActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    private fun setEditAnimalButtonListenr(animal: Animal) {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCreating", false)
            bundle.putString(ARG_ANIMAL_ID, animal.id)
            bundle.putString(ARG_ANIMAL_NAME, animal.name)
            bundle.putStringArrayList(ARG_ANIMAL_TAGS, ArrayList(animal.tags))

            val intent = Intent(activity, EditAnimalActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }
}