package yukitas.animal.collector.common

/**
 * Top-level constants
 */
object Constants {
    const val BASE_URL = "http://192.168.178.51:8080/"

    const val COLLECTION_THUMBNAIL_SIDE_LENGTH = 400
    const val PHOTO_THUMBNAIL_SIDE_LENGTH = 400

    // Argument names. The actual values are unimportant.
    const val ARG_CATEGORY_CAT = "cat"
    const val ARG_CATEGORY_DOG = "dog"
    const val ARG_CATEGORY_ID = "category_id"
    const val ARG_CATEGORY_NAME = "category_name"
    const val ARG_ALBUM_ID = "album_id"
    const val ARG_ALBUM_NAME = "album_name"
    const val ARG_ANIMAL_ID = "animal_id"
    const val ARG_ANIMAL_NAME = "animal_name"
    const val ARG_ANIMAL_TAGS = "animal_tags"
    const val ARG_PHOTO_ID = "photo_id"
    const val ARG_PHOTO_DESC = "photo_desc"
    const val FLAG_IS_CREATING = "is_creating"
    const val FLAG_RECOGNITION_ENABLED = "recognition_enabled"
    const val FLAG_CATEGORY_CONFIRMED = "category_confirmed"

    // Activity result codes.
    const val RESULT_LOAD_IMAGE = 1
    const val RESULT_EDIT_ALBUM = 2
    const val RESULT_EDIT_ANIMAL = 2
    const val RESULT_CREATE_ANIMAL = 3
    const val RESULT_CREATE_ALBUM = 3
}