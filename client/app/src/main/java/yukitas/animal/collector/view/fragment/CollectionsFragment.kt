package yukitas.animal.collector.view.fragment

import android.content.Intent
import android.os.Bundle
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.view.activity.EditPhotoActivity

abstract class CollectionsFragment : AddPhotoBaseFragment() {
    override fun startEditPhotoActivity(photoId: String, recognitionEnabled: Boolean,
                                        recognizedCategory: String?) {
        val bundle = Bundle().apply {
            putString(Constants.ARG_PHOTO_ID, photoId)
            putBoolean(Constants.FLAG_RECOGNITION_ENABLED, recognitionEnabled)

            if (!recognizedCategory.isNullOrBlank()) {
                putString(Constants.ARG_CATEGORY_NAME, recognizedCategory)
            }
        }

        val intent = Intent(activity, EditPhotoActivity::class.java).apply {
            putExtras(bundle)
        }
        activity.startActivity(intent)
    }
}