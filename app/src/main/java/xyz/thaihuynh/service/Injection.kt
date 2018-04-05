package xyz.thaihuynh.service

import android.content.Context
import retrofit2.Retrofit
import java.util.concurrent.Executors

object Injection {
    fun provideFilesRepository(context: Context): FilesRepository {
        return FilesRepository.getInstance(Retrofit.Builder().baseUrl("https://www.bluetooth.org/").build().create(ApiService::class.java),
                FilesLocalDataSource.getInstance(context), Executors.newSingleThreadExecutor())
    }
}
