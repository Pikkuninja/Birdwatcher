package fi.jara.birdwatcher.common.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import fi.jara.birdwatcher.common.Coordinate
import kotlinx.coroutines.*
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


// Implemented with basic LocationManager instead of FusedLocationProvider so there's no need for Google Services
class AndroidLocationSource(private val context: Context) : LocationSource {
    override suspend fun getCurrentLocation(): Coordinate {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = suspendCoroutine<Location> { cont ->
                var timeoutJob: Job? = null
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val locationListener = object : LocationListener {
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                    override fun onLocationChanged(location: Location) {
                        timeoutJob?.apply {
                            if (isActive) {
                                cancel()
                                cont.resume(location)
                            }
                        }
                    }
                }

                timeoutJob = CoroutineScope(cont.context).launch {
                    delay(10_000)
                    locationManager.removeUpdates(locationListener)
                    cont.resumeWithException(TimeoutException())
                }

                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    locationListener,
                    Looper.myLooper() ?: Looper.getMainLooper()
                )

            }

            return Coordinate(location.latitude, location.longitude)
        } else {
            throw IllegalAccessException("No location permission")
        }
    }

}