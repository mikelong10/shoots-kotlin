import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.shoots.shoots_ui.data.model.*
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository
import com.shoots.shoots_ui.ui.group.GroupState
import com.shoots.shoots_ui.ui.group.GroupViewModel

class GroupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var groupRepository: GroupRepository
    private lateinit var screenTimeRepository: ScreenTimeRepository
    private lateinit var viewModel: GroupViewModel

    private val testGroupId = 1

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        groupRepository = mockk()
        screenTimeRepository = mockk()
    }

    @Test
    fun `loadGroupData success sets GroupState Success`() = runTest {
        val group = Group(
            id = testGroupId,
            screen_time_goal = 60,
            code = "GROUPCODE",
            stake = 10.0,
            name = "Test Group",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        val weeklyRankings = listOf(
            Ranking(rank = 1, user = testUser(), time = 120)
        )
        val historicalRankings = listOf(
            UserHistoricalRankings(
                user = testUser(),
                weekRankings = listOf(
                    WeekRanking(rank = 1, week = "2023-W01", time = 100)
                )
            )
        )
        val screenTimes = listOf(
            ScreenTime(id = 1, userId = 100, submitted_time = 60, inserted_at = "2023-01-01T12:00:00Z")
        )
        val members = listOf(testUser())

        coEvery { groupRepository.getGroup(testGroupId) } returns group
        coEvery { groupRepository.getWeeklyRankings(testGroupId) } returns weeklyRankings
        coEvery { groupRepository.getHistoricalRankings(testGroupId) } returns historicalRankings
        coEvery { groupRepository.getGroupScreenTime(testGroupId) } returns screenTimes
        coEvery { groupRepository.getGroupMembers(testGroupId) } returns members

        viewModel = GroupViewModel(groupRepository, testGroupId, screenTimeRepository)
        advanceUntilIdle()

        val state = viewModel.groupState.value
        assertTrue(state is GroupState.Success)
        state as GroupState.Success

        assertEquals(group, state.group)
        assertEquals(weeklyRankings, state.weeklyRankings)
        assertEquals(historicalRankings, state.historicalRankings)
        assertEquals(screenTimes, state.screenTimes)
        assertEquals(members, state.members)
    }

    @Test
    fun `loadGroupData failure sets GroupState Error`() = runTest {
        coEvery { groupRepository.getGroup(testGroupId) } throws Exception("Network error")

        viewModel = GroupViewModel(groupRepository, testGroupId, screenTimeRepository)
        advanceUntilIdle()

        val state = viewModel.groupState.value
        assertTrue(state is GroupState.Error)
        (state as GroupState.Error).message.contains("Network error")
    }

    @Test
    fun `joinGroup success reloads data`() = runTest {
        val group = Group(
            id = testGroupId,
            screen_time_goal = 60,
            code = "JOINCODE",
            stake = 5.0,
            name = "Joined Group",
            inserted_at = "2023-01-02T00:00:00Z",
            updated_at = null
        )

        // Initial load fails so that we know after join it loads again.
        coEvery { groupRepository.getGroup(testGroupId) } throws Exception("Not joined yet")

        viewModel = GroupViewModel(groupRepository, testGroupId, screenTimeRepository)
        advanceUntilIdle()
        assertTrue(viewModel.groupState.value is GroupState.Error)

        // After joining, now return success on reload
        coEvery { groupRepository.joinGroup("SOME_CODE") } returns group
        coEvery { groupRepository.getGroup(testGroupId) } returns group
        coEvery { groupRepository.getWeeklyRankings(testGroupId) } returns emptyList()
        coEvery { groupRepository.getHistoricalRankings(testGroupId) } returns emptyList()
        coEvery { groupRepository.getGroupScreenTime(testGroupId) } returns emptyList()
        coEvery { groupRepository.getGroupMembers(testGroupId) } returns emptyList()

        viewModel.joinGroup("SOME_CODE")
        advanceUntilIdle()

        val state = viewModel.groupState.value
        assertTrue(state is GroupState.Success)
        (state as GroupState.Success).group == group
    }

    @Test
    fun `joinGroup failure sets Error`() = runTest {
        coEvery { groupRepository.getGroup(testGroupId) } throws Exception("Not joined yet")

        viewModel = GroupViewModel(groupRepository, testGroupId, screenTimeRepository)
        advanceUntilIdle()
        assertTrue(viewModel.groupState.value is GroupState.Error)

        coEvery { groupRepository.joinGroup("BAD_CODE") } throws Exception("Join failed")

        viewModel.joinGroup("BAD_CODE")
        advanceUntilIdle()

        val state = viewModel.groupState.value
        assertTrue(state is GroupState.Error)
        assertTrue((state as GroupState.Error).message.contains("Join failed"))
    }

    @Test
    fun `toggleHistoricalView flips the boolean`() = runTest {
        coEvery { groupRepository.getGroup(testGroupId) } throws Exception("No data")
        viewModel = GroupViewModel(groupRepository, testGroupId, screenTimeRepository)
        advanceUntilIdle()

        assertEquals(false, viewModel.isHistoricalView.value)

        viewModel.toggleHistoricalView()
        assertEquals(true, viewModel.isHistoricalView.value)

        viewModel.toggleHistoricalView()
        assertEquals(false, viewModel.isHistoricalView.value)
    }

    private fun testUser() = User(
        id = 100,
        email = "testuser@example.com",
        profile_picture = "pic",
        name = "Test User",
        inserted_at = "2023-01-01T00:00:00Z",
        updated_at = null
    )
}
