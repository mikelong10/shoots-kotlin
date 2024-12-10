package com.shoots.shoots_ui

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.shoots.shoots_ui.data.model.*
import com.shoots.shoots_ui.data.remote.ApiService
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository

class ScreenTimeRepoTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var repository: ScreenTimeRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        repository = ScreenTimeRepository(apiService)
    }

    @Test
    fun `enterScreenTime returns ScreenTime on success`() = runTest {
        val screenTime = ScreenTime(
            id = 1,
            userId = 100,
            submitted_time = 120,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        val response = ScreenTimeResponse(
            success = true,
            message = "Success",
            data = screenTime
        )

        coEvery { apiService.enterScreenTime(CreateScreenTimeRequest(120)) } returns response

        val result = repository.enterScreenTime(120)
        assertEquals(screenTime, result)
    }

    @Test
    fun `enterScreenTime throws exception on failure`() = runTest {
        val response = ScreenTimeResponse(
            success = false,
            message = "Invalid screen time value",
            data = ScreenTime(
                id = 0,
                userId = 0,
                submitted_time = 0,
                inserted_at = "2023-01-01T00:00:00Z"
            )
        )

        coEvery { apiService.enterScreenTime(CreateScreenTimeRequest(9999)) } returns response

        try {
            repository.enterScreenTime(9999)
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Invalid screen time value"))
        }
    }

    @Test
    fun `getSelfScreenTime returns ScreenTime on success`() = runTest {
        val screenTime = ScreenTime(
            id = 2,
            userId = 100,
            submitted_time = 60,
            inserted_at = "2023-01-02T00:00:00Z"
        )
        val response = ScreenTimeResponse(
            success = true,
            message = "Success",
            data = screenTime
        )

        coEvery { apiService.getSelfScreenTime() } returns response

        val result = repository.getSelfScreenTime()
        assertEquals(screenTime, result)
    }

    @Test
    fun `getSelfScreenTime throws exception on failure`() = runTest {
        val response = ScreenTimeResponse(
            success = false,
            message = "No screen time found",
            data = ScreenTime(
                id = 0,
                userId = 0,
                submitted_time = 0,
                inserted_at = "2023-01-01T00:00:00Z"
            )
        )

        coEvery { apiService.getSelfScreenTime() } returns response

        try {
            repository.getSelfScreenTime()
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("No screen time found"))
        }
    }
}
