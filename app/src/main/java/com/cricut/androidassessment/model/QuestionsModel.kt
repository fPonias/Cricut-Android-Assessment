package com.cricut.androidassessment.model

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.cricut.androidassessment.AssessmentApplication
import com.cricut.androidassessment.model.data.QuestionInput
import com.cricut.androidassessment.model.data.QuestionMultiple
import com.cricut.androidassessment.model.data.QuestionSingle
import com.cricut.androidassessment.model.data.QuestionTrueFalse
import com.cricut.androidassessment.util.Subscribable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class QuestionsModel {
    val questions = Subscribable<ArrayList<Question<*>>>(arrayListOf())

    private fun jsonArrayToStringArray(arr: JSONArray): ArrayList<String> {
        val ret = ArrayList<String>()
        for (i in 0 until arr.length()) {
            val str = arr.getString(i)
            ret.add(str)
        }

        return ret
    }

    suspend fun remoteUpdate(context: Context) {
        //simulate loading data from a web server
        Thread.sleep(100);
        val str = context.assets.open("sampleQuestions.json").bufferedReader()
            .use { it.readText() }

        val newQuestions = this.parseData(str)

        //update or insert all database entries
        this.questions.value.clear()
        for (question in newQuestions) {
            updateItem(question)
        }

        //reload local data
        load()
    }

    private fun updateItem(question: Question<*>) {
        val db = AssessmentApplication.instance.db ?: return
        when (question) {
            is QuestionTrueFalse -> db.questionTrueFalseDao().upsert(question)
            is QuestionInput -> db.questionInputDao().upsert(question)
            is QuestionSingle -> db.questionSingleDao().upsert(question)
            is QuestionMultiple -> db.questionMultipleDao().upsert(question)
        }
    }

    fun load() {
        this.questions.value.clear()

        val db = AssessmentApplication.instance.db ?: return

        val entitiestf = db.questionTrueFalseDao().getAll()
        this.questions.value.addAll(entitiestf)
        val entitiesi = db.questionInputDao().getAll()
        this.questions.value.addAll(entitiesi)
        val entitiess = db.questionSingleDao().getAll()
        this.questions.value.addAll(entitiess)
        val entitiesm = db.questionMultipleDao().getAll()
        this.questions.value.addAll(entitiesm)

        this.questions.notify()
    }

    fun commitChanges(question: Question<*>) {
        val idx = questions.value.indexOfFirst { value -> value.id == question.id && question.javaClass == value.javaClass }
        if (idx > -1) {
            questions.value[idx] = question
            questions.notify()
        }

        AssessmentApplication.instance.activity?.lifecycleScope?.launch {
            withContext(Dispatchers.IO) {
                updateItem(question)
            }
        }
    }

    fun parseData(json: String):List<Question<*>> {
        val questions = ArrayList<Question<*>>()

        val jArray = JSONArray(json)
        for (i in 0 until jArray.length()) {
            val obj = jArray.get(i) as JSONObject
            val type = obj.get("type")
            val question = obj.get("question") as String
            val id = obj.get("id") as Int

            when(type) {
                "trueFalse" -> {
                    questions.add(QuestionTrueFalse(id, question = question, answer = null))
                }
                "input" -> {
                    questions.add(QuestionInput(id, question = question, answer = null))
                }
                "single" -> {
                    val answersArr = obj.get("answers") as JSONArray
                    val answers = this.jsonArrayToStringArray(answersArr)
                    questions.add(QuestionSingle(
                        id, question = question, answers = answers, answer = null
                    ))
                }
                "multi" -> {
                    val answersArr = obj.get("answers") as JSONArray
                    val answers = this.jsonArrayToStringArray(answersArr)
                    questions.add(QuestionMultiple(
                        id, question = question, answers = answers
                    ))
                }
            }
        }

        return questions
    }
}