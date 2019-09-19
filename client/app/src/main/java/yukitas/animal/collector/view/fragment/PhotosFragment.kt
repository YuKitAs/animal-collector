package yukitas.animal.collector.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.ARG_PHOTO_ID
import yukitas.animal.collector.common.Constants.RESULT_LOAD_IMAGE
import yukitas.animal.collector.view.adapter.PhotosAdapter

/**
 *  Photos in a selected album or animal
 */
abstract class PhotosFragment : AddPhotoBaseFragment() {
    private val TAG = PhotosFragment::class.java.simpleName

    protected val THUMBNAIL_SIDE_LENGTH = 400

    protected lateinit var binding: yukitas.animal.collector.databinding.FragmentPhotosBinding
    protected lateinit var photosAdapter: PhotosAdapter

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
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_photos, container, false)
        val gridView = binding.gridPhotos
        photosAdapter = PhotosAdapter(context)
        gridView.adapter = photosAdapter

        progressSpinner = binding.layoutProgress

        if (!shouldUpdateOnResume) {
            setPhotos()
        }

        setAddPhotoButtonListener()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString(ARG_PHOTO_ID, photosAdapter.photos[position].id)
            activity.intent.putExtras(bundle)

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_photos_container,
                            PhotoDetailFragment())
                    .addToBackStack("photos")
                    .commit()
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (shouldUpdateOnResume) {
            Log.v(TAG, "Updating photos onResume")
            setPhotos()
        } else {
            shouldUpdateOnResume = true
        }
    }

    override fun setAddPhotoButtonListener() {
        binding.btnAddPhoto.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            RESULT_LOAD_IMAGE)
                } else {
                    startActivityForResult(Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            RESULT_LOAD_IMAGE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                editCollection()
                true
            }
            R.id.action_delete -> {
                deleteCollection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected abstract fun editCollection()

    protected abstract fun deleteCollection()

    protected abstract fun setPhotos()
}