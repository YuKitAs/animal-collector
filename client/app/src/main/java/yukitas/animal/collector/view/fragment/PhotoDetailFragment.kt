package yukitas.animal.collector.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.databinding.FragmentPhotoDetailBinding
import yukitas.animal.collector.viewmodel.AnimalViewModel
import yukitas.animal.collector.viewmodel.PhotoViewModel

class PhotoDetailFragment : Fragment() {
    private lateinit var binding: FragmentPhotoDetailBinding
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var animalViewModel: AnimalViewModel
    private lateinit var photoId: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_photo_detail, container, false)
        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        animalViewModel = ViewModelProviders.of(this).get(AnimalViewModel::class.java)

        photoId = activity.intent.getStringExtra(ARG_PHOTO_ID)
        photoViewModel.getPhotoById(photoId).observe(this, Observer { photo ->
            photo?.let {
                binding.photo = it

                setPhotoContent(it.content)
                setAnimals()
            }
        })
        return binding.root
    }

    private fun setPhotoContent(content: String) {
        val photoContent = Base64.decode(content.toByteArray(), Base64.NO_WRAP)
        binding.photoContent.setImageBitmap(BitmapFactory.decodeByteArray(photoContent, 0, photoContent.size))
    }

    private fun setAnimals() {
        animalViewModel.getAnimalsByPhoto(photoId).observe(this, Observer { animals ->
            animals?.let {
                binding.photoAnimals.text = animals.joinToString(", ") { animal -> animal.name }
            }
        })
    }
}