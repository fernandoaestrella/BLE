package no.nordicsemi.android.kotlin.ble.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.errors.DeviceDisconnectedException
import no.nordicsemi.android.kotlin.ble.test.utils.BlinkySpecifications
import no.nordicsemi.android.kotlin.ble.test.utils.TestAddressProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadFromDisconnectedDeviceTest {

    private val service = BlinkySpecifications.UUID_SERVICE_DEVICE
    private val char = BlinkySpecifications.UUID_LED_CHAR

    private val address = TestAddressProvider.address

    private val scope = CoroutineScope(UnconfinedTestDispatcher())

    @Test
    fun whenReadAfterDisconnectShouldThrow() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val gatt = ClientBleGatt.connect(context, address, scope)
        val services = gatt.discoverServices()
        val char = services.findService(service)?.findCharacteristic(char)
        gatt.disconnect()          // Simulate a device disconnection
        Assert.assertThrows(DeviceDisconnectedException::class.java) {
            runBlocking {
                char?.read()!!     //Issue: stuck here forever
            }
        }
    }
}
