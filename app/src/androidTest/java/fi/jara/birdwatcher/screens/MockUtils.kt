package fi.jara.birdwatcher.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import fi.jara.birdwatcher.observations.ObservationSorting
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModel
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModelFactoryCreator
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModel
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModelFactoryCreator
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModel
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModelFactoryCreator
import io.mockk.every
import io.mockk.mockk

fun mockObservationsListViewModel(): ObservationsListViewModel {
    val viewModel: ObservationsListViewModel = mockk(relaxUnitFun = true)

    every { viewModel.currentSorting } answers { ObservationSorting.TimeDescending }
    every { viewModel.showNoObservations } answers { liveData { } }
    every { viewModel.showLoading } answers { liveData { } }
    every { viewModel.showNoObservations } answers { liveData { } }
    every { viewModel.observations } answers { liveData { } }
    every { viewModel.observationLoadErrors } answers { liveData { } }

    return viewModel
}

fun mockAddObservationViewModel(): AddObservationViewModel {
    val viewModel = mockk<AddObservationViewModel>(relaxUnitFun = true)

    every { viewModel.requestImageUri } answers { liveData { } }
    every { viewModel.displayMessages } answers { liveData { } }
    every { viewModel.addLocationToObservation } answers { liveData { } }
    every { viewModel.saveButtonEnabled } answers { liveData { } }
    every { viewModel.requestLocationPermission } answers { liveData { } }
    every { viewModel.gotoListScreen } answers { liveData { } }
    every { viewModel.userImageUri } answers { liveData { } }

    return viewModel
}

fun mockObservationDetailsViewModel(): ObservationDetailsViewModel {
    val viewModel = mockk<ObservationDetailsViewModel>()

    every { viewModel.observation } answers { liveData { } }
    every { viewModel.observationLoadErrors } answers { liveData { } }
    every { viewModel.showLoading } answers { liveData { } }

    return viewModel
}

fun mockObservationsListViewModelFactoryCreator(vmFactoryLambda: () -> ViewModel): ObservationsListViewModelFactoryCreator {
    val vmFactoryCreator = mockk<ObservationsListViewModelFactoryCreator>()
    every { vmFactoryCreator.invoke(any()) } answers {
        val vmFactory = mockk<ViewModelProvider.Factory>()
        every { vmFactory.create<ViewModel>(any<Class<ViewModel>>()) } answers {
            vmFactoryLambda()
        }
        vmFactory
    }

    return vmFactoryCreator
}

fun mockAddObservationViewModelFactoryCreator(vmFactoryLambda: () -> ViewModel): AddObservationViewModelFactoryCreator {
    val vmFactoryCreator = mockk<AddObservationViewModelFactoryCreator>()
    every { vmFactoryCreator.invoke(any()) } answers {
        val vmFactory = mockk<ViewModelProvider.Factory>()
        every { vmFactory.create<ViewModel>(any<Class<ViewModel>>()) } answers {
            vmFactoryLambda()
        }
        vmFactory
    }

    return vmFactoryCreator
}


fun mockObservationDetailsViewModelFactoryCreator(vmFactoryLambda: () -> ViewModel): ObservationDetailsViewModelFactoryCreator {
    val vmFactoryCreator = mockk<ObservationDetailsViewModelFactoryCreator>()
    every { vmFactoryCreator.invoke(any()) } answers {
        val vmFactory = mockk<ViewModelProvider.Factory>()
        every { vmFactory.create<ViewModel>(any<Class<ViewModel>>()) } answers {
            vmFactoryLambda()
        }
        vmFactory
    }

    return vmFactoryCreator
}
