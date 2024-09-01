package no.nordicsemi.android.kotlin.ble.server

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun LevelStatus(index: Int, viewModel: ServerViewModel) {
    val stateStrings = arrayListOf<String>()
    stateStrings.add(stringResource(id = R.string.match_request_1))
    stateStrings.add(stringResource(id = R.string.match_supply_1))
    stateStrings.add(stringResource(id = R.string.match_request_2))
    stateStrings.add(stringResource(id = R.string.match_supply_2))
    stateStrings.add(stringResource(id = R.string.match_request_3))
    stateStrings.add(stringResource(id = R.string.match_supply_3))
    stateStrings.add(stringResource(id = R.string.match_request_4))
    stateStrings.add(stringResource(id = R.string.match_supply_4))
    stateStrings.add(stringResource(id = R.string.match_request_5))
    stateStrings.add(stringResource(id = R.string.match_supply_5))
    stateStrings.add(stringResource(id = R.string.match_request_6))
    stateStrings.add(stringResource(id = R.string.match_supply_6))
    stateStrings.add(stringResource(id = R.string.match_request_7))
    stateStrings.add(stringResource(id = R.string.match_supply_7))

    Row()
    {
//        Request
        Column {
//            OutlinedCard() {
                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                    shape = RectangleShape) {
                var isChecked by remember { mutableStateOf(false) }

                Text(stateStrings.get(index * 2))
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it; viewModel.updateUserStatus(index * 2, isChecked); viewModel.printState()  }
                )
            }
        }

//        Spacer(modifier = Modifier.size(16.dp))

//        Supply
        Column {
//            OutlinedCard() {
                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                    shape = RectangleShape) {
                var isChecked by remember { mutableStateOf(false) }

                Text(stateStrings.get(index * 2 + 1))
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it; viewModel.updateUserStatus(index * 2 + 1, isChecked); viewModel.printState() }
                )
            }
        }
    }
}