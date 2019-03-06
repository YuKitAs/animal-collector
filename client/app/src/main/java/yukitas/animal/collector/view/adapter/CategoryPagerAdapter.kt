package yukitas.animal.collector.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import yukitas.animal.collector.common.Constants.Companion.ARG_CATEGORY_ID
import yukitas.animal.collector.common.Mode
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.view.fragment.AlbumsFragment
import yukitas.animal.collector.view.fragment.AnimalsFragment

class CategoryPagerAdapter(fm: FragmentManager, private val mode: Mode) : FragmentStatePagerAdapter(fm) {
    var categories = emptyList<Category>()
        set(categories) {
            field = categories
            notifyDataSetChanged()
        }

    override fun getCount(): Int = categories.size

    override fun getItem(i: Int): Fragment {
        val fragment = when (mode) {
            Mode.ALBUM -> AlbumsFragment()
            Mode.ANIMAL -> AnimalsFragment()
        }

        fragment.arguments = Bundle().apply {
            putString(ARG_CATEGORY_ID, categories[i].id)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return categories[position].name
    }
}