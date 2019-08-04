package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_ID
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_NAME
import yukitas.animal.collector.common.Constants.Companion.ARG_ANIMAL_TAGS
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.view.activity.EditAnimalActivity
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AnimalsAdapter
import java.util.*
import java.util.stream.Collectors

class AnimalsFragment : CollectionFragment() {
    private val TAG = AnimalsFragment::class.java.simpleName

    private lateinit var binding: yukitas.animal.collector.databinding.FragmentAnimalsBinding
    private lateinit var animalsAdapter: AnimalsAdapter

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animals, container, false)
        animalsAdapter = AnimalsAdapter(context)
        binding.listAnimals.adapter = animalsAdapter

        if (!shouldUpdateOnResume) {
            setAnimals()
        }

        setAddButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (shouldUpdateOnResume) {
            Log.v(TAG, "Updating animals onResume")
            setAnimals()
        } else {
            shouldUpdateOnResume = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setAnimals() {
        val categoryId = arguments.getString(ARG_CATEGORY_ID)!!

        Log.d(TAG, "Retrieving animals for category $categoryId")

        disposable.add(
                apiService.getAnimalsByCategory(categoryId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setThumbnails))

        setAnimalListener()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("CheckResult")
    private fun setThumbnails(animals: List<Animal>) {
        if (animals.isEmpty()) {
            animalsAdapter.animals = animals
            return
        }

        val animalThumbnailMaps: List<Maybe<Map<String, Photo?>>> = animals.stream().map { animal ->
            apiService.getAnimalThumbnail(animal.id, THUMBNAIL_SIDE_LENGTH,
                    THUMBNAIL_SIDE_LENGTH).map { photo ->
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
                    // update animals
                    animalsAdapter.animals = animals
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAnimalListener() {
        binding.listAnimals.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            val animal = animalsAdapter.animals[position]
            bundle.putString(ARG_ANIMAL_ID, animal.id)
            bundle.putString(ARG_ANIMAL_NAME, animal.name)
            bundle.putStringArray(ARG_ANIMAL_TAGS, animal.tags.toTypedArray())

            val intent = Intent(activity, PhotoActivity::class.java).apply {
                putExtras(bundle)
            }
            activity.startActivity(intent)
        }
    }

    private fun setAddButtonListener() {
        binding.btnAddAnimal.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ARG_CATEGORY_ID, arguments.getString(ARG_CATEGORY_ID))

            val intent = Intent(activity, EditAnimalActivity::class.java).apply {
                putExtras(bundle)
            }

            activity.startActivity(intent)
        }
    }
}