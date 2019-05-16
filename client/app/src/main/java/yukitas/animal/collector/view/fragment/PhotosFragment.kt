package yukitas.animal.collector.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.PhotosAdapter

abstract class PhotosFragment : Fragment() {
    lateinit var binding: yukitas.animal.collector.databinding.FragmentPhotosBinding
    lateinit var photosAdapter: PhotosAdapter
    lateinit var imageView: ImageView // FIXME only for test of photo display
    val RESULT_LOAD_IMAGE = 1

    val apiService by lazy { ApiService.create() }
    val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
                yukitas.animal.collector.R.layout.fragment_photos, container, false)
        val gridView = binding.gridPhotos
        photosAdapter = PhotosAdapter(context)
        gridView.adapter = photosAdapter

        setPhotos()

        imageView = binding.imageViewPhoto
        setAddPhotoButtonListener()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString(ARG_PHOTO_ID, photosAdapter.photos[position].id)
            activity.intent.putExtras(bundle)

            activity.supportFragmentManager.beginTransaction()
                    .replace(yukitas.animal.collector.R.id.fragment_container,
                            PhotoDetailFragment())
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

    private fun setAddPhotoButtonListener() {
        binding.btnAddPhoto.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE), RESULT_LOAD_IMAGE)
                } else {
                    startActivityForResult(Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == RESULT_LOAD_IMAGE) {
            startActivityForResult(Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE)
        } else {
            Toast.makeText(activity, "Permission denied to read your external storage",
                    Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun setPhotos()

    abstract fun setEditButtonListener()

    abstract fun setDeleteButtonListener()
}