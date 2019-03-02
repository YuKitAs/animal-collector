package yukitas.animal.collector.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import yukitas.animal.collector.model.Category
import yukitas.animal.collector.repository.CategoryRepository

class CategoryViewModel(context: Application) : AndroidViewModel(context) {
    val categories: LiveData<List<Category>>
        get() = CategoryRepository.fetchCategories()
}