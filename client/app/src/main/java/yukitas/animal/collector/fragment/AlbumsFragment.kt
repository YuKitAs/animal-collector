package yukitas.animal.collector.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import yukitas.animal.R
import yukitas.animal.collector.adapter.AlbumAdapter

class AlbumsFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_albums, container, false)
        val gridview: GridView = view.findViewById(R.id.gridview)
        gridview.adapter = AlbumAdapter(this.context)

        return view
    }
}