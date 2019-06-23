package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import yukitas.animal.collector.R
import yukitas.animal.collector.model.Album

class AlbumsMultiselectionAdapter(private val context: Context) : BaseAdapter() {
    var albums = emptyList<Album>()
        set(albums) {
            field = albums
            notifyDataSetChanged()
        }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemAlbum: View = LayoutInflater.from(context).inflate(
                R.layout.item_collection_multiselection, parent, false)

        itemAlbum.findViewById<TextView>(R.id.text_collection_name).text = albums[position].name

        return convertView ?: itemAlbum
    }

    override fun getItem(position: Int): Any = albums[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = albums.size
}