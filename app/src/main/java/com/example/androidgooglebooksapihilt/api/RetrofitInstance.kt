package com.example.androidgooglebooksapihilt.api


import com.example.androidgooglebooksapihilt.repositiories.RetrofitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    val baseURL = "https://www.googleapis.com/"


    @Singleton
    @Provides
    fun getRetrofitServiceInstance(retrofit: Retrofit): RetrofitRepository{
        return retrofit.create(RetrofitRepository::class.java)
    }



    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Connection", "close")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }


//    val getApiRepository: ApiRepository by lazy {
//        getClient.create(ApiRepository::class.java)
//    }
//
//
//    fun getResponseStatusCode(response: Response<*>?): Int {
//        return response?.code() ?: 404
//    }



}