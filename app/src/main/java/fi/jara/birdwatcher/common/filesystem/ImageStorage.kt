package fi.jara.birdwatcher.common.filesystem

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface ImageStorage {
    /***
     * @return identifier that can be used to later retrieve the image
     */
    suspend fun saveImageBytes(bytes: ByteArray, fileName: String): String

    fun getUriFor(identifier: String): Uri
}

class AndroidImageSaver(private val context: Context) : ImageStorage {
    override suspend fun saveImageBytes(bytes: ByteArray, fileName: String): String = suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imageFile = File(dir, fileName)
                val outStream = FileOutputStream(imageFile)
                outStream.write(bytes)
                outStream.flush()
                outStream.close()
                cont.resume(fileName)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }
    }

    override fun getUriFor(identifier: String): Uri {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(dir, identifier)
        return Uri.fromFile(file)
    }
}
