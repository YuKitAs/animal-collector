package yukitas.animal.collector.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_animal.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.SaveAnimalRequest
import yukitas.animal.collector.networking.ApiService

/**
 * Create or update an Animal in a predetermined Category
 */
class EditAnimalActivity : AppCompatActivity() {
    private val TAG = EditAnimalActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_animal)
        setSupportActionBar(toolbar)

        val isCreating = intent.getBooleanExtra(Constants.ARG_IS_CREATING, true)
        if (isCreating) {
            labelEditAnimal.text = getString(R.string.label_create_animal)

            btnSaveAnimal.setOnClickListener {
                Log.d(TAG, "Creating animal with name '${inputAnimalName.text}'")

                disposable.add(
                        apiService.createAnimal(intent.getStringExtra(Constants.ARG_CATEGORY_ID),
                                SaveAnimalRequest(inputAnimalName.text.toString(),
                                        parseTagsFromText(inputAnimalTags.text)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { animal ->
                                    Log.d(TAG, "Created animal: $animal")
                                    Toast.makeText(this,
                                            getString(R.string.message_create_animal_success),
                                            Toast.LENGTH_SHORT).show()

                                    val data = Intent().apply {
                                        putExtra(Constants.ARG_ANIMAL_ID, animal.id)
                                    }
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                })
            }
        } else {
            labelEditAnimal.text = getString(R.string.label_edit_animal)
            inputAnimalName.setText(intent.getStringExtra(Constants.ARG_ANIMAL_NAME))
            inputAnimalTags.setText(
                    intent.getStringArrayListExtra(Constants.ARG_ANIMAL_TAGS).joinToString(" "))

            btnSaveAnimal.setOnClickListener {
                val animalName = inputAnimalName.text.toString()
                val animalTags = parseTagsFromText(inputAnimalTags.text)

                Log.d(TAG,
                        "Updating animal with name '$animalName' and tags '$animalTags'")

                disposable.add(
                        apiService.updateAnimal(intent.getStringExtra(Constants.ARG_ANIMAL_ID),
                                SaveAnimalRequest(animalName, animalTags))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    Log.d(TAG, "Updated animal")
                                    Toast.makeText(this,
                                            getString(R.string.message_update_animal_success),
                                            Toast.LENGTH_SHORT).show()
                                    // return to MainActivity
                                    finish()
                                })
            }
        }

        setCancelButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun parseTagsFromText(tags: Editable): List<String> {
        return tags.split("\\s+".toRegex()).map { it.trim() }
    }

    private fun setCancelButtonListener() {
        btnCancelAnimalEdit.setOnClickListener { finish() }
    }
}