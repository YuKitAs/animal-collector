package yukitas.animal.collector.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import yukitas.animal.R
import yukitas.animal.collector.adapter.PhotosAdapter

class AlbumPhotosFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_album_photos, container, false)
        val gridView: GridView = view.findViewById(R.id.grid_photos)
        gridView.adapter = PhotosAdapter(context)

        return view
    }
}