package yukitas.animal.collector.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun binaryToBitmap(binaryString: String): Bitmap {
    val decodedByteArray = Base64.decode(binaryString.toByteArray(),
            Base64.NO_WRAP)
    return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
}