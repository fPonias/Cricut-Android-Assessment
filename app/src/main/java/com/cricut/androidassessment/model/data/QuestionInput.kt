package com.cricut.androidassessment.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cricut.androidassessment.model.Question

@Entity
data class QuestionInput(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val question: String,
    override var answer: String?
): Question<String?> {

    override fun hasAnswer(): Boolean {
        val a = answer
        return a != null && a.isNotEmpty()
    }

    override fun copy(): Question<String?> {
        return QuestionInput(id, question, answer)
    }
}
