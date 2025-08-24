package com.cricut.androidassessment.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cricut.androidassessment.model.Question

@Entity
data class QuestionMultiple(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val question: String,
    val answers: List<String>,
    override var answer: HashSet<Int> = HashSet()
): Question<HashSet<Int>> {

    override fun hasAnswer(): Boolean {
        return answer.isNotEmpty()
    }

    //HACK.  Changing the answer to a different object does not work without this.
    //I don't know why
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun copy(): Question<HashSet<Int>> {
        return QuestionMultiple(id, question, answers, answer)
    }
}
