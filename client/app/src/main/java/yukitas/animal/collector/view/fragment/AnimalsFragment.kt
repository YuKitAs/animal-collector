package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AnimalsAdapter
import yukitas.animal.collector.viewmodel.AnimalViewModel
import java.util.*
import java.util.stream.Collectors

class AnimalsFragment : Fragment() {
    private val TAG = AnimalsFragment::class.java.simpleName

    private lateinit var binding: yukitas.animal.collector.databinding.FragmentAnimalsBinding
    private lateinit var animalViewModel: AnimalViewModel
    private lateinit var animalsAdapter: AnimalsAdapter
    private val disposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animals, container, false)
        animalsAdapter = AnimalsAdapter(context)
        binding.listAnimals.adapter = animalsAdapter

        animalViewModel = ViewModelProviders.of(this).get(AnimalViewModel::class.java)

        setAnimals()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setAnimals() {
        disposable.add(
                ApiService.create().getAnimalsByCategory(arguments.getString(
                        Constants.ARG_CATEGORY_ID)!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setThumbnails))

        setAnimalListner()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("CheckResult")
    private fun setThumbnails(animals: List<Animal>) {
        val animalThumbnailMaps: List<Maybe<Map<String, Photo?>>> = animals.stream().map { animal ->
            ApiService.create().getAnimalThumbnail(animal.id).map { photo ->
                Collections.singletonMap(animal.id, photo)
            }.defaultIfEmpty(Collections.singletonMap(animal.id, null as Photo?))
        }.collect(Collectors.toList())

        Maybe.zip(animalThumbnailMaps.asIterable()) { obj -> obj }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var animalThumbnailMap: Map<String, Photo?> = emptyMap()
                    it.forEach { animalThumbnail ->
                        animalThumbnailMap = animalThumbnailMap.plus(
                                animalThumbnail as Map<String, Photo?>)
                    }
                    animals.forEach { animal ->
                        animal.thumbnail = animalThumbnailMap[animal.id]
                        Log.d(TAG,
                                "Set thumbnail ${animalThumbnailMap[animal.id]} for animal ${animal.name}")
                    }
                    animalsAdapter.animals = animals
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAnimalListner() {
        binding.listAnimals.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(activity, PhotoActivity::class.java)
            val bundle = Bundle()
            bundle.putString(Constants.ARG_ANIMAL_ID, animalsAdapter.animals[position].id)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}