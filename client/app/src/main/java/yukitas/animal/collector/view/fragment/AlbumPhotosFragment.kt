package yukitas.animal.collector.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import yukitas.animal.R
import yukitas.animal.collector.view.adapter.PhotosAdapter

class AlbumPhotosFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_album_photos, container, false)
        val gridView = view.findViewById<GridView>(R.id.grid_photos)
        gridView.adapter = PhotosAdapter(context)

        gridView.setOnItemClickListener { _, _, _, _ ->
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PhotoDetailFragment())
                    .addToBackStack("photos")
                    .commit()
        }

        return view
    }
}