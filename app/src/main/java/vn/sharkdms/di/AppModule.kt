package vn.sharkdms.di

import android.app.Application
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.sharkdms.api.BaseApi
import vn.sharkdms.data.Database
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().baseUrl(BaseApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setLenient()
                .create())).build()

    @Provides
    @Singleton
    fun provideBaseApi(retrofit: Retrofit): BaseApi = retrofit.create(BaseApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        Room.databaseBuilder(application, Database::class.java, "database")
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideUserDao(database: Database) = database.userDao()
}