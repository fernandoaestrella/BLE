/*
 * Copyright (c) 2022, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.kotlin.ble.server

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permissions.ble.RequireBluetooth
import no.nordicsemi.android.common.permissions.ble.RequireLocation
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.kotlin.ble.app.server.R

data class Choice(
    val text: String,
    var isSelected: Boolean = false
)

fun stringToChoices(string: String): List<Choice> {
    val choicesList = mutableListOf<Choice>()
    val stringList = string.split(",")

    for (choiceString in stringList) {
        choicesList.add(Choice(text = choiceString.trim()))
    }

    return choicesList.toList()
}


@Composable
fun MultipleChoiceSelector(input: String, radioSelection: Boolean) {
    val choices = stringToChoices(input)
//    MultipleChoice(choices, radioSelection)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            NordicAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Box(modifier = Modifier.padding(it)) {
            RequireBluetooth {
                RequireLocation {
                    val viewModel = hiltViewModel<ServerViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val scrollState = ScrollState(initial = 0)

                    Column(
                        modifier = Modifier
//                                .fillMaxSize()
                            .verticalScroll(state = scrollState)
                    ) {
                        Spacer(modifier = Modifier.size(16.dp))

                        Text(stringResource(id = R.string.intro))

                        displayStateLevels(viewModel)

                        Spacer(modifier = Modifier.size(16.dp))

                        Text(stringResource(id = R.string.user_description))

                        Spacer(modifier = Modifier.size(16.dp))

                        var userIsMan by remember { mutableStateOf(false) }

//                        Do you look like a man?
                        Row()
                        {
                            OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                shape = RectangleShape
                            ) {
//                                var isChecked by remember { mutableStateOf(false) }

                                Text(stringResource(id = R.string.user_description_question_1))
                                Checkbox(
                                    checked = userIsMan,
                                    onCheckedChange = { userIsMan = it; viewModel.updateUserStatus(7, userIsMan); viewModel.printState()  }
                                )
                            }
                        }

                        if (userIsMan) {
//                            Are you taller than [median height for apparent gender]?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_2_man))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(8, isChecked); viewModel.printState()  }
                                    )
                                }
                            }

//                            Are you older than [median age for apparent gender]?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_3_man))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(9, isChecked); viewModel.printState()  }
                                    )
                                }
                            }

//                            Do you have facial hair?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_4_man))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(10, isChecked); viewModel.printState()  }
                                    )
                                }
                            }
                        } 
                        else 
                        {
//                        Are you taller than [median height for apparent gender]?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_2_woman))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(8, isChecked); viewModel.printState()  }
                                    )
                                }
                            }

//                            Are you older than [median age for apparent gender]?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_3_woman))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(9, isChecked); viewModel.printState()  }
                                    )
                                }
                            }

//                            Does your hair reach below your shoulder?
                            Row()
                            {
                                OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                    shape = RectangleShape
                                ) {
                                    var isChecked by remember { mutableStateOf(false) }

                                    Text(stringResource(id = R.string.user_description_question_4_woman))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it; viewModel.updateUserStatus(10, isChecked); viewModel.printState()  }
                                    )
                                }
                            }
                        }

//                        Are you wearing glasses?
                        Row()
                        {
                            OutlinedCard(modifier = Modifier.padding(horizontal = 2.dp),
                                shape = RectangleShape
                            ) {
                                var isChecked by remember { mutableStateOf(false) }

                                Text(stringResource(id = R.string.user_description_question_5))
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it; viewModel.updateUserStatus(11, isChecked); viewModel.printState()  }
                                )
                            }
                        }

                        var radioSelection by remember { mutableStateOf(false) }
//                        val radioSelection = remember { mutableStateOf(0) }
//                        RadioButton(
//                            onClick = {
//                                radioIisChecked = !radioIisChecked
//                            },
//                            selected = radioIisChecked
//                        )

//                        What color of your clothing occupies the most space ABOVE your hips?
                        Text(stringResource(id = R.string.user_description_question_6))
                        val choices_question_6 = stringToChoices(stringResource(id = R.string.user_description_question_6_options))
                        val (selectedOption_question_6, onOptionSelected_question_6) = remember { mutableStateOf(choices_question_6[1] ) }
                        Column {
                            choices_question_6.forEach { choice ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (choice == selectedOption_question_6),
                                            onClick = {
                                                onOptionSelected_question_6(choice)
                                            }
                                        )
                                        .padding(horizontal = 16.dp)
                                ) {
                                    RadioButton(
                                        selected = (choice == selectedOption_question_6),
                                        onClick = { onOptionSelected_question_6(choice) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = choice.text)
                                }
                            }
                        }
//                        What color of your clothing occupies the most space BELOW your hips??
                        Text(stringResource(id = R.string.user_description_question_7))

                        val choices_question_7 = stringToChoices(stringResource(id = R.string.user_description_question_7_options))
                        val (selectedOption_question_7, onOptionSelected_question_7) = remember { mutableStateOf(choices_question_7[1] ) }
                        Column {
                            choices_question_7.forEach { choice ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (choice == selectedOption_question_7),
                                            onClick = {
                                                onOptionSelected_question_7(choice)
                                            }
                                        )
                                        .padding(horizontal = 16.dp)
                                ) {
                                    RadioButton(
                                        selected = (choice == selectedOption_question_7),
                                        onClick = { onOptionSelected_question_7(choice) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = choice.text)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Text(stringResource(id = R.string.call_to_action))

                        if (state.isAdvertising) {
                            Button(
                                onClick = { viewModel.stopAdvertise() },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.stop))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.advertise() },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.advertise))
                            }
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Text(stringResource(id = R.string.about))

//                        AdvertiseView(state = state, viewModel = viewModel)

//                        Spacer(modifier = Modifier.size(16.dp))
//
//                        StateView(state = state, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun displayStateLevels(viewModel: ServerViewModel) {
    LevelStatus(0, viewModel)
    LevelStatus(1, viewModel)
    LevelStatus(2, viewModel)
    LevelStatus(3, viewModel)
    LevelStatus(4, viewModel)
    LevelStatus(5, viewModel)
    LevelStatus(6, viewModel)
}
