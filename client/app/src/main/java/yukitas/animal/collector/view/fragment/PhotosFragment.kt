package yukitas.animal.collector.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.view.adapter.PhotosAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 *  Photos in a selected album or animal
 */
abstract class PhotosFragment : BaseFragment() {
    private val TAG = PhotosFragment::class.java.simpleName

    private val RESULT_LOAD_IMAGE = 1
    private val MAX_SIDE_LENGTH = 1080
    protected val THUMBNAIL_SIDE_LENGTH = 400

    protected lateinit var binding: yukitas.animal.collector.databinding.FragmentPhotosBinding
    protected lateinit var photosAdapter: PhotosAdapter
    protected lateinit var albumId: String
    protected lateinit var animalId: String

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

        setPhotos()

        setAddPhotoButtonListener()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString(ARG_PHOTO_ID, photosAdapter.photos[position].id)
            activity.intent.putExtras(bundle)

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,
                            PhotoDetailFragment())
                    .addToBackStack("photos")
                    .commit()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // reset photos
        setPhotos()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            postPhoto(processPhoto(readPhotoFromStorage(data.data!!)))
        }
    }

    private fun readPhotoFromStorage(data: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(data, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val photoPath = cursor.getString(columnIndex)
        cursor.close()

        Log.d(TAG, "Photo path in storage: $photoPath")
        return photoPath
    }

    /* Scale and fix photo orientation */
    private fun processPhoto(photoPath: String): Bitmap {
        val scaledPhoto = scaleBitmap(BitmapFactory.decodeFile(photoPath))

        val rotatedPhoto: Bitmap
        val orientation = ExifInterface(photoPath).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
        rotatedPhoto = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateBitmap(scaledPhoto, 90F)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateBitmap(scaledPhoto, 180F)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateBitmap(scaledPhoto, 270F)
            }
            else -> {
                scaledPhoto
            }
        }

        Log.d(TAG, "Final photo: ${rotatedPhoto.width}x${rotatedPhoto.height}")

        return rotatedPhoto
    }

    private fun scaleBitmap(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val ratio = width.toFloat() / height.toFloat()

        var finalWidth = width
        var finalHeight = height
        if (width >= height) {
            if (width > MAX_SIDE_LENGTH) {
                finalWidth = MAX_SIDE_LENGTH
                finalHeight = (MAX_SIDE_LENGTH / ratio).toInt()
            }
        } else {
            if (height > MAX_SIDE_LENGTH) {
                finalHeight = MAX_SIDE_LENGTH
                finalWidth = (MAX_SIDE_LENGTH * ratio).toInt()
            }
        }
        return Bitmap.createScaledBitmap(source, finalWidth, finalHeight, true)
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    private fun postPhoto(photo: Bitmap) {
        val wrapper = ContextWrapper(context)

        var file = wrapper.getDir(context.cacheDir.name, Context.MODE_PRIVATE)
        file = File(file, "photo-${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val os: OutputStream = FileOutputStream(file)
            photo.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val outputPhotoPath = Uri.parse(file.absolutePath).path
        Log.d(TAG, "Photo to send: $outputPhotoPath")

        uploadPhoto(outputPhotoPath, RequestBody.create(MediaType.parse("image/*"), file))
    }

    private fun uploadPhoto(photoFilePath: String?, requestFile: RequestBody) {
        disposable.add(
                apiService.createPhoto(
                        MultipartBody.Part.createFormData("content", photoFilePath, requestFile))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            val photoId = response.id
                            Log.d(TAG, "Created photo with id '$photoId'")
                            startEditPhotoActivity(photoId)
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))
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

    protected abstract fun startEditPhotoActivity(photoId: String)

    protected abstract fun setPhotos()

    protected abstract fun setEditButtonListener()

    protected abstract fun setDeleteButtonListener()
}