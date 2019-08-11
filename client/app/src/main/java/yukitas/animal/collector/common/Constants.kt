package yukitas.animal.collector.common

class Constants {
    companion object {
        const val ARG_CATEGORY_ID = "category_id"
        const val ARG_ALBUM_ID = "album_id"
        const val ARG_ALBUM_NAME = "album_name"
        const val ARG_ANIMAL_ID = "animal_id"
        const val ARG_ANIMAL_NAME = "animal_name"
        const val ARG_ANIMAL_TAGS = "animal_tags"
        const val ARG_PHOTO_ID = "photo_id"
        const val ARG_PHOTO_DESC = "photo_desc"
        const val ARG_IS_CREATING = "is_creating"
        const val ARG_RECOGNIZED_CATEGORY = "recognized_category"

        const val CATEGORY_UNKNOWN = "UNKNOWN"

        const val RESULT_LOAD_IMAGE = 1
        const val RESULT_EDIT_ALBUM = 2
        const val RESULT_EDIT_ANIMAL = 2
        const val RESULT_CREATE_ANIMAL = 3
        const val RESULT_CREATE_ALBUM = 3
    }
}