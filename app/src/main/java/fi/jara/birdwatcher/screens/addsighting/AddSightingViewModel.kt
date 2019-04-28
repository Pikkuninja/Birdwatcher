package fi.jara.birdwatcher.screens.addsighting

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCase
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCaseParams
import fi.jara.birdwatcher.sightings.SightingRarity
import kotlinx.coroutines.*
import java.lang.Exception

class AddSightingViewModel(
    private val insertNewSightingUseCase: InsertNewSightingUseCase,
    private val bitmapResolver: BitmapResolver
) : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private val _addLocationToSighting = MutableLiveData<Boolean>().apply { value = false }
    val addLocationToSighting: LiveData<Boolean>
        get() = _addLocationToSighting

    private val _userImageBitmap = MutableLiveData<Bitmap?>().apply { value = null }
    val userImageBitmap: LiveData<Bitmap?>
        get() = _userImageBitmap

    private val isResolvingBitmap = MutableLiveData<Boolean>().apply { value = false }
    private val isSavingSighting = MutableLiveData<Boolean>().apply { value = false }

    val saveButtonEnabled: LiveData<Boolean>

    init {
        saveButtonEnabled = MediatorLiveData<Boolean>().apply {
            fun resolve() {
                val resolvingBitmap = isResolvingBitmap.value ?: true
                val saving = isSavingSighting.value ?: true
                value = !resolvingBitmap && !saving
            }

            addSource(isResolvingBitmap) {
                resolve()
            }

            addSource(isSavingSighting) {
                resolve()
            }
        }
    }

    fun onAddLocationToSightingToggled(value: Boolean) {
        if (value && !hasLocationPermission) {
            _requestLocationPermission.postValue(Unit)
        }

        _addLocationToSighting.value = value
    }

    fun onLocationPermissionRequestFinished(hasPermission: Boolean) {
        hasLocationPermission = hasPermission
        if (!hasPermission) {
            _addLocationToSighting.value = false
        }
    }

    fun onSaveSightingClicked(
        sightingName: String,
        sightingRarity: SightingRarity?,
        sightingDescription: String
    ) {
        uiScope.launch {
            isSavingSighting.value = true

            val addLocation = addLocationToSighting.value!! //addLocation LiveData was initialized with nonnull
            val imageBytes = userImageBitmap.value?.let { bitmapResolver.getBytes(it) }

            insertNewSightingUseCase.execute(
                InsertNewSightingUseCaseParams(
                    sightingName,
                    addLocation,
                    sightingRarity,
                    sightingDescription,
                    imageBytes
                ),
                { saveSightingSuccess() },
                { saveSightingFailed(it) }
            )
        }
    }

    private fun saveSightingSuccess() {
        _displayMessages.value = "Successfully saved sighting"
        uiScope.launch {
            delay(1000)
            _gotoListScreen.postValue(Unit)
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

    private fun saveSightingFailed(errorMessage: String) {
        _displayMessages.value = errorMessage
        isSavingSighting.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}