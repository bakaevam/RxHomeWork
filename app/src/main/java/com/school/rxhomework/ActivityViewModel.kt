package com.school.rxhomework

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class ActivityViewModel : ViewModel() {
    private val getPostsSubject = PublishSubject.create<Unit>()

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    init {
        getPostsSubject
            .switchMap {
                Repository.getPosts().onErrorReturn { onFailure() }
                    .toObservable()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _state.value = State.Loaded(it.body())
                    Log.d("SSS", "$it")
                },
                {
                    _state.value = State.Loaded(emptyList())
                }
            )
    }

    val getPostsObserver: Observer<Unit> = getPostsSubject

    fun onFailure(): Response<List<MainActivity.Adapter.Item>> {
        return Response.error(0, ResponseBody.create(MediaType.get("error"), "error"))
    }
}
