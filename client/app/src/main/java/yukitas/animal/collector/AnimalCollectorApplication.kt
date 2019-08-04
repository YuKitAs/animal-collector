package yukitas.animal.collector

import android.app.Application
import yukitas.animal.collector.common.ViewMode

class AnimalCollectorApplication : Application() {
    companion object {
        // default view mode on start
        var currentViewMode = ViewMode.ANIMAL
        var currentCategoryIndex = 0
    }
}