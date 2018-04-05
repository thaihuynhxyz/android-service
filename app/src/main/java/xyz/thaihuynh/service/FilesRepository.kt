package xyz.thaihuynh.service

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.Executor


class FilesRepository private constructor(private val mApiService: ApiService,
                                          private val mFilesLocalDataSource: FilesLocalDataSource,
                                          private val mExecutor: Executor) {

    fun getFile(fileUrl: String, callback: FilesLocalDataSource.GetFileCallback) {
        mFilesLocalDataSource.getFile(fileUrl, object : FilesLocalDataSource.GetFileCallback {
            override fun onFileLoaded(file: File) {
                callback.onFileLoaded(file)
            }

            override fun onDataNotAvailable() {
                mApiService.downloadFileWithDynamicUrl(fileUrl).enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "server contacted and has file")
                            mExecutor.execute {
                                mFilesLocalDataSource.saveFile(fileUrl, response.body()!!.byteStream(), object : FilesLocalDataSource.SaveFileCallback {

                                    override fun onFileSaveSuccess(file: File) {
                                        callback.onFileLoaded(file)
                                    }

                                    override fun onFileSaveFailed() {
                                        callback.onDataNotAvailable()
                                    }
                                })
                            }
                        } else {
                            Log.d(TAG, "server contact failed")
                            callback.onDataNotAvailable()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "error")
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    companion object {

        private const val TAG = "FilesRepository"

        const val FILE_URL = "docman/handlers/downloaddoc.ashx?doc_id=245140&_ga=2.159027449.27173501.1521441802-23704169.1517386802"

        private var INSTANCE: FilesRepository? = null

        fun getInstance(apiService: ApiService,
                        filesLocalDataSource: FilesLocalDataSource,
                        executor: Executor): FilesRepository {
            return INSTANCE ?: FilesRepository(apiService, filesLocalDataSource, executor)
        }
    }
}