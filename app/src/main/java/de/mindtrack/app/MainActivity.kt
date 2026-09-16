package de.mindtrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import de.mindtrack.app.ui.MindTrackScreen
import de.mindtrack.app.ui.TrackerViewModel
import de.mindtrack.app.ui.theme.MindTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as MindTrackApplication).repository

        setContent {
            MindTrackTheme {
                val trackerViewModel: TrackerViewModel = viewModel(
                    factory = TrackerViewModel.factory(repository)
                )
                MindTrackScreen(viewModel = trackerViewModel)
            }
        }
    }
}
