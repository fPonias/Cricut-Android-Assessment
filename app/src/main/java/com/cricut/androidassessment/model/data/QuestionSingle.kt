package com.cricut.androidassessment.model.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
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

@Dao
interface QuestionSingleDao {
    @Query("SELECT * FROM QuestionSingle ORDER BY id")
    fun getAll(): List<QuestionSingle>

    @Query("SELECT * FROM QuestionSingle ORDER BY id")
    fun observeAll(): androidx.lifecycle.LiveData<List<QuestionSingle>>

    @Query("SELECT * FROM QuestionSingle WHERE id = :id")
    fun get(id: Long): QuestionSingle

    @Update
    fun update(question: QuestionSingle)

    @Insert
    fun insert(question: QuestionSingle):Long
}