import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.cricut.androidassessment.AssessmentApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preferences() {
    companion object {
        private val QUESTION_INDEX = intPreferencesKey("questionIndex")
        val questionIndex: Flow<Int> =
            AssessmentApplication.instance.dataStore.data.map { settings ->
                settings[QUESTION_INDEX] ?: 0
            }

        suspend fun setQuestionIndex(value: Int) {
            AssessmentApplication.instance.dataStore.edit { settings ->
                settings[QUESTION_INDEX] = value
            }
        }

        private val FIRST_LOAD_INDEX = booleanPreferencesKey("firstLoad")
        val firstLoad: Flow<Boolean> =
            AssessmentApplication.instance.dataStore.data.map { settings ->
                settings[FIRST_LOAD_INDEX] ?: true
            }

        suspend fun setFirstLoad(value: Boolean) {
            AssessmentApplication.instance.dataStore.edit { settings ->
                settings[FIRST_LOAD_INDEX] = value
            }
        }
    }
}