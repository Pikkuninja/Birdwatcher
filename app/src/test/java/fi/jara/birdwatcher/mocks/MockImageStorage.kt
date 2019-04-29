package fi.jara.birdwatcher.mocks

import android.net.Uri
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import java.io.IOException


class AlwaysSucceedingImageStrorage(): ImageStorage {
    override suspend fun saveImageBytes(bytes: ByteArray, fileName: String): String {
        return fileName
    }

    override fun getUriFor(identifier: String): Uri {
        return Uri.EMPTY
    }
}

class AlwaysFailingImageStorage(): ImageStorage {
    override suspend fun saveImageBytes(bytes: ByteArray, fileName: String): String {
        throw IOException()
    }

    override fun getUriFor(identifier: String): Uri {
        return Uri.EMPTY
    }
}