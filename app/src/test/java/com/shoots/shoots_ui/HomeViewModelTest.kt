import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.model.ScreenTime
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository
import com.shoots.shoots_ui.ui.home.HomeState
import com.shoots.shoots_ui.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var groupRepository: GroupRepository
    private lateinit var screenTimeRepository: ScreenTimeRepository
    private lateinit var viewModel: HomeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        groupRepository = mockk()
        screenTimeRepository = mockk()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadHomeData success sets HomeState Success`() = runTest {
        val allGroups = listOf(
            Group(id = 1, name = "Group 1", screen_time_goal = 60, stake = 10.0, code = "G1CODE", inserted_at = "2023-01-01T00:00:00Z", updated_at = null),
            Group(id = 2, name = "Group 2", screen_time_goal = 120, stake = 5.0, code = "G2CODE", inserted_at = "2023-01-01T00:00:00Z", updated_at = null)
        )
        val myGroups = listOf(
            Group(id = 2, name = "Group 2", screen_time_goal = 120, stake = 5.0, code = "G2CODE", inserted_at = "2023-01-01T00:00:00Z", updated_at = null)
        )
        val myGroupIds = myGroups.map { it.id }.toSet()
        val availableGroups = allGroups.filterNot { it.id in myGroupIds }

        val screenTime = ScreenTime(id = 1, userId = 100, submitted_time = 60, inserted_at = "2023-01-01T12:00:00Z")

        coEvery { groupRepository.listGroups() } returns allGroups
        coEvery { groupRepository.listMyGroups() } returns myGroups
        coEvery { screenTimeRepository.getSelfScreenTime() } returns screenTime

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Success)
        state as HomeState.Success
        assertEquals(availableGroups, state.groups)
        assertEquals(myGroups, state.myGroups)
        assertEquals(screenTime, state.screenTime)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadHomeData failure sets HomeState Error`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } throws Exception("Network error")
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Error)
        (state as HomeState.Error).message.contains("Network error")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `show and hide dialog states`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        assertFalse(viewModel.isCreateGroupDialogVisible.value)
        viewModel.showCreateGroupDialog()
        assertTrue(viewModel.isCreateGroupDialogVisible.value)
        viewModel.hideCreateGroupDialog()
        assertFalse(viewModel.isCreateGroupDialogVisible.value)

        assertFalse(viewModel.isJoinGroupDialogVisible.value)
        viewModel.showJoinGroupDialog()
        assertTrue(viewModel.isJoinGroupDialogVisible.value)
        viewModel.hideJoinGroupDialog()
        assertFalse(viewModel.isJoinGroupDialogVisible.value)

        assertFalse(viewModel.isEnterScreenTimeDialogVisible.value)
        viewModel.showEnterScreenTimeDialog()
        assertTrue(viewModel.isEnterScreenTimeDialogVisible.value)
        viewModel.hideEnterScreenTimeDialog()
        assertFalse(viewModel.isEnterScreenTimeDialogVisible.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `createGroup success reloads data`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        val dummyGroup = Group(
            id = 10,
            name = "Dummy Group",
            screen_time_goal = 60,
            stake = 10.0,
            code = "DUMMYCODE",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { groupRepository.createGroup("New Group", 60, 10.0) } returns dummyGroup

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.createGroup("New Group", 60, 10.0)
        advanceUntilIdle()

        coVerify { groupRepository.createGroup("New Group", 60, 10.0) }
        assertTrue(viewModel.homeState.value is HomeState.Success)
        assertFalse(viewModel.isCreateGroupDialogVisible.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `createGroup failure sets Error`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { groupRepository.createGroup("Fail Group", 100, 20.0) } throws Exception("Create failed")

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.createGroup("Fail Group", 100, 20.0)
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Error && state.message.contains("Create failed"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joinGroup success reloads data`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        val dummyGroup = Group(
            id = 10,
            name = "Dummy Group",
            screen_time_goal = 60,
            stake = 10.0,
            code = "DUMMYCODE",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { groupRepository.joinGroup("CODE123") } returns dummyGroup

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.joinGroup("CODE123")
        advanceUntilIdle()

        coVerify { groupRepository.joinGroup("CODE123") }
        assertTrue(viewModel.homeState.value is HomeState.Success)
        assertFalse(viewModel.isJoinGroupDialogVisible.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joinGroup failure sets Error`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { groupRepository.joinGroup("BADCODE") } throws Exception("Join failed")

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.joinGroup("BADCODE")
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Error && state.message.contains("Join failed"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `enterScreenTime success reloads data`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        val expectedScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 100,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { screenTimeRepository.enterScreenTime(120) } returns expectedScreenTime

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.enterScreenTime(120)
        advanceUntilIdle()

        coVerify { screenTimeRepository.enterScreenTime(120) }
        assertTrue(viewModel.homeState.value is HomeState.Success)
        assertFalse(viewModel.isEnterScreenTimeDialogVisible.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `enterScreenTime failure sets Error`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )
        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime
        coEvery { screenTimeRepository.enterScreenTime(9999) } throws Exception("Invalid input")

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.enterScreenTime(9999)
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Error && state.message.contains("Invalid input"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `enter negative screen time sets Error`() = runTest {
        val dummyScreenTime = ScreenTime(
            id = 0,
            userId = 0,
            submitted_time = 0,
            inserted_at = "2023-01-01T00:00:00Z"
        )

        coEvery { groupRepository.listGroups() } returns emptyList()
        coEvery { groupRepository.listMyGroups() } returns emptyList()
        coEvery { screenTimeRepository.getSelfScreenTime() } returns dummyScreenTime

        viewModel = HomeViewModel(groupRepository, screenTimeRepository)
        advanceUntilIdle()

        viewModel.enterScreenTime(-10)
        advanceUntilIdle()

        val state = viewModel.homeState.value
        assertTrue(state is HomeState.Error && state.message.contains("Screen time must be a positive number"))
    }
}
