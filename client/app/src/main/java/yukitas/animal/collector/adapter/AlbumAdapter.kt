package yukitas.animal.collector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import yukitas.animal.R

class AlbumAdapter(private val context: Context) : BaseAdapter() {
    override fun getCount(): Int = 12

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView
                ?: LayoutInflater.from(context).inflate(R.layout.item_album, parent, false)
    }
}