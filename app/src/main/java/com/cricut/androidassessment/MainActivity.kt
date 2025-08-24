package com.cricut.androidassessment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.ui.viewmodel.QuestionsViewModel
import com.cricut.androidassessment.ui.screens.AssessmentScreen
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object{
        val QUESTIONS_COUNT = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //let the application instance know the main activity has started
        AssessmentApplication.instance.activity = this
        val questionsModel = AssessmentApplication.instance.questionsModel ?: return
        val questionsVM = QuestionsViewModel(questionsModel, QUESTIONS_COUNT)

        //update local data from all remote sources here.
        //my json seed data isn't updated with the rest of the data so we'll ignore the "remote update"
        //if the database already has data in it
        lifecycleScope.launch(Dispatchers.IO) {
            Preferences.firstLoad.collect { firstLoad ->
                if (firstLoad) {
                    Log.d("MainActivity", "First load - updating remote data")
                    val success = questionsModel.remoteUpdate(AssessmentApplication.instance)
                    if (success) {
                        Preferences.setFirstLoad(false)
                    } else {
                        Log.e("MainActivity", "Remote update failed")
                    }
                }
            }
        }

        //setup the navigator and display the first screen
        enableEdgeToEdge()
        setContent {
            AndroidAssessmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AssessmentScreen(
                        modifier = Modifier.padding(innerPadding),
                        questionsVM
                    )
                }
            }
        }
    }
}
