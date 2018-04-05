package xyz.thaihuynh.service

import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.concurrent.Executors

@RunWith(JUnit4::class)
class FilesRepositoryTest {

    private lateinit var mFilesRepository: FilesRepository
    private lateinit var mApiService: ApiService
    private lateinit var mFilesLocalDataSource: FilesLocalDataSource

    @Before
    fun setup() {
        mApiService = Mockito.mock<ApiService>(ApiService::class.java)
        mFilesLocalDataSource = Mockito.mock<FilesLocalDataSource>(FilesLocalDataSource::class.java)
        mFilesRepository = FilesRepository.getInstance(mApiService, mFilesLocalDataSource, Executors.newSingleThreadExecutor())
    }
}