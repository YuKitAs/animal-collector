package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_DESC
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.databinding.FragmentPhotoDetailBinding
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.utility.toBitmap
import yukitas.animal.collector.view.activity.EditPhotoActivity

class PhotoDetailFragment : Fragment() {
    private val TAG = PhotoDetailFragment::class.java.simpleName

    private lateinit var binding: FragmentPhotoDetailBinding
    private lateinit var photoId: String

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.fragment_photo_detail, container, false)

        photoId = activity.intent.getStringExtra(ARG_PHOTO_ID)

        setPhoto()
        setDeleteButtonListener()
        setEditButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setPhoto()
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
                            binding.photoContent.setImageBitmap(toBitmap(it.content))
                            setAnimals()
                        }, {
                            Log.e(TAG, "Some errors occurred: $it")
                        }))
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
                setMessage(R.string.message_delete_photo_confirm)
                setPositiveButton(R.string.btn_confirm_positive
                ) { _, _ ->
                    Log.d(TAG, "Deleting photo '$photoId'")

                    disposable.add(
                            apiService.deletePhoto(photoId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Log.d(TAG, "Deleted photo '$photoId'")
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
}