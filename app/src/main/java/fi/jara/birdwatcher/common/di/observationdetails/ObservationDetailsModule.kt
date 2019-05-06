package fi.jara.birdwatcher.common.di.observationdetails

import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragmentArgs
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModelFactory
import java.lang.IllegalStateException
import javax.inject.Named

// Another option would be to make the Fragment to figure out the ID for itself,
// but this way the Fragment doesn't need to know anything about it.
// This approach can be helpful in tests and mimics the Dagger.Android and the way it is used

@Module
class ObservationDetailsModule(private val observationDetailsFragment: ObservationDetailsFragment) {
    @Provides
    fun provideObservationDetailsViewModelFactory(
        observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
        @Named("observationId") observationId: Long
    ): ObservationDetailsViewModelFactory {
        return ObservationDetailsViewModelFactory(observeSingleObservationsUseCase, observationId)
    }

    @Provides
    @Named("observationId")
    fun provideObservationId(): Long {
        observationDetailsFragment.arguments?.let {
            val args = ObservationDetailsFragmentArgs.fromBundle(it)
            return args.observationId
        } ?: throw IllegalStateException("ObservationDetailsFragment shouldn't be created without arguments")
    }
}