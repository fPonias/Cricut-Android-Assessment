package com.cricut.androidassessment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.AssessmentApplication
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.QuestionsModel
import com.cricut.androidassessment.model.data.QuestionInput
import com.cricut.androidassessment.model.data.QuestionMultiple
import com.cricut.androidassessment.model.data.QuestionSingle
import com.cricut.androidassessment.model.data.QuestionTrueFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsViewModel(val questionsModel: QuestionsModel, val questionCount: Int): ViewModel() {
    //keep a local filtered list of the questions to show
    private val questions:MutableList<Question<*>> = mutableListOf()

    init {
        questionsModel.questions.subscribe { from, to ->
            filterQuestions()
            updateState()
        }

        viewModelScope.launch {
            Preferences.questionIndex.collect { value ->
                index = value
                updateState()
            }
        }
    }

    private fun updateState() {
        val index = index
        if (index == null || index >= questions.size) {
            question.value = null
            nextEnabled.value = false
            previousVisible.value = false
        } else {
            question.value = questions[index]
            nextEnabled.value = question.value?.hasAnswer() ?: false
            previousVisible.value = index > 0
        }
    }

    var index:Int? = null
        get() {
            return field
        }
        set(value) {
            field = value
            viewModelScope.launch {
                Preferences.setQuestionIndex(value ?: 0)
            }
        }


    val question = MutableStateFlow<Question<*>?>(null)
    val nextEnabled = MutableStateFlow(false)
    val previousVisible = MutableStateFlow(false)

    fun filterQuestions() {
        val max = questionCount.coerceAtMost(questionsModel.questions.value.size)
        val filtered = questionsModel.questions.value.subList(0, max)
        questions.clear()
        questions.addAll(filtered.toMutableList())
    }

    fun onNextClicked() {
        val idx = index;
        if (!nextEnabled.value || idx == null) { return }

        val q = question.value
        if (q != null) {
            questionsModel.commitChanges(q)
        }

        if (idx + 1 < questions.size) {
            index = idx + 1
        }

    }

    fun onBackClicked() {
        val idx = index;
        if (!previousVisible.value || idx == null) { return }

        val q = question.value
        if (q != null) {
            questionsModel.commitChanges(q)
        }

        if (idx - 1 >= 0) {
            index = idx - 1
        }
    }

    fun onTrueFalseClicked(value: Boolean) {
        val q = question.value
        if (q !is QuestionTrueFalse) {
            val cls = q?.javaClass?.name ?: "null"
            Log.e("QuestionsVM",
                "unexpected question event triggered. Expected QuestionTrueFalse instead got $cls"
            )
            return
        }

        val newQ = q.copy() as QuestionTrueFalse
        if (q.answer == value) {
            newQ.answer = null
            nextEnabled.value = false
        } else {
            newQ.answer = value
            nextEnabled.value = true
        }

        question.value = newQ
        questionsModel.commitChanges(newQ)
    }

    fun onInputChanged(value: String) {
        val q = question.value
        if (q !is QuestionInput) {
            val cls = q?.javaClass?.name ?: "null"
            Log.e("QuestionsVM",
                "unexpected question event triggered. Expected QuestionInput instead got $cls"
            )
            return
        }

        if (value.isNotEmpty()) {
            nextEnabled.value = true
        } else {
            nextEnabled.value = false
        }

        q.answer = value
    }

    fun onSingleClicked(value: Int) {
        val q = question.value
        if (q !is QuestionSingle) {
            val cls = q?.javaClass?.name ?: "null"
            Log.e("QuestionsVM",
                "unexpected question event triggered. Expected QuestionSingle instead got $cls"
            )
            return
        }

        if (value < 0 || value >= q.answers.size) {
            Log.e("QuestionsVM",
                "invalid answer index provided $value expected 0 to ${q.answers.size}"
            )
            return
        }

        val newQ = q.copy() as QuestionSingle
        if (q.answer == null || q.answer != value) {
            newQ.answer = value
            nextEnabled.value = true
        } else {
            newQ.answer = null
            nextEnabled.value = false
        }

        question.value = newQ
        questionsModel.commitChanges(newQ)
    }

    fun onMultipleClicked(value: Int) {
        val q = question.value
        if (q !is QuestionMultiple) {
            val cls = q?.javaClass?.name ?: "null"
            Log.e("QuestionsVM",
                "unexpected question event triggered. Expected QuestionMultiple instead got $cls"
            )
            return
        }

        if (value < 0 || value >= q.answers.size) {
            Log.e("QuestionsVM",
                "invalid answer index provided $value expected 0 to ${q.answers.size}"
            )
            return
        }

        val newQ = q.copy() as QuestionMultiple
        if (q.answer.contains(value)) {
            q.answer.remove(value);
        } else {
            q.answer.add(value);
        }
        newQ.answer = q.answer.toHashSet()

        if (newQ.answer.isNotEmpty()) {
            nextEnabled.value = true
        } else {
            nextEnabled.value = false
        }

        question.value = newQ
        questionsModel.commitChanges(newQ)
    }
}