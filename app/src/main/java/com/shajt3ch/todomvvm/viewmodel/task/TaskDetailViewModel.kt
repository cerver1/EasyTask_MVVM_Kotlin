package com.shajt3ch.todomvvm.viewmodel.task

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shajt3ch.todomvvm.BuildConfig
import com.shajt3ch.todomvvm.model.local.AppPreferences
import com.shajt3ch.todomvvm.model.local.db.AppDatabase
import com.shajt3ch.todomvvm.model.local.entity.TaskEntity
import com.shajt3ch.todomvvm.model.remote.Networking
import com.shajt3ch.todomvvm.model.remote.request.todo.DeleteTaskRequest
import com.shajt3ch.todomvvm.model.repository.DeleteTaskRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "TaskDetailViewModel"
    }

    private val networkService = Networking.create(BuildConfig.BASE_URL)
    private lateinit var deleteTaskRepository: DeleteTaskRepository

    private var sharedPreferences =
        application.getSharedPreferences(BuildConfig.PREF_NAME, Context.MODE_PRIVATE)
    private var appPreferences: AppPreferences
    private var userId: String

    val idField: MutableLiveData<String> = MutableLiveData()
    val taskId: MutableLiveData<String> = MutableLiveData()
    val dataTime: MutableLiveData<String> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val body: MutableLiveData<String> = MutableLiveData()
    val status: MutableLiveData<String> = MutableLiveData()
    val userIdField: MutableLiveData<String> = MutableLiveData()
    val bgColor: MutableLiveData<String> = MutableLiveData()
    val isValidUser: MutableLiveData<Boolean> = MutableLiveData()
    val isDeleted: MutableLiveData<Boolean> = MutableLiveData()
    private var token: String = ""

    init {
        deleteTaskRepository =
            DeleteTaskRepository(networkService, AppDatabase.getInstance(application))
        appPreferences = AppPreferences(sharedPreferences)
        userId = appPreferences.getUserId().toString()

        token = appPreferences.getAccessToken().toString()
    }


    fun checkUserId() {

        try {
            isValidUser.value = userIdField.value == userId

        } catch (httpException: HttpException) {
            Log.e(TAG, httpException.toString())


        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())

        }

    }

    fun deleteTask() {
        viewModelScope.launch {
            val response = deleteTaskRepository.deleteTaskFromApi(
                token,
                DeleteTaskRequest(idField.value!!, userId)
            )

            if (response.code() == 200) {
                response.body()?.run {
                    val result = deleteTaskRepository.deleteTaskFromDb(
                        TaskEntity(
                            taskId = id,
                            userId = this.userId,
                            title = this.title,
                            body = this.body,
                            status = this.status,
                            createdAt = this.createdAt,
                            updatedAt = this.updatedAt
                        )
                    )

                    if (result >= 1) {
                        isDeleted.postValue(true)
                        Log.d(TAG, "Deleted row : $result")
                    } else {
                        isDeleted.postValue(false)
                        Log.d(TAG, "Error in Delete row")
                    }

                }

            }
        }
    }

}
