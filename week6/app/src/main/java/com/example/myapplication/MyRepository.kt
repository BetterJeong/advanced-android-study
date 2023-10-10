package com.example.myapplication

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import androidx.room.*

data class Owner (val login: String)
data class Repo (val name: String, val owner: Owner, val url: String)

interface RestApi {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>
}

@Entity
data class RepoD(
    @PrimaryKey val name: String,
    val owner: String
)

@Dao
interface MyDao {
    @Query("SELECT * FROM RepoD")
    fun getAll(): LiveData<List<RepoD>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<RepoD>)
}

@Database(entities = [RepoD::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract val myDao : MyDao

    companion object {
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(context: Context) : MyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, MyDatabase::class.java, "repos_database")
                    .build()
            }
            return INSTANCE as MyDatabase
        }
    }
}

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
    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            val repos = api.listRepos("test")
            // 네트워크에서 가져온 데이터를 Room 데이터에 맞게 변환
            val repoDs = repos.map {
                RepoD(it.name, it.owner.login)
            }
            myDao.insertAll(repoDs)
        }
    }
}