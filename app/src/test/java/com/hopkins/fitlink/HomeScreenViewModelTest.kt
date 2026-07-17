package com.hopkins.fitlink

import com.hopkins.fitlink.core.data.BleDevice
import com.hopkins.fitlink.core.data.impl.BleRepositoryFake
import com.hopkins.fitlink.home.HomeScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class HomeScreenViewModelTest {
    lateinit var bleRepositoryFake: BleRepositoryFake
    lateinit var viewModel: HomeScreenViewModel

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)

        bleRepositoryFake = BleRepositoryFake(
            scope = testScope
        )

        viewModel = HomeScreenViewModel(
            bleRepository = bleRepositoryFake
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Calling scanForDevices should display correct devices`() = runTest {
        val expectedDevice = BleDevice(
            name = "TestDevice",
            macAddress = "AA:BB:CC:DD:EE:FF"
        )

        viewModel.scanForDevices()

        advanceUntilIdle()

        assertEquals(listOf(expectedDevice), viewModel.devices.value)
    }
}