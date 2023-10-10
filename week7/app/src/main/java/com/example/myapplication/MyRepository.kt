package com.example.myapplication

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyRepository(context: Context) {
    private val baseURL = "https://api.github.com/"
    private val api = retrofitInit()

    private fun retrofitInit() : RestApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(RestApi::class.java)

    }

    private val myDao = MyDatabase.getDatabase(context).myDao
    val repos = myDao.getAll() // LiveData<List<ReposD>>, viewModel에게 제공하는 데이터

    suspend fun refreshData(username: String) {
        withContext(Dispatchers.IO) {
            val repos = api.listRepos(username)
            // convert Repo to RepoD
            val repoDs = repos.map {
                RepoD(it.name, it.owner.login)
            }
            myDao.insertAll(repoDs)
        }
    }
}