package yukitas.animal.collector.view.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import yukitas.animal.collector.model.Category

class CategoryArrayAdapter(context: Context, private var resource: Int,
                           private var textViewResourceId: Int,
                           private var categories: ArrayList<Category>) : ArrayAdapter<Category>(
        context, resource, textViewResourceId, categories) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getRowView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getRowView(position, convertView, parent)
    }

    override fun getItem(position: Int): Category = categories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = categories.size

    private fun getRowView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent,
                false)
        view.findViewById<TextView>(textViewResourceId).text = getItem(position).name
        view.findViewById<TextView>(textViewResourceId).setTextSize(TypedValue.COMPLEX_UNIT_SP,
                20f)
        return view
    }
}