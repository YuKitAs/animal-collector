package yukitas.animal.collector.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.AnimalCollectorApplication
import yukitas.animal.collector.R
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.view.adapter.CategoryPagerAdapter
import yukitas.animal.collector.viewmodel.CategoryViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var categoryPagerAdapter: CategoryPagerAdapter
    private lateinit var tabs: TabLayout
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.pager_category)
        categoryPagerAdapter = CategoryPagerAdapter(supportFragmentManager)


        // TODO clean up listeners
        tabs = findViewById(R.id.sliding_tabs)
        tabs.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> AnimalCollectorApplication.viewMode = ViewMode.ALBUM
                    1 -> AnimalCollectorApplication.viewMode = ViewMode.ANIMAL
                }

                viewPager.adapter = categoryPagerAdapter
                viewPager.currentItem = AnimalCollectorApplication.selectedCategory
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        categoryViewModel.categories.observe(this, Observer { categories ->
            categories?.let {
                categoryPagerAdapter.categories = it
            }
        })

        viewPager.adapter = categoryPagerAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                AnimalCollectorApplication.selectedCategory = position

            }

            override fun onPageSelected(position: Int) {
                AnimalCollectorApplication.selectedCategory = position
            }
        })
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
