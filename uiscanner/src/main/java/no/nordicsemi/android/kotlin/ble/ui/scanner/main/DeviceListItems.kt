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

package no.nordicsemi.android.kotlin.ble.ui.scanner.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.kotlin.ble.ui.scanner.repository.ScanningState
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResults
import no.nordicsemi.android.kotlin.ble.ui.scanner.R
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.viewmodel.ScannerViewModel
import kotlin.math.pow

@Suppress("FunctionName")
internal fun LazyListScope.DeviceListItems(
    devices: ScanningState.DevicesDiscovered,
    onClick: (BleScanResults) -> Unit,
    deviceView: @Composable (BleScanResults) -> Unit,
    viewModel: ScannerViewModel
) {
    val bondedDevices = devices.bonded
    val discoveredDevices = devices.notBonded

//    if (bondedDevices.isNotEmpty()) {
//        item {
//            Text(
//                text = stringResource(id = R.string.bonded_devices),
//                style = MaterialTheme.typography.titleSmall,
//                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
//            )
//        }
//        items(bondedDevices.size) {
//            ClickableDeviceItem(bondedDevices[it], onClick, deviceView, viewModel)
//        }
//    }

    if (discoveredDevices.isNotEmpty()) {
        item {
//            Text(
//                text = stringResource(id = R.string.discovered_devices),
//                style = MaterialTheme.typography.titleSmall,
//                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
//            )
        }

        items(discoveredDevices.size) {
            ClickableDeviceItem(discoveredDevices[it], onClick, deviceView, viewModel)
        }
    }
}

internal fun countMatches(otherUserDataByteArray: ByteArray, viewModel: ScannerViewModel): Int {
    val myUserDataBinaryString = byteArrayToBinaryString(viewModel.getUserData(0))
    val otherUserDataBinaryString = byteArrayToBinaryString(otherUserDataByteArray)
    var matchesCount = 0
    var bitIndex = 0

    for (otherUserBitChar in otherUserDataBinaryString) {
        // In order to only look at the first 14 bits
        if (bitIndex < 14) {
            // If we are now looking at any bit placed in an even position (0, 2, 4, 6)
            if (bitIndex % 2 == 0) {
                // If this user's bit is 1 and the other user's next bit is 1
                if (myUserDataBinaryString[bitIndex] == '1' && otherUserDataBinaryString[bitIndex + 1] == '1') {
                    matchesCount++
                }
            // Else, if we are looking at a bit placed in an odd position (1, 3, 5, 7)
            } else {
                // If this user's bit is 1 and the other user's previous bit is 1
                if (myUserDataBinaryString[bitIndex] == '1' && otherUserDataBinaryString[bitIndex - 1] == '1') {
                    matchesCount++
                }
            }
        }

        bitIndex++
    }

    return matchesCount
}

fun hexStringToByteArray(hexString: String): ByteArray {
    val byteArray = ByteArray(hexString.length / 2)
    for (i in hexString.indices step 2) {
        val highNibble = Character.digit(hexString[i], 16) shl 4
        val lowNibble = Character.digit(hexString[i + 1], 16)
        byteArray[i / 2] = (highNibble or lowNibble).toByte()
    }
    return byteArray
}

fun byteArrayToHexString(byteArray: ByteArray): String {
    return byteArray.joinToString("") { it.toString(16).padStart(2, '0') }
}

fun byteArrayToBinaryString(byteArray: ByteArray): String {

    val booleanString = StringBuilder()
    for (byte in byteArray) {
        for (bitIndex in 7 downTo 0) {
            val bit = (byte.toInt() shr bitIndex) and 1 != 0
            booleanString.append(if (bit) '1' else '0')
        }
    }
    return booleanString.toString()

//    return byteArray.joinToString("") { it.toString(2).padStart(8, '0') }
}

@Composable
internal fun describeMatches(otherUserDataByteArray: ByteArray, viewModel: ScannerViewModel): String {
    val myUserDataBinaryString = byteArrayToBinaryString(viewModel.getUserData(0))
    val otherUserDataBinaryString = byteArrayToBinaryString(otherUserDataByteArray)
    val needsList = listOf(stringResource(R.string.match_supply_1), stringResource(R.string.match_supply_2), stringResource(R.string.match_supply_3), stringResource(R.string.match_supply_4), stringResource(R.string.match_supply_5), stringResource(R.string.match_supply_6), stringResource(R.string.match_supply_7))
    var matchAnalysis = ""
    var bitIndex = 0

    for (otherUserBitChar in otherUserDataBinaryString) {
        // In order to only look at the first 14 bits
        if (bitIndex < 14) {
            // If we are now looking at any bit placed in an even position (0, 2, 4, 6)
            if (bitIndex % 2 == 0) {
                // If this user's bit is 1 and the other user's next bit is 1. This means this user is requesting something that the other user can provide
                if (myUserDataBinaryString[bitIndex] == '1' && otherUserDataBinaryString[bitIndex + 1] == '1') {
                    matchAnalysis += stringResource(R.string.they_can) + needsList[bitIndex / 2] + stringResource(
                        R.string.for_you
                    )
                }
                // Else, if we are looking at a bit placed in an odd position (1, 3, 5, 7). This means this user can provide something the other user is requesting
            } else {
                // If this user's bit is 1 and the other user's previous bit is 1
                if (myUserDataBinaryString[bitIndex] == '1' && otherUserDataBinaryString[bitIndex - 1] == '1') {
                    matchAnalysis += stringResource(R.string.you_can) + needsList[bitIndex / 2] + stringResource(
                        R.string.for_them
                    )
                }
            }
        }

        bitIndex++
    }

    return matchAnalysis
}

@Composable
private fun ClickableDeviceItem(
    device: BleScanResults,
    onClick: (BleScanResults) -> Unit,
    deviceView: @Composable (BleScanResults) -> Unit,
    viewModel: ScannerViewModel
) {


    if (device.scanResult.isNotEmpty()) {
        val userDataString = device.scanResult[0].scanRecord?.serviceData.toString().substringAfter("=(0x) ").substringBefore("}").replace(":","")

        MatchDescription(userDataString, viewModel)
    }
    Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable { onClick(device) }
        .padding(8.dp)
    ) {
//        Text(text = device.scanResult[0].scanRecord?.serviceData.toString())

//        deviceView(device)
    }
}

@Composable
internal fun MatchDescription(
    otherUserDataString: String,
    viewModel: ScannerViewModel
) {
    val matchCount = countMatches(hexStringToByteArray(otherUserDataString), viewModel)

//    Log.d(
//        "progression",
//        "hexstring" + otherUserDataString + "\nbytearray: " + byteArrayToBinaryString(
//            hexStringToByteArray(otherUserDataString)
//        ) + "\noutput user description: " + outputUserDescription(
//            hexStringToByteArray(
//                otherUserDataString
//            )
//        )
//    )
//        Column with a clear color background for each scan: green if matchCount is above 9, yellow if between 5 and 9, red if below 5
    val color = when {
        matchCount > 9 -> Color.Green
        matchCount in 5..9 -> Color.Yellow
        else -> Color.Red
    }

    // Changes text color to be black if matchCount is above 9, black if between 5 and 9, white if below 5
    val textColor = when {
        matchCount > 9 -> Color.Black
        matchCount in 5..9 -> Color.Black
        else -> Color.White
    }

    Column(modifier = Modifier.background(color)) {
        Text(
            text = stringResource(R.string.number_of_matches_with_other_user) + "$matchCount" + stringResource(
                R.string.out_of_14
            ),
            color = textColor
        )
        Text(
            text = stringResource(R.string.this_user_looks_like_this),
            color = textColor
        )
        outputVisualUserDescription(otherUserDataByteArray = hexStringToByteArray(
            otherUserDataString
        ))
        Text(
            text = outputUserDescription(
                hexStringToByteArray(
                    otherUserDataString
                )
            ), color = textColor
        )

        if (matchCount > 0) {
            // Describe matches
            Text(
                text = stringResource(R.string.match_intro) + describeMatches(
                    hexStringToByteArray(otherUserDataString),
                    viewModel
                ), color = textColor
            )
        }
        Text(text = "User Data: $otherUserDataString", color = textColor)
    }
}

fun byteArrayToBitArray(byteArray: ByteArray): List<Boolean> {
    val bitArray = mutableListOf<Boolean>()
    // Convert each byte to a bit array maintaining the order of the bits inside the bytes
    for (bitChar in byteArrayToBinaryString(byteArray)) {
        if (bitChar == '1') {
            bitArray.add(true)
        } else {
            bitArray.add(false)
        }
    }
    return bitArray
}

@Composable
private fun outputVisualUserDescription(otherUserDataByteArray: ByteArray): Unit {
    val image_male = painterResource(R.drawable.male)
    val image_female = painterResource(R.drawable.female)
    val image_small = painterResource(R.drawable.small)
    val image_tall = painterResource(R.drawable.tall)
    val image_young = painterResource(R.drawable.young)
    val image_old = painterResource(R.drawable.old)
    val image_hair_male_shaved = painterResource(R.drawable.male_shaved)
    val image_hair_male_bearded = painterResource(R.drawable.male_bearded)
    val image_hair_female_short = painterResource(R.drawable.short_hair)
    val image_hair_female_long = painterResource(R.drawable.long_hair)
    val image_no_glasses = painterResource(R.drawable.no_glasses)
    val image_has_glasses = painterResource(R.drawable.glasses)

    var image_gender: Painter? = null
    var image_height: Painter? = null
    var image_age: Painter? = null
    var image_hair_male: Painter? = null
    var image_hair_female: Painter? = null
    var image_glasses: Painter? = null
    var image_top_color: Painter? = null
    var image_bottom_color: Painter? = null

    var otherUserLooksLikeMan = false
    // creates an array of bits from the input array
    val bitArray = byteArrayToBitArray(otherUserDataByteArray)
    var bitIndex = 0

    Log.d("outputUserDescription", "bitArray: $bitArray")
    for (bit in bitArray) {
        when (bitIndex) {
            14 -> {
                // If this user's bit is 1
                if (bit) {
                    otherUserLooksLikeMan = true
                    image_gender = image_male
                } else {
                    image_gender = image_female
                }
            }
            15 -> {
                if (bit) {
                    image_height = image_tall
                } else {
                    image_height = image_small
                }
            }
            16 -> {
                if (bit) {
                    image_age = image_old
                } else {
                    image_age = image_young
                }
            }
            17 -> {
                if (otherUserLooksLikeMan) {
                    if (bit) {
                        image_hair_male = image_hair_male_bearded
                    } else {
                        image_hair_male = image_hair_male_shaved
                    }
                    // Else if the other user looks like a woman
                } else {
                    if (bit) {
                        image_hair_female = image_hair_female_long
                    } else {
                        image_hair_female = image_hair_female_short
                    }
                }
            }
            18 -> {
                if (bit) {
                    image_glasses = image_has_glasses
                } else {
                    image_glasses = image_no_glasses
                }
            }
        }

        bitIndex++
    }

    // Represents next 4 bits as an int
    val otherUserTopColorBitArray = mutableListOf<Boolean>(bitArray[19], bitArray[20], bitArray[21], bitArray[22])

    //    Convert bit array to integer
    var otherUserTopColorInt = convertBitArrayToInt(otherUserTopColorBitArray)

//    Convert integer to color
    when (otherUserTopColorInt) {
        0 -> image_top_color = painterResource(R.drawable.top_none)
        1 -> image_top_color = painterResource(R.drawable.top_white)
        2 -> image_top_color = painterResource(R.drawable.top_black)
        3 -> image_top_color = painterResource(R.drawable.top_gray)
        4 -> image_top_color = painterResource(R.drawable.top_brown)
        5 -> image_top_color = painterResource(R.drawable.top_red)
        6 -> image_top_color = painterResource(R.drawable.top_green)
        7 -> image_top_color = painterResource(R.drawable.top_blue)
        8 -> image_top_color = painterResource(R.drawable.top_purple)
        9 -> image_top_color = painterResource(R.drawable.top_orange)
        10 -> image_top_color = painterResource(R.drawable.top_yellow)
        else -> {}
    }

    // Represent next 3 bit as a boolean array
    val otherUserBottomColorBitArray = mutableListOf<Boolean>(bitArray[23], bitArray[24], bitArray[25])

    // Convert bit array to integer
    var otherUserBottomColorInt = convertBitArrayToInt(otherUserBottomColorBitArray)

    // Convert integer to color
    when (otherUserBottomColorInt) {
        0 -> image_bottom_color = painterResource(R.drawable.bottom_none)
        1 -> image_bottom_color = painterResource(R.drawable.bottom_white)
        2 -> image_bottom_color = painterResource(R.drawable.bottom_black)
        3 -> image_bottom_color = painterResource(R.drawable.bottom_gray)
        4 -> image_bottom_color = painterResource(R.drawable.bottom_brown)
        5 -> image_bottom_color = painterResource(R.drawable.bottom_blue)
        6 -> image_bottom_color = painterResource(R.drawable.bottom_other)
        else -> {}
    }

    if (image_gender != null) {
        Row() {
            Image(
                painter = image_gender,
                contentDescription = null
            )
            Image(
                painter = image_height!!,
                contentDescription = null
            )
            Image(
                painter = image_age!!,
                contentDescription = null
            )
            if (otherUserLooksLikeMan) {
                Image(
                    painter = image_hair_male!!,
                    contentDescription = null
                )
            } else {
                Image(
                    painter = image_hair_female!!,
                    contentDescription = null
                )
            }
            Image(
                painter = image_glasses!!,
                contentDescription = null
            )
            if (image_top_color != null) {
                Image(
                    painter = image_top_color!!,
                    contentDescription = null
                )
            }
            if (image_bottom_color != null) {
                Image(
                    painter = image_bottom_color!!,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun outputUserDescription(otherUserDataByteArray: ByteArray): String {
    var outputString = ""
    var otherUserLooksLikeMan = false
    // creates an array of bits from the input array
    val bitArray = byteArrayToBitArray(otherUserDataByteArray)
    var bitIndex = 0

    for (bit in bitArray) {
        when (bitIndex) {
            14 -> {
                // If this user's bit is 1
                if (bit) {
                    otherUserLooksLikeMan = true
                    outputString += stringResource(R.string.man)
                } else {
                    outputString += stringResource(R.string.woman)
                }

                outputString += "\n"
            }
            15 -> {
                // if the other user looks like a man
                if (otherUserLooksLikeMan) {
                    // If the other user's bit is true
                    if (bit) {
                        outputString += stringResource(R.string.male_tall)
                    } else {
                        outputString += stringResource(R.string.male_short)
                    }
                    // Else if the other user looks like a woman
                } else {
                    // If the other user's bit is true
                    if (bit) {
                        outputString += stringResource(R.string.female_tall)
                    } else {
                        outputString += stringResource(R.string.female_short)
                    }
                }

                outputString += "\n"
            }
            16 -> {
                if (otherUserLooksLikeMan) {
                    // If the other user's bit is 1
                    if (bit) {
                        outputString += stringResource(R.string.male_old)
                    } else {
                        outputString += stringResource(R.string.male_young)
                    }
                    // Else if the other user looks like a woman
                } else {
                    if (bit) {
                        outputString += stringResource(R.string.female_old)
                    } else {
                        outputString += stringResource(R.string.female_young)
                    }
                }

                outputString += "\n"
            }
            17 -> {
                if (otherUserLooksLikeMan) {
                    if (bit) {
                        outputString += stringResource(R.string.male_has_facial_hair)
                    } else {
                        outputString += stringResource(R.string.male_does_not_have_facial_hair)
                    }
                    // Else if the other user looks like a woman
                } else {
                    if (bit) {
                        outputString += stringResource(R.string.female_long_hair)
                    } else {
                        outputString += stringResource(R.string.female_short_hair)
                    }
                }

                outputString += "\n"
            }
            18 -> {
                if (bit) {
                    outputString += stringResource(R.string.wearing_glasses)
                } else {
                    outputString += stringResource(R.string.not_wearing_glasses)
                }

                outputString += "\n"
            }
        }
        bitIndex++
    }

    // Represents next 4 bits as an int
    val otherUserTopColorBitArray = mutableListOf<Boolean>(bitArray[19], bitArray[20], bitArray[21], bitArray[22])

    //    Convert bit array to integer
    var otherUserTopColorInt = convertBitArrayToInt(otherUserTopColorBitArray)

    outputString += stringResource(R.string.top_color)
//    Convert integer to color
    when (otherUserTopColorInt) {
        0 -> outputString += stringResource(R.string.none)
        1 -> outputString += stringResource(R.string.white)
        2 -> outputString += stringResource(R.string.black)
        3 -> outputString += stringResource(R.string.gray)
        4 -> outputString += stringResource(R.string.brown)
        5 -> outputString += stringResource(R.string.red)
        6 -> outputString += stringResource(R.string.green)
        7 -> outputString += stringResource(R.string.blue)
        8 -> outputString += stringResource(R.string.purple)
        9 -> outputString += stringResource(R.string.orange)
        10 -> outputString += stringResource(R.string.yellow)
        else -> outputString += stringResource(R.string.invalid_code)
    }

    // Represent next 3 bit as a boolean array
    val otherUserBottomColorBitArray = mutableListOf<Boolean>(bitArray[23], bitArray[24], bitArray[25])

    // Convert bit array to integer
    var otherUserBottomColorInt = convertBitArrayToInt(otherUserBottomColorBitArray)

    outputString += stringResource(R.string.bottom_color)
    // Convert integer to color
    when (otherUserBottomColorInt) {
        0 -> outputString += stringResource(R.string.none)
        1 -> outputString += stringResource(R.string.white)
        2 -> outputString += stringResource(R.string.black)
        3 -> outputString += stringResource(R.string.gray)
        4 -> outputString += stringResource(R.string.brown)
        5 -> outputString += stringResource(R.string.blue)
        6 -> outputString += stringResource(R.string.undefined)
        else -> outputString += stringResource(R.string.invalid_code)
    }

//    Log.d("outputUserDescription", "outputString: $outputString")
    return outputString
}

private fun convertBitArrayToInt(inputBitArray: MutableList<Boolean>): Int {
    var outputInt = 0
    for (i in inputBitArray.size - 1 downTo 0) {
        if (inputBitArray[i]) {
            outputInt += (2.0).pow(inputBitArray.size - 1 - i).toInt()
        }
    }
    return outputInt
}
