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

package no.nordicsemi.android.kotlin.ble.advertiser

import android.Manifest
import android.bluetooth.le.AdvertiseData
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.kotlin.ble.advertiser.callback.BleAdvertisingStatus
import no.nordicsemi.android.kotlin.ble.advertiser.callback.BleAdvertisingEvent
import no.nordicsemi.android.kotlin.ble.advertiser.callback.OnAdvertisingSetStarted
import no.nordicsemi.android.kotlin.ble.advertiser.callback.OnAdvertisingSetStopped
import no.nordicsemi.android.kotlin.ble.core.advertiser.BleAdvertisingConfig
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.advertiser.BleAdvertisingData
import no.nordicsemi.android.kotlin.ble.core.advertiser.ServiceData
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.mock.MockEngine
import java.util.UUID

/**
 * Advertiser class which provides BLE advertising functionality.
 * It is wrapper around native Android API.
 *
 * @see [BluetoothLeAdvertiser](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeAdvertiser)
 */
interface BleAdvertiser {


    /**
     * Starts BLE advertising.
     *
     * @param config Advertising configuration [BleAdvertisingConfig].
     * @return [Flow] which emits advertisement process status changes ([BleAdvertisingEvent]).
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE])
    fun advertise(config: BleAdvertisingConfig): Flow<BleAdvertisingEvent>

    /**
     * Starts BLE advertising locally on a device. The devices should be returned by NordicScanner
     * during scanning.
     *
     * @param config Advertising configuration [BleAdvertisingConfig].
     * @param mock [MockServerDevice] which will advertise locally on a device.
     * @return which emits advertisement process status changes ([BleAdvertisingEvent]).
     */
    fun advertise(config: BleAdvertisingConfig, mock: MockServerDevice): Flow<BleAdvertisingEvent> {
        return callbackFlow {

////            var existingConfig: BleAdvertisingConfig = config
//            val byteArray = "aum".toByteArray()
//            val uuidString = "00001234-0000-1000-8000-00805f9b34fb"
//            val parcelUuid = ParcelUuid(UUID.fromString(uuidString))
//
//            val existingConfig = config// ... (get your existing BleAdvertisingConfig instance)
//
//            val newAdvertiseData = BleAdvertisingData() // Assuming BleAdvertisingData has a default constructor
//
//            // Copy existing properties
//            newAdvertiseData.serviceUuid = existingConfig.advertiseData?.serviceUuid
//
//            // Modify service data
//            newAdvertiseData.serviceData = List(1, ServiceData(parcelUuid, DataByteArray(byteArray))

            // Set the new advertiseData on the existing configuration
            MockEngine.advertiseServer(mock, config)

            trySend(
                OnAdvertisingSetStarted(null, 0, BleAdvertisingStatus.ADVERTISE_SUCCESS)
            )

            awaitClose {
                MockEngine.stopAdvertising(mock)
                trySend(OnAdvertisingSetStopped(null))
            }
        }
    }

    companion object {

        /**
         * Creates an instance of [BleAdvertiser]. The implementation differs based on Android
         * version. Limited functionality is available prior to Android O.
         *
         * @param context An application context.
         * @return Instance of [BleAdvertiser].
         */
        fun create(context: Context): BleAdvertiser {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                BleAdvertiserOreo(context)
            } else {
                BleAdvertiserLegacy(context)
            }
        }
    }
}
