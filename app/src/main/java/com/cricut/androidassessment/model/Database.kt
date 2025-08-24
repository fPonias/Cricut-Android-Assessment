package com.cricut.androidassessment.model


import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import com.cricut.androidassessment.model.data.QuestionInput
import com.cricut.androidassessment.model.data.QuestionInputDao
import com.cricut.androidassessment.model.data.QuestionMultiple
import com.cricut.androidassessment.model.data.QuestionMultipleDao
import com.cricut.androidassessment.model.data.QuestionSingle
import com.cricut.androidassessment.model.data.QuestionSingleDao
import com.cricut.androidassessment.model.data.QuestionTrueFalse
import com.cricut.androidassessment.model.data.QuestionTrueFalseDao

interface Question<T> {
    val id: Int
    val question: String
    var answer: T
    fun hasAnswer(): Boolean
    fun copy():Question<T>
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