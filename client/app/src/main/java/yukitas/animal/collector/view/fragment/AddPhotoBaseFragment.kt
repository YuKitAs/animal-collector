package yukitas.animal.collector.view.fragment

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Location
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

private const val MAX_SIDE_LENGTH = 1080

abstract class AddPhotoBaseFragment : BaseFragment() {
    private val TAG = AddPhotoBaseFragment::class.java.simpleName

    protected lateinit var progressSpinner: View

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val photoPath = readPhotoFromStorage(data.data!!)
            postPhoto(processPhoto(photoPath), getCreationTime(photoPath), getLocation(photoPath))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == Constants.RESULT_LOAD_IMAGE) {
            startActivityForResult(Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Constants.RESULT_LOAD_IMAGE)
        } else {
            Toast.makeText(activity, "Permission denied to read your external storage",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> true
            R.id.action_delete -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun readPhotoFromStorage(data: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(data, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val photoPath = cursor.getString(columnIndex)
        cursor.close()

        Log.d(TAG, "Photo path in storage: '$photoPath'")

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

    private fun getCreationTime(photoPath: String): OffsetDateTime {
        val photoAttrs = Files.readAttributes(File(photoPath).toPath(),
                BasicFileAttributes::class.java)
        val creationTime = OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(photoAttrs.creationTime().toMillis()),
                TimeZone.getDefault().toZoneId())

        Log.d(TAG, "Photo created at $creationTime [${TimeZone.getDefault().id}]")
        return creationTime
    }

    private fun getLocation(photoPath: String): Location {
        val exif = ExifInterface(photoPath)

        val latitude = if (exif.latLong != null) exif.latLong[0] else null
        val longitude = if (exif.latLong != null) exif.latLong[1] else null

        Log.d(TAG, "Photo lat: $latitude, long: $longitude")

        if (latitude == null || longitude == null) {
            return Location(null, null, null)
        }

        val address = Geocoder(activity, Locale.getDefault()).getFromLocation(latitude, longitude,
                1)[0] ?: return Location(latitude, longitude, null)

        Log.d(TAG, "Photo address from lat and long: $address")

        val city = address.locality
        val country = address.countryName
        return Location(latitude, longitude, "$city, $country")
    }

    private fun postPhoto(photo: Bitmap, creationTime: OffsetDateTime, location: Location) {
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

        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle(R.string.title_enable_recognition)
            setMessage(R.string.message_enable_recognition)
            setPositiveButton(R.string.btn_confirm_positive
            ) { _, _ ->
                progressSpinner.visibility = View.VISIBLE
                activity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                uploadPhoto(outputPhotoPath, RequestBody.create(MediaType.parse("image/*"), file),
                        creationTime, location, true)
            }
            setNegativeButton(R.string.btn_confirm_negative) { _, _ ->
                uploadPhoto(outputPhotoPath, RequestBody.create(MediaType.parse("image/*"), file),
                        creationTime, location, false)
            }
            setCancelable(false)
        }
        builder.show()
    }

    private fun uploadPhoto(photoFilePath: String?, requestFile: RequestBody,
                            creationTime: OffsetDateTime, location: Location,
                            recognitionEnabled: Boolean) {
        disposable.add(
                apiService.createPhoto(
                        MultipartBody.Part.createFormData("content", photoFilePath,
                                requestFile),
                        creationTime.toString(), location.latitude, location.longitude,
                        location.address, recognitionEnabled)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            val photoId = response.id

                            if (recognitionEnabled) {
                                progressSpinner.visibility = View.GONE
                                activity.window.clearFlags(
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                                val recognizedCategory = response.recognizedCategory
                                Log.d(TAG,
                                        "Created photo with id '$photoId' and recognized category: $recognizedCategory")
                                startEditPhotoActivity(photoId, true, recognizedCategory)
                            } else {
                                Log.d(TAG,
                                        "Created photo with id '$photoId' without recognized category")
                                startEditPhotoActivity(photoId, false, null)
                            }
                        }, {
                            Log.e(TAG, "Cannot create photo. Some errors occurred: $it")
                            it.printStackTrace()

                            Toast.makeText(activity,
                                    getString(R.string.message_server_error),
                                    Toast.LENGTH_SHORT).show()
                        }))
    }

    protected abstract fun setAddPhotoButtonListener()

    protected abstract fun startEditPhotoActivity(photoId: String, recognitionEnabled: Boolean,
                                                  recognizedCategory: String?)
}