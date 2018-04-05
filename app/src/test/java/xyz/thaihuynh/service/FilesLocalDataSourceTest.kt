package xyz.thaihuynh.service

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File
import java.io.OutputStream


@RunWith(JUnit4::class)
class FilesLocalDataSourceTest {

    private lateinit var mFilesLocalDataSource: FilesLocalDataSource
    
    @Before
    fun setup() {
        mFilesLocalDataSource = FilesLocalDataSource.getInstance(mock<Context>(Context::class.java))
    }

    @Test
    fun saveAndClose() {
        val file = File(javaClass.classLoader.getResource("api-response/test").toURI())
        val mockOutputStream = Mockito.mock<OutputStream>(OutputStream::class.java)
        val write = mFilesLocalDataSource.saveAndClose(file.inputStream(), mockOutputStream)

        assertEquals(file.length(), write)
        verify(mockOutputStream).close()
    }
}
