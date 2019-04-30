package yukitas.animal.collector.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_animal.*
import kotlinx.android.synthetic.main.activity_main.*
import yukitas.animal.collector.R
import yukitas.animal.collector.common.Constants
import yukitas.animal.collector.model.dto.CreateAnimalRequest
import yukitas.animal.collector.networking.ApiService

class CreateAnimalActivity : AppCompatActivity() {
    private val TAG = CreateAnimalActivity::class.java.simpleName

    private val apiService by lazy { ApiService.create() }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_animal)
        setSupportActionBar(toolbar)

        btnCreateAnimal.setOnClickListener {
            Log.d(TAG, "Creating animal with name '${inputAnimalName.text}'")

            disposable.add(
                    apiService.createAnimal(intent.getStringExtra(Constants.ARG_CATEGORY_ID),
                            CreateAnimalRequest(inputAnimalName.text.toString(), emptyList()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { animal ->
                                Log.d(TAG, "Created animal: $animal")
                                // return to MainActivity
                                finish()
                            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}