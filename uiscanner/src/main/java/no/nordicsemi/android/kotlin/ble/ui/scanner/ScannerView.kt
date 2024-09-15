/*
 * Copyright (c) 2023, Nordic Semiconductor
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

package no.nordicsemi.android.kotlin.ble.ui.scanner

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.util.copy
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.permissions.ble.RequireBluetooth
import no.nordicsemi.android.common.permissions.ble.RequireLocation
import no.nordicsemi.android.common.ui.R
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResults
//import no.nordicsemi.android.kotlin.ble.server.ServerViewModel
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.DevicesListView
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.viewmodel.ScannerViewModel
import no.nordicsemi.android.kotlin.ble.ui.scanner.repository.ScanningState
import no.nordicsemi.android.kotlin.ble.ui.scanner.view.internal.FilterView

@Composable
fun HexTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            val cleanedValue = newValue.filter { it.isLetterOrDigit() }.uppercase()
            if (cleanedValue.length <= 8) {
                onValueChange(cleanedValue)
            }
            Log.d("HexTextField", "onValueChange: $cleanedValue")
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerView(
    filters: List<Filter>,
    onScanningStateChanged: (Boolean) -> Unit = {},
    onResult: (BleScanResults) -> Unit,
    filterShape: Shape = MaterialTheme.shapes.small,
    deviceItem: @Composable (BleScanResults) -> Unit = {
        DeviceListItem(it.device.name ?: it.advertisedName, it.device.address)
    },
) {
    RequireBluetooth(
        onChanged = { onScanningStateChanged(it) }
    ) {
        RequireLocation(
            onChanged = { onScanningStateChanged(it) }
        ) { isLocationRequiredAndDisabled ->
            val viewModel = hiltViewModel<ScannerViewModel>()
//            val serverViewModel = hiltViewModel<ServerViewModel>()

            LaunchedEffect(key1 = Unit) {
                print("Launch effect")
                viewModel.setFilters(filters)
            }

            val state by viewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
            val config by viewModel.filterConfig.collectAsStateWithLifecycle()
            val pullRefreshState = rememberPullToRefreshState()
            val scope =  rememberCoroutineScope()

            Column(modifier = Modifier.fillMaxSize()) {
                if (filters.isNotEmpty()) {
                    FilterView(
                        state = config,
                        onChanged = { viewModel.toggleFilter(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = R.color.appBarColor))
                            .padding(horizontal = 16.dp),
                        shape = filterShape,
                    )
                }

                Text("""Hi!
                Before using this app, please do the following:
                1. Open the advertising app
                2. Answer all the questions
                3. Tap on "Advertise"
                4. Tap on "Stop Advertising"
                5. Write down the text in the line that starts with "(0x)", under the "Advertise" button
                6. Input that text in the form below""")

                val (hexValue, setHexValue) = remember { mutableStateOf("00000000") }

                HexTextField(
                    value = hexValue,
                    onValueChange = setHexValue
                )

                Button(onClick = { viewModel.stopScanning()}) {
                    Text("Stop Scanning")
                }

                // Print BleScanResults based on the current state
                if (state is ScanningState.DevicesDiscovered) {
                    val scanResults = (state as ScanningState.DevicesDiscovered)
                    Text(
                        text = "Scan Results:",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    PullToRefreshBox(
                        isRefreshing = state is ScanningState.Loading,
                        onRefresh = {
                            viewModel.refresh()
                            scope.launch {
                                pullRefreshState.animateToHidden()
                            }
                        },
                        state = pullRefreshState,
                        content = {
                            DevicesListView(
                                isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
                                state = state,
                                modifier = Modifier.fillMaxSize(),
                                onClick = {
                                    onResult(it)
                                },
                                deviceItem = { scanResult ->
                                    deviceItem(scanResult) // Use the existing deviceItem composable

                                    // Extract and print specific information from BleScanResult (Optional)
//                                    val deviceName =
//                                        scanResult.device.name ?: scanResult.advertisedName
//                                    val rssi = scanResult.highestRssi
                                    println("Scan Record: ${scanResult.scanResult}")
                                    if (scanResult.scanResult.isNotEmpty()) {
                                        println("User Data: ${scanResult.scanResult[0].scanRecord?.serviceData?.values.toString()}}")
                                    }
                                }
                            )

                        }
                    )
                } else {
                    // Handle other states (Loading, Error) with appropriate messages
                    Text(text = "Scanning: ${state.toString()}")
                }

//                PullToRefreshBox(
//                    isRefreshing = state is ScanningState.Loading,
//                    onRefresh = {
//                        viewModel.refresh()
//                        scope.launch {
//                            pullRefreshState.animateToHidden()
//                        }
//                    },
//                    state = pullRefreshState,
//                    content = {
//                        DevicesListView(
//                            isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
//                            state = state,
//                            modifier = Modifier.fillMaxSize(),
//                            onClick = { onResult(it) },
//                            deviceItem = deviceItem,
//                        )
//                    }
//                )
            }
        }
    }
}
