package yukitas.animal.collector.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AnimalsAdapter
import yukitas.animal.collector.viewmodel.AnimalViewModel

class AnimalsFragment : Fragment() {
    private lateinit var binding: yukitas.animal.collector.databinding.FragmentAnimalsBinding
    private lateinit var animalViewModel: AnimalViewModel
    private lateinit var animalsAdapter: AnimalsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animals, container, false)
        animalsAdapter = AnimalsAdapter(context)
        binding.listAnimals.adapter = animalsAdapter

        animalViewModel = ViewModelProviders.of(this).get(AnimalViewModel::class.java)
        animalViewModel.getAnimalsByCategory(arguments.getString(Constants.ARG_CATEGORY_ID)!!).observe(this, Observer { animals ->
            animals?.let {
                animalsAdapter.animals = it
                binding.listAnimals.setOnItemClickListener { _, _, position, _ ->
                    val intent = Intent(activity, PhotoActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(Constants.ARG_ANIMAL_ID, animalsAdapter.animals[position].id)
                    intent.putExtras(bundle)
                    activity.startActivity(intent)
                }
            }
        })

        return binding.root
    }
}