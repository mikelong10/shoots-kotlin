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
import com.shoots.shoots_ui.data.repository.GroupRepository

class GroupRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var repository: GroupRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        repository = GroupRepository(apiService)
    }

    @Test
    fun `listGroups returns list on success`() = runTest {
        val groups = listOf(
            Group(
                id = 1,
                screen_time_goal = 60,
                code = "G1CODE",
                stake = 10.0,
                name = "Group 1",
                inserted_at = "2023-01-01T00:00:00Z",
                updated_at = "2023-01-02T00:00:00Z"
            ),
            Group(
                id = 2,
                screen_time_goal = 120,
                code = "G2CODE",
                stake = 5.0,
                name = "Group 2",
                inserted_at = "2023-01-01T00:00:00Z",
                updated_at = null
            )
        )
        val response = GroupsResponse(success = true, message = "Success", data = groups)

        coEvery { apiService.listGroups() } returns response

        val result = repository.listGroups()
        assertEquals(groups, result)
    }

    @Test
    fun `listGroups throws exception on failure`() = runTest {
        val response = GroupsResponse(success = false, message = "Error occurred", data = emptyList())
        coEvery { apiService.listGroups() } returns response

        try {
            repository.listGroups()
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Error occurred"))
        }
    }

    @Test
    fun `getGroup returns a group on success`() = runTest {
        val group = Group(
            id = 1,
            screen_time_goal = 30,
            code = "G1CODE",
            stake = 2.0,
            name = "Test Group",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        val response = GroupResponse(success = true, message = "Success", data = group)
        coEvery { apiService.getGroup(1) } returns response

        val result = repository.getGroup(1)
        assertEquals(group, result)
    }

    @Test
    fun `getGroup throws exception on failure`() = runTest {
        val response = GroupResponse(
            success = false,
            message = "Not found",
            data = Group(
                id = 0,
                screen_time_goal = 0,
                code = "",
                stake = 0.0,
                name = "",
                inserted_at = "2023-01-01T00:00:00Z",
                updated_at = null
            )
        )
        coEvery { apiService.getGroup(99) } returns response

        try {
            repository.getGroup(99)
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Not found"))
        }
    }

    @Test
    fun `createGroup returns a group on success`() = runTest {
        val createdGroup = Group(
            id = 10,
            screen_time_goal = 60,
            code = "NEWCODE",
            stake = 10.0,
            name = "New Group",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        val response = GroupResponse(success = true, message = "Success", data = createdGroup)
        coEvery { apiService.createGroup(CreateGroupRequest("New Group", 60, 10.0)) } returns response

        val result = repository.createGroup("New Group", 60, 10.0)
        assertEquals(createdGroup, result)
    }

    @Test
    fun `createGroup throws exception on failure`() = runTest {
        val response = GroupResponse(
            success = false,
            message = "Creation failed",
            data = Group(
                id = 0,
                screen_time_goal = 0,
                code = "",
                stake = 0.0,
                name = "",
                inserted_at = "2023-01-01T00:00:00Z",
                updated_at = null
            )
        )
        coEvery { apiService.createGroup(any()) } returns response

        try {
            repository.createGroup("Fail Group", 100, 20.0)
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Creation failed"))
        }
    }

    @Test
    fun `joinGroup returns a group on success`() = runTest {
        val joinedGroup = Group(
            id = 3,
            screen_time_goal = 90,
            code = "JOINCODE",
            stake = 5.0,
            name = "Joined Group",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        val response = GroupResponse(success = true, message = "Success", data = joinedGroup)
        coEvery { apiService.joinGroup(JoinGroupRequest("ABC123")) } returns response

        val result = repository.joinGroup("ABC123")
        assertEquals(joinedGroup, result)
    }

    @Test
    fun `joinGroup throws exception on failure`() = runTest {
        val response = GroupResponse(
            success = false,
            message = "Invalid code",
            data = Group(
                id = 0,
                screen_time_goal = 0,
                code = "",
                stake = 0.0,
                name = "",
                inserted_at = "2023-01-01T00:00:00Z",
                updated_at = null
            )
        )
        coEvery { apiService.joinGroup(JoinGroupRequest("XYZ")) } returns response

        try {
            repository.joinGroup("XYZ")
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Invalid code"))
        }
    }

    @Test
    fun `createInvite returns invite code on success`() = runTest {
        val response = ApiResponse(success = true, message = "Success", data = "INVITE_CODE")
        coEvery { apiService.createInvite(1) } returns response

        val result = repository.createInvite(1)
        assertEquals("INVITE_CODE", result)
    }

    @Test
    fun `createInvite throws exception on failure`() = runTest {
        val response = ApiResponse(success = false, message = "Unable to create invite", data = "")
        coEvery { apiService.createInvite(1) } returns response

        try {
            repository.createInvite(1)
            assertTrue("Exception not thrown", false)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Unable to create invite"))
        }
    }
}
