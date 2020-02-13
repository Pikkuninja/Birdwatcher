package fi.jara.birdwatcher.common.di

import fi.jara.birdwatcher.common.di.application.ApplicationComponent
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent


class TestApplicationComponent: ApplicationComponent {
    override fun newPresentationComponent(): PresentationComponent = TestPresentationComponent()
}