package yukitas.animal.collector.view.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photos.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_ALBUM_ID
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.dto.SavePhotoRequest
import yukitas.animal.collector.view.activity.EditAlbumActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AlbumPhotosFragment : PhotosFragment() {
    private val TAG = AlbumPhotosFragment::class.java.simpleName
    private lateinit var album: Album

    private val MAX_SIDE_LENGTH = 1080

    override fun setPhotos() {
        val albumId = activity.intent!!.extras!!.getString(ARG_ALBUM_ID)!!

        disposable.add(apiService.getAlbumById(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    album = it

                    textCollectionName.text = it.name.toUpperCase()

                    setEditButtonListener()
                    setDeleteButtonListener()
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

    override fun setEditButtonListener() {
        binding.btnEditCollection.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCreating", false)
            bundle.putString(ARG_ALBUM_ID, album.id)
            bundle.putString(Constants.ARG_ALBUM_NAME, album.name)

            val intent = Intent(activity, EditAlbumActivity::class.java)
            intent.putExtras(bundle)

            activity.startActivity(intent)
        }
    }

    override fun setDeleteButtonListener() {
        binding.btnDeleteCollection.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.apply {
                setMessage(String.format(getString(R.string.message_delete_confirm), "album"))
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    val albumId = album.id
                    Log.d(TAG, "Deleting album '$albumId'")

                    disposable.add(
                            apiService.deleteAlbum(albumId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Log.d(TAG, "Deleted album '$albumId'")
                                        activity.onBackPressed()
                                    })
                }
                setNegativeButton(R.string.btn_confirm_negative
                ) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.contentResolver.query(data.data!!,
                    filePathColumn, null, null, null)!!
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val photoPath = cursor.getString(columnIndex)
            cursor.close()

            Log.d(TAG, "Photo path in storage: $photoPath")

            postPhoto(processPhoto(photoPath))
        }
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

        val photoFilePath = Uri.parse(file.absolutePath).path
        Log.d(TAG, "Photo to send: $photoFilePath")

        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

        disposable.add(
                apiService.createPhoto(
                        MultipartBody.Part.createFormData("content", photoFilePath, requestFile))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            val photoId = response.id
                            Log.d(TAG, "Created photo with id '$photoId'")
                            updatePhoto(photoId)
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))
    }

    private fun updatePhoto(photoId: String) {
        // FIXME
        val animalIds = listOf("02215b2b-94d2-4dcb-b3b8-7e038a76bfec")
        val albumIds = listOf("bfd1ece1-7cf6-4fab-b63c-dd058b8c0f71")
        val description = "test"

        disposable.add(
                apiService.updatePhoto(
                        photoId, SavePhotoRequest(animalIds, albumIds, description))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d(TAG, "Updated photo '$photoId'")
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))
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
}
