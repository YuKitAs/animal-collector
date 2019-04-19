package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_viewmode.*
import kotlinx.android.synthetic.main.viewpager_category.*
import yukitas.animal.collector.AnimalCollectorApplication
import yukitas.animal.collector.R
import yukitas.animal.collector.common.ViewMode
import yukitas.animal.collector.networking.ApiService
import yukitas.animal.collector.view.adapter.CategoryPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var categoryPagerAdapter: CategoryPagerAdapter
    private lateinit var tabs: TabLayout
    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewPager = pager_category
        categoryPagerAdapter = CategoryPagerAdapter(supportFragmentManager)

        tabs = sliding_tabs
        tabs.addOnTabSelectedListener(getOnTabSelectedListener())

        setCategories()

        viewPager.adapter = categoryPagerAdapter
        viewPager.addOnPageChangeListener(getOnPageChangeListener())
    }

    private fun setCategories() {
        disposable.add(
                apiService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { categoryPagerAdapter.categories = it })
    }

    private fun getOnTabSelectedListener(): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> AnimalCollectorApplication.currentViewMode = ViewMode.ALBUM
                    1 -> AnimalCollectorApplication.currentViewMode = ViewMode.ANIMAL
                }

                // reset adapter in order to invoke getItem() when position is not changed
                viewPager.adapter = categoryPagerAdapter
                viewPager.currentItem = AnimalCollectorApplication.currentCategoryIndex
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        }
    }

    private fun getOnPageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
                AnimalCollectorApplication.currentCategoryIndex = position
            }

            override fun onPageSelected(position: Int) {
                AnimalCollectorApplication.currentCategoryIndex = position
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
