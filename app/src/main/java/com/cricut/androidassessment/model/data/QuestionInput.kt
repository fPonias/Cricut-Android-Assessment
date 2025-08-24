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

@Dao
interface QuestionInputDao {
    @Query("SELECT * FROM QuestionInput ORDER BY id")
    fun getAll(): List<QuestionInput>

    @Query("SELECT * FROM QuestionInput ORDER BY id")
    fun observeAll(): LiveData<List<QuestionInput>>


    @Query("SELECT * FROM QuestionInput WHERE id = :id")
    fun get(id: Long): QuestionInput

    @Update
    fun update(question: QuestionInput)

    @Insert
    fun insert(question: QuestionInput):Long
}