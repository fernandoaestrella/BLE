package no.nordicsemi.android.kotlin.ble.server

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.kotlin.ble.app.server.R

val userDescriptionStrings = arrayListOf<String>()

@Composable
fun UserDescription(index: Int, viewModel: ServerViewModel, userIsMan: Boolean): Boolean {
    userDescriptionStrings.add(stringResource(id = R.string.user_description_question_1))

    var isChecked by remember { mutableStateOf(false) }

    Row()
    {
        OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
            shape = RectangleShape
        ) {

            Text(stringResource(id = R.string.user_description_question_1))
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it; viewModel.updateUserState(index, isChecked); viewModel.printState()  }
            )
        }
    }

    return isChecked
}