package yukitas.animal.collector.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import yukitas.animal.R

private val mThumbIds = arrayOf(
        R.drawable.ic_test_image_1, R.drawable.ic_test_image_2,
        R.drawable.ic_test_image_3, R.drawable.ic_test_image_4,
        R.drawable.ic_test_image_1, R.drawable.ic_test_image_2,
        R.drawable.ic_test_image_3, R.drawable.ic_test_image_4,
        R.drawable.ic_test_image_1, R.drawable.ic_test_image_2,
        R.drawable.ic_test_image_3, R.drawable.ic_test_image_4)

class AlbumAdapter(private val mContext: Context) : BaseAdapter() {
    override fun getCount(): Int = mThumbIds.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(mContext)
            imageView.layoutParams = ViewGroup.LayoutParams(150, 150)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(mThumbIds[position])
        return imageView
    }
}