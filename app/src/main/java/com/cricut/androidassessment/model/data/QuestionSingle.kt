package com.cricut.androidassessment.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cricut.androidassessment.model.Question

@Entity
data class QuestionSingle(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val question: String,
    val answers: List<String>,
    override var answer: Int?
): Question<Int?> {

    override fun hasAnswer(): Boolean {
        return answer != null
    }

    override fun copy(): Question<Int?> {
        return QuestionSingle(id, question, answers, answer)
    }
}