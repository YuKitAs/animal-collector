package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import yukitas.animal.collector.R
import yukitas.animal.collector.model.Animal

class AnimalsMultiselectionAdapter(private val context: Context) : BaseAdapter() {
    var animals = emptyList<Animal>()
        set(animals) {
            field = animals
            notifyDataSetChanged()
        }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemAnimal: View = LayoutInflater.from(context).inflate(
                R.layout.item_collection_multiselection, parent, false)

        itemAnimal.findViewById<TextView>(R.id.text_collection_name).text = animals[position].name

        return convertView ?: itemAnimal
    }

    override fun getItem(position: Int): Any = animals[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = animals.size
}