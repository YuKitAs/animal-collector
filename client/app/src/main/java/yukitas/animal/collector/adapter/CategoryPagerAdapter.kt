package yukitas.animal.collector.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import yukitas.animal.collector.fragment.AlbumsFragment

private const val ARG_CATEGORY = "category"
private const val NUM_CATEGORIES = 3

class CategoryPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = NUM_CATEGORIES

    override fun getItem(i: Int): Fragment {
        val fragment = AlbumsFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_CATEGORY, i + 1)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "CATEGORY " + (position + 1)
    }
}