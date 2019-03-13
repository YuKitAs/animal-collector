package yukitas.animal.collector

import android.app.Application
import yukitas.animal.collector.common.ViewMode

class AnimalCollectorApplication : Application() {
    companion object {
        var viewMode = ViewMode.ALBUM
        var selectedCategory = 0
    }
}