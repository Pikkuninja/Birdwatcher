# Birdwatcher
Keep track of bird sightings

[![Build Status](https://app.bitrise.io/app/c2780485f7d3438f/status.svg?token=VdZoib3KvUAL-JrokQI1yQ&branch=master)](https://app.bitrise.io/app/c2780485f7d3438f)

# Features
- Store bird sightings locally, sighting information includes:
  - Bird species name
  - Rarity of the bird
  - Timestamp for when the sighting was stored
  - Optional user location at the time of storing
  - Optional picture from device's gallery
  - Optional notes
- View stored sightings
  - Can be ordered by time or species names

# Implementation notes
- Uses AndroidX and Jetpack libraries heavily: LiveData, Navigation, ViewModel
  - I wanted to test using these, as I haven't had used them in any projects before
- Asynchronity handled with Kotlin Coroutines
  - Again, some tech I hadn't used in earlier projects.
  - Especially with Flows coming soon Coroutines can be used to replace RxJava, and they even have multiplatform support (though multithreading isn't supported on non-JVM targets yet)
- Dependency injection with Dagger
  - Dagger graph includes e.g. UseCases & their dependencies, ViewModelProviders
  - Dagger.Android extension wasn't used, my current project at work uses it and I wanted to return to the roots for a change.

# TODOs
- A screen for viewing single sighting's details
- Better tests coverage, both unit and instrumentation
- Add an About screen with notes and licenses of libraries used
- Animate moving between screens
- Better form error handling: currently errors in filling the add sighting form are reported 1 at a time
- Better feedback when saving the sighting is in progress, getting user's location with GPS can take a while
  - Maybe add a cancelation option too?
- Remove LiveData from repository layer and replace it with Kotlin Flows
  - Might need switching to SQLDelight
- Try making ViewModels run their Coroutines on a background thread by default, updating the UI through LiveData should handle moving to UI thread already
  - Overall more Coroutine experimentation, maybe replace saveButtonEnabled MediatorLiveData with Channels / Flows?
- Update the App theme (colors, etc.) from defaults and make an app icon

# Running tests
Unit tests (to be added)
1. Run ``./gradlew test``

Android tests (currently has Room tests, UI tests to be added)
1. Make sure that an Android emulator is running (or a real device with USB debug setup is connected)
2. Run ``./gradlew connectedAndroidTest``

# License
The app is open sourced with MIT license
