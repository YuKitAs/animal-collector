package yukitas.animal.collector.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yukitas.animal.R
import yukitas.animal.collector.common.Constants.Companion.ARG_PHOTO_ID
import yukitas.animal.collector.viewmodel.PhotoViewModel

class PhotoDetailFragment : Fragment() {
    private lateinit var binding: yukitas.animal.databinding.FragmentPhotoDetailBinding
    private lateinit var photoViewModel: PhotoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_photo_detail, container, false)
        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        photoViewModel.getPhotoById(activity.intent.getStringExtra(ARG_PHOTO_ID)).observe(this, Observer { photo ->
            photo?.let {
                binding.photo = it
            }
        })
        return binding.root
    }
}