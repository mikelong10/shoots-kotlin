package com.shoots.shoots_ui.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shoots.shoots_ui.data.local.GroupDao
import com.shoots.shoots_ui.data.model.GroupEntity
import com.shoots.shoots_ui.data.repository.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class GroupSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val groupRepository: GroupRepository,
    private val groupDao: GroupDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val groups = groupRepository.listGroups()
            // Convert API groups to GroupEntity and store in Room
            val groupEntities = groups.map { group ->
                GroupEntity(
                    id = group.id,
                    screenTimeGoal = group.screen_time_goal,
                    code = group.code,
                    stake = group.stake,
                    name = group.name,
                    insertedAt = group.inserted_at,
                    updatedAt = group.updated_at
                )
            }
            groupDao.insertGroups(groupEntities)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val WORK_NAME = "group_sync_worker"

        fun startPeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<GroupSyncWorker>(
                15, TimeUnit.MINUTES,  // Repeat every 15 minutes
                5, TimeUnit.MINUTES    // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
        }
    }

    class Factory(
        private val groupRepository: GroupRepository,
        private val groupDao: GroupDao
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return when (workerClassName) {
                GroupSyncWorker::class.java.name ->
                    GroupSyncWorker(appContext, workerParameters, groupRepository, groupDao)
                else -> null
            }
        }
    }
} 