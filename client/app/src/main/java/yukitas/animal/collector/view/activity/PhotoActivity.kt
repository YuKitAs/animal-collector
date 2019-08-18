package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.activity_photo.*
import yukitas.animal.collector.AnimalCollectorApplication
import yukitas.animal.collector.R
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.view.fragment.AlbumPhotosFragment
import yukitas.animal.collector.view.fragment.AnimalPhotosFragment


class PhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            when (AnimalCollectorApplication.currentViewMode) {
                ViewMode.ALBUM -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_photos_container, AlbumPhotosFragment())
                            .commit()
                }
                ViewMode.ANIMAL -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_photos_container, AnimalPhotosFragment())
                            .commit()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_collection, menu)
        return true
    }
}