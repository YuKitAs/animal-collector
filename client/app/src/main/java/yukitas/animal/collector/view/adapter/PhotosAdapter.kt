package yukitas.animal.collector.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import yukitas.animal.collector.R
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.utility.toBitmap

class PhotosAdapter(private val context: Context) : BaseAdapter() {
    private lateinit var binding: yukitas.animal.collector.databinding.ItemPhotoBinding

    var photos = emptyList<Photo>()
        set(photos) {
            field = photos
            notifyDataSetChanged()
        }

    override fun getCount(): Int = photos.size

    override fun getItem(position: Int): Any? = photos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_photo, parent,
                false)

        val photo = photos[position]
        binding.photo = photo
        binding.imagePhotoThumbnail.setImageBitmap(toBitmap(photo.content))

        return binding.root
    }
}