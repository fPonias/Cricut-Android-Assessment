package com.cricut.androidassessment.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cricut.androidassessment.ui.theme.DeselectedButton
import com.cricut.androidassessment.ui.theme.SelectedButton

@Composable
fun QuestionButton(value: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) {
        SelectedButton
    } else {
        DeselectedButton
    }

    var lastClicked = 0L

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = {
            val now = System.currentTimeMillis()
            if (now - lastClicked > 100) {
                onClick()
                lastClicked = now
            }
        }
    ) {
        Text(value)
    }
}