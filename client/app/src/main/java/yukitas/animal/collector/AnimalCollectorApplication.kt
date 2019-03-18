package yukitas.animal.collector

import android.app.Application
import yukitas.animal.collector.common.ViewMode

class AnimalCollectorApplication : Application() {
    companion object {
        var currentViewMode = ViewMode.ALBUM
        var currentCategoryIndex = 0
    }
}