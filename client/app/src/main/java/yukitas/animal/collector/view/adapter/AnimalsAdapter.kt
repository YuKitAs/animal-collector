package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
        binding.animal = animals[position]

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