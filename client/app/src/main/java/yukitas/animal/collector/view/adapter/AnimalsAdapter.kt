package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.template_animal_tag.view.*
import yukitas.animal.collector.R
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.utility.toBitmap

class AnimalsAdapter(private val context: Context) : BaseAdapter() {
    private lateinit var binding: yukitas.animal.collector.databinding.ItemAnimalBinding

    var animals = emptyList<Animal>()
        set(animals) {
            field = animals
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        return animals.size
    }

    override fun getItem(position: Int): Any {
        return animals[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_animal,
                parent, false)

        val animal = animals[position]
        binding.animal = animal

        setAnimalThumbnail(animal)
        setAnimalTags(animal, parent)

        return convertView ?: binding.root
    }

    private fun setAnimalThumbnail(animal: Animal) {
        if (animal.thumbnail != null) {
            binding.imageAnimalThumbnail.setImageBitmap(toBitmap(animal.thumbnail!!.content))
        } else {
            binding.imageAnimalThumbnail.setImageResource(R.drawable.ic_test_image_3)
        }
    }

    private fun setAnimalTags(animal: Animal, parent: ViewGroup?) {
        animal.tags.forEach {
            if (it.isNotEmpty()) {
                val tagView = LayoutInflater.from(context).inflate(R.layout.template_animal_tag,
                        parent, false).text_animal_tag_template
                tagView.text = it
                binding.layoutAnimalTags.addView(tagView)

                val spaceView = LayoutInflater.from(context).inflate(R.layout.template_space,
                        parent, false)
                binding.layoutAnimalTags.addView(spaceView)
            }
        }
    }
}