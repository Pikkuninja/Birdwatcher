package fi.jara.birdwatcher.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import fi.jara.birdwatcher.BirdwatcherApplication
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent
import fi.jara.birdwatcher.screens.common.BirdwatcherFragmentFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val presentationComponent: PresentationComponent by lazy {
        (application as BirdwatcherApplication).applicationComponent.newPresentationComponent()
    }

    @Inject
    lateinit var fragmentFactory: BirdwatcherFragmentFactory

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        presentationComponent.inject(this)
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}