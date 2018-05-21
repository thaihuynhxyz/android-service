package xyz.thaihuynh.service

import android.util.Log
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import okio.Okio
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

@RunWith(JUnit4::class)
class ApiServiceTest {

    private var service: ApiService? = null

    private var mockWebServer: MockWebServer? = null

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer!!.url("/"))
                .build()
                .create(ApiService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer!!.shutdown()
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun downloadFirmwareAndCheckSum() {
        enqueueResponse("test")
        val responseBody = service!!.downloadFileWithDynamicUrl("").execute().body()

        assertThat(responseBody, notNullValue())

        var inputStream: InputStream? = null


        try {
            val fileReader = ByteArray(2014)

            val fileSize = responseBody!!.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = responseBody.byteStream()

            val digest = MessageDigest.getInstance("MD5")

            while (true) {
                val read = inputStream!!.read(fileReader)

                if (read == -1) {
                    break
                }

                if (read > 0) digest.update(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()
            }

            assertEquals(fileSize, fileSizeDownloaded)
            assertEquals("9ac566829163928e2388e243f8a1dc46", bytesToString(digest.digest()))
        } catch (e: IOException) {
            Log.e("ApiServiceTest", e.message)
        } finally {
            inputStream?.close()
        }
    }

    private fun bytesToString(input: ByteArray): String {
        val ret = StringBuilder()
        for (anInput in input) {
            ret.append(Integer.toString(anInput.toPositiveInt() + 0x100, 16).substring(1))
        }
        return ret.toString().toLowerCase()
    }

    private fun Byte.toPositiveInt() = toInt() and 0xFF

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        enqueueResponse(fileName, emptyMap())
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String>) {

        val buffer = Buffer()
        buffer.writeAll(Okio.source(File(javaClass.classLoader.getResource("api-response/$fileName").toURI())))

        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer!!.enqueue(mockResponse.setBody(buffer))
    }
}
