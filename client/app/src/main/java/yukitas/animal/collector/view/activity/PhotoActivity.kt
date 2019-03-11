package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.AnimalCollectorApplication
import yukitas.animal.collector.R
import yukitas.animal.collector.view.fragment.PhotosFragment

class PhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            val fragment = PhotosFragment()
            fragment.viewMode = AnimalCollectorApplication.viewMode

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        }
    }
}