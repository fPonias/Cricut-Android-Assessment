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

@Dao
interface QuestionTrueFalseDao {
    @Query("SELECT * FROM QuestionTrueFalse ORDER BY id")
    fun getAll(): List<QuestionTrueFalse>

    @Query("SELECT * FROM QuestionTrueFalse ORDER BY id")
    fun observeAll(): LiveData<List<QuestionTrueFalse>>

    @Query("SELECT * FROM QuestionTrueFalse WHERE id = :id")
    fun get(id: Long): QuestionTrueFalse

    @Update
    fun update(question: QuestionTrueFalse)

    @Insert
    fun insert(question: QuestionTrueFalse):Long
}