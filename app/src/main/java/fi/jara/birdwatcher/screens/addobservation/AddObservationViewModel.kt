package fi.jara.birdwatcher.screens.addobservation

import android.net.Uri
import androidx.lifecycle.*
import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObservationRarity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddObservationViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver,
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val imageUriObserver: Observer<Uri?>
    private var hasLocationPermission: Boolean = false

    private val _displayMessages = LiveEvent<String>()
    val displayMessages: LiveData<String>
        get() = _displayMessages

    private val _requestLocationPermission = LiveEvent<Unit>()
    val requestLocationPermission: LiveData<Unit>
        get() = _requestLocationPermission

    private val _requestImageUri = LiveEvent<Unit>()
    val requestImageUri: LiveData<Unit>
        get() = _requestImageUri

    private val _gotoListScreen = LiveEvent<Unit>()
    val gotoListScreen: LiveData<Unit>
        get() = _gotoListScreen

    private val _addLocationToObservation = MutableLiveData<Boolean>().apply { value = false }
    val addLocationToObservation: LiveData<Boolean>
        get() = _addLocationToObservation

    private val _userImageUri = MutableLiveData<Uri?>()
    val userImageUri: LiveData<Uri?>
        get() = _userImageUri

    private val isSavingObservation = MutableLiveData<Boolean>().apply { value = false }

    val saveButtonEnabled: LiveData<Boolean>

    init {
        (savedStateHandle.get<Uri>(SAVED_STATE_IMAGE_URI))?.let {
            _userImageUri.value = it
        }

        imageUriObserver = Observer {
            savedStateHandle.set(SAVED_STATE_IMAGE_URI, it)
        }
        _userImageUri.observeForever(imageUriObserver)

        saveButtonEnabled = MediatorLiveData<Boolean>().apply {
            fun resolve() {
                val saving = isSavingObservation.value ?: true
                value = !saving
            }

            addSource(isSavingObservation) {
                resolve()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _userImageUri.removeObserver(imageUriObserver)
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

    fun onAttachImageToggled(value: Boolean) {
        if (value) {
            // Require removing old image before requesting new one
            if (_userImageUri.value == null) {
                _requestImageUri.postValue(Unit)
            }
        } else {
            _userImageUri.postValue(null)
        }
    }

    fun onImageUriRequestFinished(uri: Uri?) {
        _userImageUri.postValue(uri)
    }

    fun onSaveObservationClicked(
        observationName: String,
        observationRarity: ObservationRarity?,
        observationDescription: String
    ) {
        viewModelScope.launch(uiDispatcher) {
            isSavingObservation.value = true

            val addLocation =
                addLocationToObservation.value!! //addLocation LiveData was initialized with nonnull
            val imageBytes = userImageUri.value?.let {
                val bitmap = bitmapResolver.getBitmap(it)
                bitmapResolver.getBytes(bitmap)
            }

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
        viewModelScope.launch(uiDispatcher) {
            delay(1000)
            _gotoListScreen.postValue(Unit)
        }
    }

    private fun saveObservationFailed(errorMessage: String) {
        _displayMessages.value = errorMessage
        isSavingObservation.value = false
    }

    companion object {
        const val SAVED_STATE_IMAGE_URI = "add_observation_image_uri"
    }
}