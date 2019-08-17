package yukitas.animal.collector.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
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
import yukitas.animal.collector.common.Constants.Companion.RESULT_CREATE_ANIMAL
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.view.activity.PhotoActivity
import yukitas.animal.collector.view.adapter.AnimalsAdapter
import yukitas.animal.collector.view.fragment.dialog.CreateAnimalDialogFragment
import java.util.*
import java.util.stream.Collectors


class AnimalsFragment : CollectionFragment() {
    private val TAG = AnimalsFragment::class.java.simpleName

    private lateinit var binding: yukitas.animal.collector.databinding.FragmentAnimalsBinding
    private lateinit var animalsAdapter: AnimalsAdapter

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animals, container, false)
        animalsAdapter = AnimalsAdapter(context)
        binding.listAnimals.adapter = animalsAdapter

        if (!shouldUpdateOnResume) {
            setAnimals()
        }

        resetActionMenu()
        setAddButtonListener()
        setActionButtonListener()

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

        resetActionMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CREATE_ANIMAL && resultCode == Activity.RESULT_OK) {
            onResume()
        }
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
                    // update animals (sorted by last modified)
                    animalsAdapter.animals = animals.reversed()
                }) {
                    Log.e(TAG, "Some errors occurred: $it")
                }
    }

    private fun setAnimalListener() {
        binding.listAnimals.setOnItemClickListener { _, _, position, _ ->
            val animal = animalsAdapter.animals[position]

            val bundle = Bundle().apply {
                putString(ARG_ANIMAL_ID, animal.id)
                putString(ARG_ANIMAL_NAME, animal.name)
                putStringArray(ARG_ANIMAL_TAGS, animal.tags.toTypedArray())
            }

            val intent = Intent(activity, PhotoActivity::class.java).apply {
                putExtras(bundle)
            }
            activity.startActivity(intent)
        }
    }

    private fun setAddButtonListener() {
        binding.btnAddAnimal.setOnClickListener {
            val createAnimalDialog = CreateAnimalDialogFragment()
            createAnimalDialog.setTargetFragment(this, RESULT_CREATE_ANIMAL)
            createAnimalDialog.show(activity.supportFragmentManager,
                    CreateAnimalDialogFragment::class.java.simpleName)
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
        if (isActionButtonOpen) {
            binding.btnAction.animate().rotationBy(-45f)
        }

        isActionButtonOpen = false

        binding.btnAddPhoto.animate().translationY(0f)
        binding.btnAddAnimal.animate().translationY(0f)

        binding.btnAddPhoto.visibility = View.INVISIBLE
        binding.btnAddAnimal.visibility = View.INVISIBLE
    }

    private fun openActionMenu() {
        if (!isActionButtonOpen) {
            binding.btnAction.animate().rotationBy(45f)
        }

        isActionButtonOpen = true

        binding.btnAddPhoto.visibility = View.VISIBLE
        binding.btnAddAnimal.visibility = View.VISIBLE

        binding.btnAddPhoto.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        binding.btnAddAnimal.animate().translationY(
                -resources.getDimension(R.dimen.standard_130))
    }
}