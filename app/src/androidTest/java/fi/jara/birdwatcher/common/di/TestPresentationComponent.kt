package fi.jara.birdwatcher.common.di

import fi.jara.birdwatcher.common.di.presentation.PresentationComponent
import fi.jara.birdwatcher.screens.MainActivity

class TestPresentationComponent: PresentationComponent {
    override fun inject(mainActivity: MainActivity) {
        //no-op
    }
}