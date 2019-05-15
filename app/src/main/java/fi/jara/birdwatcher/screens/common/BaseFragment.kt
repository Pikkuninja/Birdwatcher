package fi.jara.birdwatcher.screens.common

import androidx.fragment.app.Fragment
import fi.jara.birdwatcher.screens.MainActivity
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent

abstract class BaseFragment: Fragment() {
    val presentationComponent: PresentationComponent
        get() = (requireActivity() as MainActivity).presentationComponent
}
