package yukitas.animal.collector.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_select_collection.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.view.adapter.CollectionArrayAdapter

abstract class SelectCollectionFragment : BaseFragment() {
    private val TAG = SelectCollectionFragment::class.java.simpleName

    protected lateinit var photoId: String
    protected var isCreating = true

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        photoId = activity.intent.getStringExtra(Constants.ARG_PHOTO_ID)
        isCreating = activity.intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)

        return inflater.inflate(R.layout.fragment_select_collection, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCollection.setOnClickListener {
            createNewCollection()
        }
        btnConfirmSelection.setOnClickListener {
            confirmSelectedCollections()
        }

        btnCancelSelection.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_edit_photo_container, EditPhotoMainFragment())
                    .commit()
        }
    }

    protected fun selectItemByCollectionId(multiSelectList: ListView, id: String) {
        val adapter = multiSelectList.adapter as CollectionArrayAdapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).id == id) {
                Log.d(TAG, "Select item ${adapter.getItem(i).name}")
                multiSelectList.setItemChecked(i, true)
            }
        }
    }

    protected fun selectItemsByCollectionIds(multiSelectList: ListView, ids: Set<String>) {
        val adapter = multiSelectList.adapter as CollectionArrayAdapter
        for (i in 0 until adapter.count) {
            if (ids.contains(adapter.getItem(i).id)) {
                Log.d(TAG, "Select item ${adapter.getItem(i).name}")
                multiSelectList.setItemChecked(i, true)
            }
        }
    }

    protected fun getSelectedCollectionIds(multiSelectList: ListView): ArrayList<String> {
        val selectedCollectionPositions = multiSelectList.checkedItemPositions

        return ArrayList<String>().apply {
            for (i in 0 until selectedCollectionPositions.size()) {
                if (selectedCollectionPositions.valueAt(i)) {
                    val selectedCollection = (multiSelectList.adapter as CollectionArrayAdapter).getItem(
                            selectedCollectionPositions.keyAt(i))
                    Log.d(TAG, "Selected collection: ${selectedCollection.name}")
                    add(selectedCollection.id)
                }
            }
        }
    }

    protected abstract fun setList()

    protected abstract fun createNewCollection()

    protected abstract fun confirmSelectedCollections()
}