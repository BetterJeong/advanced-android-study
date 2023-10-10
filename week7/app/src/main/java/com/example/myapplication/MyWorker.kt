package com.example.myapplication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val repository = MyRepository(applicationContext)
        try {
            repository.refreshData()
        } catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val name = "com.example.repository_pattern.MyWorker"
    }
}