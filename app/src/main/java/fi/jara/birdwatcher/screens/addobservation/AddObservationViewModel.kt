package fi.jara.birdwatcher.screens.addobservation

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.InsertNewObservationUseCaseParams
import fi.jara.birdwatcher.observations.ObservationRarity
import kotlinx.coroutines.*
import java.lang.Exception

class AddObservationViewModel(
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver
) : ViewModel() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var hasLocationPermission: Boolean = false
    private var imageResolvingJob: Job? = null

    private val _displayMessages = LiveEvent<String>()
    val displayMessages: LiveData<String>
        get() = _displayMessages

    private val _requestLocationPermission = LiveEvent<Unit>()
    val requestLocationPermission: LiveData<Unit>
        get() = _requestLocationPermission

    private val _gotoListScreen = LiveEvent<Unit>()
    val gotoListScreen: LiveData<Unit>
        get() = _gotoListScreen

    private val _addLocationToObservation = MutableLiveData<Boolean>().apply { value = false }
    val addLocationToObservation: LiveData<Boolean>
        get() = _addLocationToObservation

    private val _userImageBitmap = MutableLiveData<Bitmap?>().apply { value = null }
    val userImageBitmap: LiveData<Bitmap?>
        get() = _userImageBitmap

    private val isResolvingBitmap = MutableLiveData<Boolean>().apply { value = false }
    private val isSavingObservation = MutableLiveData<Boolean>().apply { value = false }

    val saveButtonEnabled: LiveData<Boolean>

    init {
        saveButtonEnabled = MediatorLiveData<Boolean>().apply {
            fun resolve() {
                val resolvingBitmap = isResolvingBitmap.value ?: true
                val saving = isSavingObservation.value ?: true
                value = !resolvingBitmap && !saving
            }

            addSource(isResolvingBitmap) {
                resolve()
            }

            addSource(isSavingObservation) {
                resolve()
            }
        }
    }

    fun onAddLocationToObservationToggled(value: Boolean) {
        if (value && !hasLocationPermission) {
            _requestLocationPermission.postValue(Unit)
        }

        _addLocationToObservation.value = value
    }

    fun onLocationPermissionRequestFinished(hasPermission: Boolean) {
        hasLocationPermission = hasPermission
        if (!hasPermission) {
            _addLocationToObservation.value = false
        }
    }

    fun setImage(uri: Uri) {
        imageResolvingJob?.cancel()
        imageResolvingJob = uiScope.launch {
            isResolvingBitmap.value = true
            try {
                val bitmap = bitmapResolver.getBitmap(uri)
                _userImageBitmap.value = bitmap
            } catch (e: Exception) {
                _displayMessages.postValue("Error reading image")
                _userImageBitmap.value = null
            }
            isResolvingBitmap.value = false
        }
    }

    fun removeImage() {
        imageResolvingJob?.cancel()
        _userImageBitmap.value = null
        isResolvingBitmap.value = false
    }

    fun onSaveObservationClicked(
        observationName: String,
        observationRarity: ObservationRarity?,
        observationDescription: String
    ) {
        uiScope.launch {
            isSavingObservation.value = true

            val addLocation =
                addLocationToObservation.value!! //addLocation LiveData was initialized with nonnull
            val imageBytes = userImageBitmap.value?.let { bitmapResolver.getBytes(it) }


            try {
                val result = insertNewObservationUseCase(
                    observationName,
                    addLocation,
                    observationRarity,
                    observationDescription,
                    imageBytes
                )

                when (result) {
                    is Either.Left -> saveObservationSuccess()
                    is Either.Right -> saveObservationFailed(result.value)
                }

            } catch (e: Exception) {
                saveObservationFailed(e.message ?: "")
            }
        }
    }


    private fun saveObservationSuccess() {
        _displayMessages.value = "Successfully saved observation"
        uiScope.launch {
            delay(1000)
            _gotoListScreen.postValue(Unit)
        }
    }

    private fun saveObservationFailed(errorMessage: String) {
        _displayMessages.value = errorMessage
        isSavingObservation.value = false
    }

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }
}