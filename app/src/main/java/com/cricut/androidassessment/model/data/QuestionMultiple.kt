package com.cricut.androidassessment.model.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
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

@Dao
interface QuestionMultipleDao {
    @Query("SELECT * FROM QuestionMultiple")
    fun getAll(): List<QuestionMultiple>

    @Query("SELECT * FROM QuestionMultiple")
    fun observeAll(): LiveData<List<QuestionMultiple>>

    @Query("SELECT * FROM QuestionMultiple WHERE id = :id")
    fun get(id: Long): QuestionMultiple

    @Update
    fun update(question: QuestionMultiple)

    @Insert
    fun insert(question: QuestionMultiple):Long
}
