package com.cricut.androidassessment.model


import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import com.cricut.androidassessment.model.data.QuestionInput
import com.cricut.androidassessment.model.data.QuestionMultiple
import com.cricut.androidassessment.model.data.QuestionSingle
import com.cricut.androidassessment.model.data.QuestionTrueFalse

interface Question<T> {
    val id: Int
    val question: String
    var answer: T
    fun hasAnswer(): Boolean
    fun copy():Question<T>
}

@Dao
interface QuestionTrueFalseDao {
    @Query("SELECT * FROM QuestionTrueFalse ORDER BY id")
    fun getAll(): List<QuestionTrueFalse>

    @Query("SELECT * FROM QuestionTrueFalse WHERE id = :id")
    fun get(id: Long): QuestionTrueFalse

    @Upsert
    fun upsert(question: QuestionTrueFalse):Long
}

@Dao
interface QuestionInputDao {
    @Query("SELECT * FROM QuestionInput ORDER BY id")
    fun getAll(): List<QuestionInput>

    @Query("SELECT * FROM QuestionInput WHERE id = :id")
    fun get(id: Long): QuestionInput

    @Upsert
    fun upsert(question: QuestionInput):Long
}

@Dao
interface QuestionSingleDao {
    @Query("SELECT * FROM QuestionSingle ORDER BY id")
    fun getAll(): List<QuestionSingle>

    @Query("SELECT * FROM QuestionSingle WHERE id = :id")
    fun get(id: Long): QuestionSingle

    @Upsert
    fun upsert(question: QuestionSingle):Long
}

@Dao
interface QuestionMultipleDao {
    @Query("SELECT * FROM QuestionMultiple")
    fun getAll(): List<QuestionMultiple>

    @Query("SELECT * FROM QuestionMultiple WHERE id = :id")
    fun get(id: Long): QuestionMultiple

    @Upsert
    fun upsert(question: QuestionMultiple):Long
}

class Converters {
    @TypeConverter
    fun stringToList(value: String): List<String> {
        return value.split("\n")
    }

    @TypeConverter
    fun listToString(value: List<String>): String {
        return value.joinToString("\n");
    }

    @TypeConverter
    fun stringToHashSet(value: String): HashSet<Int> {
        if (value.isEmpty()) { return HashSet<Int>() }
        val parts = value.split(",")
        val ints = parts.map { value -> value.toInt() }
        return ints.toHashSet()
    }

    @TypeConverter
    fun hashSetToString(value: HashSet<Int>): String {
        return value.joinToString(",")
    }
}

@Database(entities = [QuestionTrueFalse::class,
    QuestionInput::class,
    QuestionSingle::class,
    QuestionMultiple::class
], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionTrueFalseDao(): QuestionTrueFalseDao
    abstract fun questionInputDao(): QuestionInputDao
    abstract fun questionSingleDao(): QuestionSingleDao
    abstract fun questionMultipleDao(): QuestionMultipleDao
}