package com.cricut.androidassessment.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.model.data.QuestionInput
import com.cricut.androidassessment.model.data.QuestionMultiple
import com.cricut.androidassessment.model.data.QuestionSingle
import com.cricut.androidassessment.model.data.QuestionTrueFalse
import com.cricut.androidassessment.model.QuestionsModel
import com.cricut.androidassessment.ui.viewmodel.QuestionsViewModel
import com.cricut.androidassessment.ui.components.QuestionButton
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import com.cricut.androidassessment.ui.theme.DeselectedButton
import com.cricut.androidassessment.ui.theme.SelectedButton
import com.cricut.androidassessment.R

@Composable
fun AssessmentScreen(
    modifier: Modifier = Modifier,
    viewModel: QuestionsViewModel = viewModel()
) {
    val question by viewModel.question.collectAsState()
    val nextEnabled by viewModel.nextEnabled.collectAsState()
    val previousVisible by viewModel.previousVisible.collectAsState()

    if (question == null) {
        return Box(modifier = modifier.fillMaxSize()) {
            Text(
                modifier = modifier.align(Alignment.Center),
                text = "No questions available"
            )
        }
    }

    @Composable
    fun renderTrueFalse(question: QuestionTrueFalse) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(Modifier.weight(1f)) {
                QuestionButton("True",
                    question.answer == true
                ) {
                    viewModel.onTrueFalseClicked(true)
                }
            }
            Box(Modifier.weight(1f)) {
                QuestionButton("False",
                    question.answer == false
                ) {
                    viewModel.onTrueFalseClicked(false)
                }
            }
        }
    }

    @Composable
    fun renderInput(question: QuestionInput) {
        val value = remember { mutableStateOf(question.answer ?: "") }
        Box(modifier.fillMaxWidth()) {
            TextField(
                modifier = modifier.fillMaxWidth(),
                value = value.value,
                onValueChange = {newValue:String ->
                    value.value = newValue
                    viewModel.onInputChanged(newValue)
                },
                label = {Text(stringResource(R.string.question_input))},
                keyboardActions = KeyboardActions {  }
            )
        }
    }

    @Composable
    fun renderSingle(question: QuestionSingle) {
        Column (modifier.fillMaxWidth()) {
            question.answers.mapIndexed { index, value ->
                QuestionButton(value,
                    index == question.answer,
                {
                    viewModel.onSingleClicked(index)
                })
            }
        }
    }

    @Composable
    fun renderMultiple(question: QuestionMultiple) {
        Column (modifier.fillMaxWidth()) {
            question.answers.mapIndexed { index, value ->
                QuestionButton(value,
                    question.answer.contains(index),
                    {
                    viewModel.onMultipleClicked(index)
                })
            }
        }
    }

    @Composable
    fun renderQuestion() {
        Box {
            Text(modifier = modifier.align(Alignment.Center), text = question?.question ?: "")
        }

        when (question) {
            is QuestionTrueFalse -> {
                renderTrueFalse(question as QuestionTrueFalse)
            }
            is QuestionInput -> {
                renderInput(question as QuestionInput)
            }
            is QuestionSingle -> {
                renderSingle(question as QuestionSingle)
            }
            is QuestionMultiple -> {
                renderMultiple(question as QuestionMultiple)
            }
        }
    }

    @Composable
    fun renderButtons() {
        val nextColor = if (nextEnabled) {
            SelectedButton
        } else {
            DeselectedButton
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (previousVisible) {
                Button(
                    modifier = modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SelectedButton),
                    onClick = { viewModel.onBackClicked() }
                ) {
                    Text(stringResource(R.string.question_back))
                }
            }
            Button(
                modifier = modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = nextColor),
                onClick = { viewModel.onNextClicked() }
            ) {
                Text(stringResource( R.string.question_next))
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Column(modifier.fillMaxSize().weight(weight = 1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            renderQuestion()
        }
        renderButtons()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    val model = QuestionsModel()
    val items = model.parseData("[{\n" +
            "    \"id\": 4,\n" +
            "    \"question\": \"What media types are natively supported in Android?\",\n" +
            "    \"type\": \"multi\",\n" +
            "    \"answers\": [\"webm\", \"aac\", \"mp3\", \"svg\"]\n" +
            "  }]")
    for (item in items) {
        model.questions.value.add(item)
    }
    val vm = QuestionsViewModel(model, 1)

    AndroidAssessmentTheme {
        AssessmentScreen(viewModel = vm)
    }
}
