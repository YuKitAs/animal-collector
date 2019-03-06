package yukitas.animal.collector.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import yukitas.animal.collector.R
import yukitas.animal.collector.model.Photo

class PhotosAdapter(private val context: Context) : BaseAdapter() {
    var photos = emptyList<Photo>()
        set(photos) {
            field = photos
            notifyDataSetChanged()
        }

    override fun getCount(): Int = photos.size

    override fun getItem(position: Int): Any? = photos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView
                ?: LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
    }
}