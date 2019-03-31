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

class AnimalsAdapter(private val context: Context) : BaseAdapter() {
    private lateinit var binding: yukitas.animal.collector.databinding.ItemAnimalBinding

    var animals = emptyList<Animal>()
        set(animals) {
            field = animals
            notifyDataSetChanged()
        }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_animal, parent, false)

        val animal = animals[position]
        binding.animal = animal

        animal.tags.forEach {
            if (it.isNotEmpty()) {
                val tagView = LayoutInflater.from(context).inflate(R.layout.template_animal_tag, parent, false).text_animal_tag_template
                tagView.text = it
                binding.layoutAnimalTags.addView(tagView)

                val spaceView = LayoutInflater.from(context).inflate(R.layout.template_space, parent, false)
                binding.layoutAnimalTags.addView(spaceView)
            }
        }

        return convertView ?: binding.root
    }

    override fun getItem(position: Int): Any {
        return animals[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return animals.size
    }
}