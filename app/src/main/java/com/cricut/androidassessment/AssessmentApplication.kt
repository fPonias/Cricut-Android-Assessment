package com.cricut.androidassessment

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.cricut.androidassessment.model.AppDatabase
import com.cricut.androidassessment.model.QuestionsModel
import com.cricut.androidassessment.ui.viewmodel.QuestionsViewModel

class AssessmentApplication : Application() {
    companion object {
        private var _instance: AssessmentApplication? = null
        val instance: AssessmentApplication
            get() {
                val ret = _instance
                if (ret == null) {
                    throw Error("Fatal Error:  Application instance not initialized")
                }

                return ret
            }

        val DATABASE_NAME = "roomdb"
    }

    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    var questionsModel: QuestionsModel? = null

    var db:AppDatabase? = null
        private set

    var activity: MainActivity? = null
        get
        set(value) {
            field = value
            questionsModel = QuestionsModel()
        }

    override fun onCreate() {
        super.onCreate()

        _instance = this

        this.db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, DATABASE_NAME
        ).build()
    }
}
