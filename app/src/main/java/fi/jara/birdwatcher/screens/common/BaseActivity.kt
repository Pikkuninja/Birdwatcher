package fi.jara.birdwatcher.screens.common

import androidx.appcompat.app.AppCompatActivity
import fi.jara.birdwatcher.BirdwatcherApplication
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent

abstract class BaseActivity : AppCompatActivity() {
    val presentationComponent: PresentationComponent by lazy {
        (application as BirdwatcherApplication).applicationComponent.newPresentationComponent()
    }
}