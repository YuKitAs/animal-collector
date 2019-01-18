package yukitas.animal.collector.activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.R
import yukitas.animal.collector.adapter.CategoryPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var categoryPagerAdapter: yukitas.animal.collector.adapter.CategoryPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        categoryPagerAdapter = CategoryPagerAdapter(supportFragmentManager)

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = categoryPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
