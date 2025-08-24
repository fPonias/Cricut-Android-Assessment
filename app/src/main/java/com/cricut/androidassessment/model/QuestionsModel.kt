package com.cricut.androidassessment.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
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

    private fun updateQuestions(list: List<Question<*>>, type: Class<out Question<*>>) {
        val notInserted = list.toMutableList()
        questions.value.replaceAll {value ->
            if (value.javaClass != type) {
                value
            } else {
                val item = list.firstOrNull { it.id == value.id }
                if (item != null) {
                    notInserted.remove(item)
                    return@replaceAll item
                } else {
                    return@replaceAll value
                }
            }
        }

        for (item in notInserted) {
            questions.value.add(item)
        }
    }

    init {
        val db = AssessmentApplication.instance.db
        val activity = AssessmentApplication.instance.activity

        //as it is now the questions have no inherent order
        if (db != null && activity != null) {
            //observe changes to the database and update our local list
            db.questionTrueFalseDao().observeAll().observe(activity as LifecycleOwner) { list ->
                Log.d("QuestionsModel", "Observed true/false list change")
                updateQuestions(list, QuestionTrueFalse::class.java)
                questions.notify()
            }

            db.questionInputDao().observeAll().observe(activity as LifecycleOwner) { list ->
                Log.d("QuestionsModel", "Observed input list change")
                updateQuestions(list, QuestionInput::class.java)
                questions.notify()
            }

            db.questionSingleDao().observeAll().observe(activity as LifecycleOwner) { list ->
                Log.d("QuestionsModel", "Observed single choice list change")
                updateQuestions(list, QuestionSingle::class.java)
                questions.notify()
            }

            db.questionMultipleDao().observeAll().observe(activity as LifecycleOwner) { list ->
                Log.d("QuestionsModel", "Observed multiple choice list change")
                updateQuestions(list, QuestionMultiple::class.java)
                questions.notify()
            }
        }
    }

    suspend fun remoteUpdate(context: Context):Boolean {
        //simulate loading data from a web server
        Thread.sleep(100);
        val str = context.assets.open("sampleQuestions.json").bufferedReader()
            .use { it.readText() }

        val newQuestions = this.parseData(str)

        //update or insert all database entries
        this.questions.value.clear()
        for (question in newQuestions) {
            insertItem(question)
        }

        return true
    }

    private fun insertItem(question: Question<*>) {
        val db = AssessmentApplication.instance.db ?: return
        when (question) {
            is QuestionTrueFalse -> db.questionTrueFalseDao().insert(question)
            is QuestionInput -> db.questionInputDao().insert(question)
            is QuestionSingle -> db.questionSingleDao().insert(question)
            is QuestionMultiple -> db.questionMultipleDao().insert(question)
        }
    }

    private fun updateItem(question: Question<*>) {
        val db = AssessmentApplication.instance.db ?: return
        when (question) {
            is QuestionTrueFalse -> db.questionTrueFalseDao().update(question)
            is QuestionInput -> db.questionInputDao().update(question)
            is QuestionSingle -> db.questionSingleDao().update(question)
            is QuestionMultiple -> db.questionMultipleDao().update(question)
        }
    }

    fun commitChanges(question: Question<*>) {
        AssessmentApplication.instance.activity?.lifecycleScope?.launch {
            withContext(Dispatchers.IO) {
                updateItem(question)
            }
        }
    }

    private fun jsonArrayToStringArray(arr: JSONArray): ArrayList<String> {
        val ret = ArrayList<String>()
        for (i in 0 until arr.length()) {
            val str = arr.getString(i)
            ret.add(str)
        }

        return ret
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