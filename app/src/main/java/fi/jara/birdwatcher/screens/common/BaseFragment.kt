package fi.jara.birdwatcher.screens.common

import androidx.fragment.app.Fragment
import fi.jara.birdwatcher.BirdwatcherApplication
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent

open class BaseFragment: Fragment() {
    fun getPresentationComponent(): PresentationComponent {
        val appComponent = (requireActivity().application as BirdwatcherApplication).applicationComponent
        return appComponent.newPresentationComponent()
    }
}
