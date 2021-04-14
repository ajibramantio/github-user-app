package com.dicoding.mygithubuserapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mygithubuserapp.adapter.UserListAdapter
import com.dicoding.mygithubuserapp.api.UserServices
import com.dicoding.mygithubuserapp.databinding.ActivityFavoriteBinding
import com.dicoding.mygithubuserapp.db.DatabaseContract
import com.dicoding.mygithubuserapp.helper.MappingHelper
import com.dicoding.mygithubuserapp.model.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoriteActivity : AppCompatActivity() {
    private var binding: ActivityFavoriteBinding? = null
    private var list: ArrayList<User> = arrayListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.rvUsersFavorite?.setHasFixedSize(true)
        supportActionBar?.title = "Favorite User"
        binding?.progressBarFavorite?.visibility = View.GONE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            loadFavoriteAsync()
        } else {
            val listData = savedInstanceState.getParcelableArrayList<User>(
                EXTRA_STATE
            )
            if (listData != null) {
                showRecyclerList(listData)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, list)
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding?.progressBarFavorite?.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(DatabaseContract.FavoriteColumns.CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(
                    cursor
                )
            }
            binding?.progressBarFavorite?.visibility = View.GONE
            val favorites = deferredNotes.await()
            if (favorites.size > 0) {
                showRecyclerList(favorites)
            } else {
                showRecyclerList(list)
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }


    private fun getDetail(user: User){
        binding?.progressBarFavorite?.visibility = View.VISIBLE
        binding?.rvUsersFavorite?.visibility = View.GONE
        val service = retrofit.create(UserServices::class.java)
        val call = service.getDetailUserData(user.login ?: "")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    val UserList = response.body()
                    user.followers= UserList.followers
                    user.following = UserList.following
                    user.name = UserList.name
                    binding?.progressBarFavorite?.visibility = View.GONE
                    binding?.rvUsersFavorite?.visibility = View.VISIBLE
                    showSelectedProject(user)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable?) {
                binding?.progressBarFavorite?.visibility = View.GONE
                binding?.rvUsersFavorite?.visibility = View.VISIBLE
                Toast.makeText(this@FavoriteActivity
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showSelectedProject(user: User) {
        val moveWithDataIntent = Intent(this@FavoriteActivity, DetailActivity::class.java)
        moveWithDataIntent.putExtra(DetailActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)
    }

    private fun showRecyclerList(listUser: ArrayList<User>) {
        list = listUser
        binding?.rvUsersFavorite?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter =
            UserListAdapter(
                listUser
            )
        binding?.rvUsersFavorite?.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                getDetail(data)
            }
        })
    }

    private fun showSnackbarMessage(message: String) {
        binding?.rvUsersFavorite?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}