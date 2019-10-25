# Birdwatcher
Keep track of bird observations

[![Build Status](https://app.bitrise.io/app/c2780485f7d3438f/status.svg?token=VdZoib3KvUAL-JrokQI1yQ&branch=master)](https://app.bitrise.io/app/c2780485f7d3438f)

# Features
- Store bird observations locally, observation information includes:
  - Bird species name
  - Rarity of the bird
  - Timestamp for when the observation was stored
  - Optional user location at the time of storing
  - Optional picture from device's gallery
  - Optional notes
- View stored observations in a list
  - Can be ordered by timestamp or species names
- View a single stored observation in more detail

# Implementation notes
- Uses AndroidX and Jetpack libraries heavily: LiveData, Navigation, Room, ViewModel
  - I wanted to test using these, as Room was the only one I had previously used
- Asynchronity handled with Kotlin Coroutines
  - Again, some tech I hadn't used in earlier projects
  - Especially with Flows coming soon Coroutines can be used to replace RxJava, and they even have multiplatform support (though multithreading isn't supported on non-JVM targets yet)
- Dependency injection with Dagger
  - Dagger graph includes e.g. UseCases & their dependencies, ViewModelProviders

# Things that could be improved
- UI tests
- Better form error handling: currently errors in filling the add observation form are reported 1 at a time
- Better feedback when saving the observation is in progress, getting user's location with GPS can take a while
  - Maybe add a cancelation option too?
- Better i18n, viewmodels can currently just send hardcoded strings to fragments
- Remove LiveData from repository layer and replace it with Kotlin Flows
  - Might need switching to SQLDelight
- Remove all other Android dependencies from business layer (ImageStorage is used in an UseCase, and that exposes Android Uris)
- Update the App theme (colors, etc.) from defaults and make an app icon

# Building and running
Project uses gradle and doesn't require anything out of your regular Android app building pipeline. The Gradle warnings about 'variant.getPreBuild' and 'variant.getMergeResources' are expected and caused by the gradle plugin used to automatically generate open source licenses listings (https://github.com/google/play-services-plugins/issues/37, issue is closed but people are still reporting having these issues with newest releases).

Easiest way to build and run is through Android Studio

Building from command line can be done with commands like ``./gradlew assembleDebug``

To install a debug build to a connected device through command line, run ``./gradlew installDebug``, and then you can start the app from the device

# Running tests
You can run tests either through Android Studio or from the command line

Unit tests from command line
1. Run ``./gradlew test``

Android tests from command line (currently has Room tests, UI tests to be added)
1. Make sure that an Android emulator is running (or a real device with USB debug setup is connected)
2. Run ``./gradlew connectedAndroidTest``

# License
The app is open sourced with MIT license
