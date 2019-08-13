package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_DESC
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.databinding.FragmentPhotoDetailBinding
import yukitas.animal.collector.utility.binaryToBitmap
import yukitas.animal.collector.view.activity.EditPhotoActivity
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class PhotoDetailFragment : BaseFragment() {
    private val TAG = PhotoDetailFragment::class.java.simpleName

    private lateinit var binding: FragmentPhotoDetailBinding
    private lateinit var photoId: String

    private var isActionButtonOpen = false
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.fragment_photo_detail, container, false)

        photoId = activity.intent.getStringExtra(ARG_PHOTO_ID)

        if (!shouldUpdateOnResume) {
            setPhoto()
        }

        resetActionMenu()
        setDeleteButtonListener()
        setEditButtonListener()
        setActionButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (shouldUpdateOnResume) {
            Log.v(TAG, "Updating photo onResume")
            setPhoto()
        } else {
            shouldUpdateOnResume = true
        }

        resetActionMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setPhoto() {
        disposable.add(
                apiService.getPhotoById(photoId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            binding.photo = it
                            binding.photoContent.setImageBitmap(binaryToBitmap(it.content))
                            binding.photoDate.text = formatDateTime(it.createdAt)

                            if (it.description.isBlank()) {
                                binding.photoDescription.visibility = View.GONE
                            } else {
                                binding.photoDescription.visibility = View.VISIBLE
                            }

                            setAnimals()
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))
    }

    private fun formatDateTime(createdAt: String): String {
        // convert String to OffsetDateTime
        val createdAtOffsetDateTime = OffsetDateTime.parse(createdAt,
                DateTimeFormatterBuilder().append(
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME).toFormatter())

        // convert OffsetDateTime to formatted LocalDateTime
        return createdAtOffsetDateTime.toLocalDateTime().format(
                DateTimeFormatter.ofPattern("LLL dd, yyyy HH:mm"))
    }

    private fun setAnimals() {
        disposable.add(
                apiService.getAnimalsByPhoto(photoId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            binding.photoAnimals.text = it.joinToString(
                                    ", ") { animal -> animal.name }
                        }
        )
    }

    private fun setEditButtonListener() {
        binding.btnEditPhoto.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.ARG_IS_CREATING, false)
            bundle.putString(ARG_PHOTO_ID, photoId)
            bundle.putString(ARG_PHOTO_DESC, binding.photo!!.description)

            val intent = Intent(activity, EditPhotoActivity::class.java).apply {
                putExtras(bundle)
            }
            activity.startActivity(intent)
        }
    }

    private fun setDeleteButtonListener() {
        binding.btnDeletePhoto.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.apply {
                setTitle(R.string.message_delete_confirm_photo)
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    Log.d(TAG, "Deleting photo '$photoId'")

                    disposable.add(
                            apiService.deletePhoto(photoId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Log.d(TAG, "Deleted photo '$photoId'")
                                        Toast.makeText(activity,
                                                getString(R.string.message_delete_photo_success),
                                                Toast.LENGTH_SHORT).show()
                                        activity.onBackPressed()
                                    })
                }
                setNegativeButton(R.string.btn_confirm_negative) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.show()
        }
    }

    private fun setActionButtonListener() {
        binding.btnAction.setOnClickListener {
            if (!isActionButtonOpen) {
                openActionMenu()
            } else {
                closeActionMenu()
            }
        }
    }

    private fun resetActionMenu() {
        binding.btnAction.visibility = View.VISIBLE
        closeActionMenu()
    }

    private fun closeActionMenu() {
        isActionButtonOpen = false

        binding.btnEditPhoto.animate().translationY(0f)
        binding.btnDeletePhoto.animate().translationY(0f)

        binding.btnEditPhoto.visibility = View.INVISIBLE
        binding.btnDeletePhoto.visibility = View.INVISIBLE
    }

    private fun openActionMenu() {
        isActionButtonOpen = true

        binding.btnEditPhoto.visibility = View.VISIBLE
        binding.btnDeletePhoto.visibility = View.VISIBLE

        binding.btnEditPhoto.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        binding.btnDeletePhoto.animate().translationY(
                -resources.getDimension(R.dimen.standard_130))
    }
}