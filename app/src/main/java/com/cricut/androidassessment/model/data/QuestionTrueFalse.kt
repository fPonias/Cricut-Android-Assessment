package com.cricut.androidassessment.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cricut.androidassessment.model.Question

@Entity
data class QuestionTrueFalse (
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val question: String,
    override var answer: Boolean?
): Question<Boolean?> {

    override fun hasAnswer(): Boolean {
        return answer != null
    }

    override fun copy(): Question<Boolean?> {
        return QuestionTrueFalse(id, question, answer)
    }
}