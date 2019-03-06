package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import yukitas.animal.collector.R
import yukitas.animal.collector.databinding.ItemAlbumBinding
import yukitas.animal.collector.model.Album

class AlbumsAdapter(private val context: Context) : BaseAdapter() {
    private lateinit var binding: ItemAlbumBinding

    var albums = emptyList<Album>()
        set(albums) {
            field = albums
            notifyDataSetChanged()
        }

    override fun getCount(): Int = albums.size

    override fun getItem(position: Int): Any? = albums[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_album, parent, false)
        binding.album = albums[position]

        return convertView ?: binding.root
    }
}