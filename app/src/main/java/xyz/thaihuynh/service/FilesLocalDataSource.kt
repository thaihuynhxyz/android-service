package xyz.thaihuynh.service

import android.annotation.SuppressLint
import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FilesLocalDataSource private constructor(private val mContext: Context) {

    fun getFile(fileUrl: String, callback: GetFileCallback) {
        try {
            val fileName = fileUrl.hashCode().toString()
            mContext.openFileInput(fileName).close()
            callback.onFileLoaded(File(mContext.filesDir, fileName))
        } catch (exception: FileNotFoundException) {
            callback.onDataNotAvailable()
        }
    }

    fun saveFile(fileUrl: String, inputStream: InputStream, saveFileCallback: SaveFileCallback) {
        var file: File? = null
        try {
            file = File(mContext.filesDir, fileUrl.hashCode().toString())

            try {
                saveAndClose(inputStream, FileOutputStream(file))
                saveFileCallback.onFileSaveSuccess(file)
            } catch (e: IOException) {
                file.delete()
                saveFileCallback.onFileSaveFailed()
            }
            saveFileCallback.onFileSaveSuccess(file)
        } catch (e: IOException) {
            file?.delete()
            saveFileCallback.onFileSaveFailed()
        }
    }

    fun saveAndClose(inputStream: InputStream, outputStream: OutputStream): Long {
        var fileSizeDownloaded: Long = 0
        try {
            val fileReader = ByteArray(4096)

            while (true) {
                val read = inputStream.read(fileReader)

                if (read == -1) {
                    break
                }

                outputStream.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()
        } catch (e: IOException) {
            throw IOException()
        } finally {
            inputStream.close()
            outputStream.close()
        }
        return fileSizeDownloaded
    }

    interface GetFileCallback {

        fun onFileLoaded(file: File)

        fun onDataNotAvailable()
    }

    interface SaveFileCallback {

        fun onFileSaveSuccess(file: File)

        fun onFileSaveFailed()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: FilesLocalDataSource? = null

        fun getInstance(context: Context): FilesLocalDataSource {
            return INSTANCE ?: FilesLocalDataSource(context)
        }
    }
}