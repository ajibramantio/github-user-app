package com.dicoding.mygithubuserapp.api

import com.dicoding.mygithubuserapp.model.User
import com.dicoding.mygithubuserapp.model.UserList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UserServices {
    @GET("search/users?")
    @Headers("Authorization: token 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")
    fun getSearchUserData(@Query("q") username: String): Call<UserList>

    @GET("users/{username}")
    @Headers("Authorization: token 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")
    fun getDetailUserData(@Path(value = "username", encoded = true) username: String): Call<User>

    @GET("users/{username}/followers")
    @Headers("Authorization: token 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")
    fun getFollowers(@Path(value = "username", encoded = true) username: String): Call<ArrayList<User>>

    @GET("users/{username}/following")
    @Headers("Authorization: token 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")
    fun getFollowing(@Path(value = "username", encoded = true) username: String): Call<ArrayList<User>>
}