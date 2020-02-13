package fi.jara.birdwatcher.screens.common

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class LambdaSavedStateViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    private val vmLambda: (SavedStateHandle) -> ViewModel
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = vmLambda(handle) as T
}