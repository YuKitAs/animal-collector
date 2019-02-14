package yukitas.animal.collector.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import yukitas.animal.R
import yukitas.animal.collector.activity.PhotoActivity
import yukitas.animal.collector.adapter.AlbumsAdapter

class AlbumsFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_albums, container, false)
        val gridView: GridView = view.findViewById(R.id.grid_albums)
        gridView.adapter = AlbumsAdapter(context)

        gridView.setOnItemClickListener { _, _, _, _ ->
            val intent = Intent(activity, PhotoActivity::class.java)
            activity.startActivity(intent)
        }

        return view
    }
}