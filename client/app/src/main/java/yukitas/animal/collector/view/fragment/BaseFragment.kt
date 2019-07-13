package yukitas.animal.collector.view.fragment

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import yukitas.animal.collector.networking.ApiService

open class BaseFragment : Fragment() {
    protected val apiService by lazy { ApiService.create() }
    protected val disposable = CompositeDisposable()
}