package fi.jara.birdwatcher.common.filesystem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.graphics.Bitmap.CompressFormat
import android.R.attr.bitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import java.io.ByteArrayOutputStream


interface BitmapResolver {
    suspend fun getBitmap(uri: Uri): Bitmap
    suspend fun getBytes(bitmap: Bitmap): ByteArray
}

class AndroidBitmapResolver(private val context: Context) : BitmapResolver {
    override suspend fun getBitmap(uri: Uri) = suspendCoroutine<Bitmap> { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            context.contentResolver.openInputStream(uri)?.let {
                it.use { inputStream ->
                    cont.resume(BitmapFactory.decodeStream(inputStream))
                }
            } ?: run {
                cont.resumeWithException(IOException())
            }
        }
    }

    /**
     * @return Bytes of the image in JPEG format
     */
    override suspend fun getBytes(bitmap: Bitmap): ByteArray = suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            val stream = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.JPEG, 80, stream)
            cont.resume(stream.toByteArray())
        }
    }
}