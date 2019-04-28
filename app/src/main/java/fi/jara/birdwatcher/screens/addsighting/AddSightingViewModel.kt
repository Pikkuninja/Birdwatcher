package fi.jara.birdwatcher.screens.addsighting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCase
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCaseParams
import fi.jara.birdwatcher.sightings.SightingRarity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddSightingViewModel(private val insertNewSightingUseCase: InsertNewSightingUseCase) : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var hasLocationPermission: Boolean = false
    private var setAddLocationToAfterPermissionCheck: Boolean = false

    private val _displayMessages = LiveEvent<String>()
    val displayMessages: LiveData<String>
        get() = _displayMessages

    private val _requestLocationPermission = LiveEvent<Unit>()
    val requestLocationPermission: LiveData<Unit>
        get() = _requestLocationPermission

    private val _addLocationToSighting = MutableLiveData<Boolean>().apply { value = false }
    val addLocationToSighting: LiveData<Boolean>
        get() = _addLocationToSighting

    private val _saveButtonEnabled = MutableLiveData<Boolean>().apply { value = true }
    val saveButtonEnabled: LiveData<Boolean>
        get() = _saveButtonEnabled


    fun onAddLocationToSightingToggled(value: Boolean) {
        if (value && !hasLocationPermission) {
            setAddLocationToAfterPermissionCheck = true
            _requestLocationPermission.postValue(Unit)
        } else {
            setAddLocationToAfterPermissionCheck = value
            _addLocationToSighting.value = value
        }
    }

    fun onLocationPermissionRequestFinished(hasPermission: Boolean) {
        val valueToSet = if (hasPermission) {
            setAddLocationToAfterPermissionCheck
        } else {
            false
        }

        if (_addLocationToSighting.value != valueToSet) {
            _addLocationToSighting.value = valueToSet
        }
    }

    fun onSaveSightingClicked(sightingName: String,
                              sightingRarity: SightingRarity?,
                              sightingDescription: String) {
        //All of the LiveDatas are initialized with non nulls
        uiScope.launch {
            _saveButtonEnabled.value = false

            insertNewSightingUseCase.execute(
                InsertNewSightingUseCaseParams(
                    sightingName,
                    addLocationToSighting.value!!,
                    sightingRarity,
                    sightingDescription
                ),
                { saveSightingSuccess() },
                { saveSightingFailed(it) }
            )
        }
    }

    private fun saveSightingSuccess() {
        _displayMessages.value = "Successfully saved sighting"
    }

    private fun saveSightingFailed(errorMessage: String) {
        _displayMessages.value = errorMessage
        _saveButtonEnabled.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}