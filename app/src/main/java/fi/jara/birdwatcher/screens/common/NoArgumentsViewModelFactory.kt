package fi.jara.birdwatcher.screens.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

class NoArgumentsViewModelFactory(private val providerMap: Map<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = providerMap[modelClass]
        @Suppress("UNCHECKED_CAST")
        return provider?.get() as? T
            ?: throw IllegalArgumentException("No ViewModel Provider for ${modelClass.canonicalName}")

    }
}