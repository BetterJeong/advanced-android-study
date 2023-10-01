package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class Owner (val login: String)
data class Repo (val name: String, val owner: Owner, val url: String)

interface RestApi {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>
}

class MyAdapter(val items: List<Repo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvOwner = v.findViewById<TextView>(R.id.tvOwner)
        val tvRepo = v.findViewById<TextView>(R.id.tvRepo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvOwner.text = items[position].owner.login
        holder.tvRepo.text = items[position].name
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val api = retrofit.create(RestApi::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(emptyList())

        val editText = findViewById<EditText>(R.id.editUsername)
        val button = findViewById<Button>(R.id.buttonQuery)

        button.setOnClickListener {
            val userName = editText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val repos = api.listRepos(userName)
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = MyAdapter(repos)
                }
            }
        }
    }
}