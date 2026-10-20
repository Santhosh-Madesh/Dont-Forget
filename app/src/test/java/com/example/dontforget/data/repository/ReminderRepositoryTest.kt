package com.example.dontforget.data.repository

import com.example.dontforget.data.local.dao.ChecklistItemDao
import com.example.dontforget.data.local.dao.ReminderDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeReminderDao(
    private val fakeChecklistItemDaoProvider: () -> FakeChecklistItemDao? = { null }
) : ReminderDao {
    val reminders = mutableListOf<ReminderEntity>()

    override fun getActiveReminders(): Flow<List<ReminderWithItems>> = flow {
        val itemsList = fakeChecklistItemDaoProvider()?.items ?: emptyList()
        val active = reminders.filter { !it.isCompleted }.map { reminder ->
            ReminderWithItems(
                reminder = reminder,
                items = itemsList.filter { it.parentId == reminder.id && it.parentType == "REMINDER" }
            )
        }
        emit(active)
    }

    override fun getCompletedReminders(): Flow<List<ReminderWithItems>> = flow {
        val itemsList = fakeChecklistItemDaoProvider()?.items ?: emptyList()
        val completed = reminders.filter { it.isCompleted }.map { reminder ->
            ReminderWithItems(
                reminder = reminder,
                items = itemsList.filter { it.parentId == reminder.id && it.parentType == "REMINDER" }
            )
        }
        emit(completed)
    }

    override fun getReminderById(id: Long): Flow<ReminderWithItems?> = flow {
        val reminder = reminders.find { it.id == id }
        if (reminder == null) {
            emit(null)
        } else {
            val itemsList = fakeChecklistItemDaoProvider()?.items ?: emptyList()
            val items = itemsList.filter { it.parentId == reminder.id && it.parentType == "REMINDER" }
            emit(ReminderWithItems(reminder, items))
        }
    }

    override suspend fun getReminderEntityById(id: Long): ReminderEntity? {
        return reminders.find { it.id == id }
    }

    override suspend fun insertReminder(reminder: ReminderEntity): Long {
        val id = if (reminder.id == 0L) (reminders.size + 1).toLong() else reminder.id
        val newReminder = reminder.copy(id = id)
        reminders.add(newReminder)
        return id
    }

    override suspend fun updateReminder(reminder: ReminderEntity) {
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder
        }
    }

    override suspend fun markCompleted(id: Long, completedTime: Long) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            reminders[index] = reminders[index].copy(isCompleted = true, completedEpochMillis = completedTime)
        }
    }

    override suspend fun restoreReminder(id: Long) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            reminders[index] = reminders[index].copy(isCompleted = false, completedEpochMillis = null)
        }
    }

    override suspend fun deleteReminderById(id: Long) {
        reminders.removeAll { it.id == id }
    }

    override suspend fun getInactiveReminderIdsOlderThan(cutoffTime: Long): List<Long> {
        return reminders.filter { it.isCompleted && (it.completedEpochMillis ?: 0L) <= cutoffTime }.map { it.id }
    }

    override suspend fun deleteInactiveRemindersOlderThan(cutoffTime: Long) {
        reminders.removeAll { it.isCompleted && (it.completedEpochMillis ?: 0L) <= cutoffTime }
    }
}

class FakeChecklistItemDao : ChecklistItemDao {
    val items = mutableListOf<ChecklistItemEntity>()

    override suspend fun insertItems(items: List<ChecklistItemEntity>) {
        this.items.addAll(items)
    }

    override suspend fun updateItem(item: ChecklistItemEntity) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
        }
    }

    override suspend fun updateItems(items: List<ChecklistItemEntity>) {
        items.forEach { updatedItem ->
            val index = this.items.indexOfFirst { it.id == updatedItem.id }
            if (index != -1) {
                this.items[index] = updatedItem
            }
        }
    }

    override suspend fun updatePackedStatus(itemId: Long, isPacked: Boolean) {
        val index = items.indexOfFirst { it.id == itemId }
        if (index != -1) {
            items[index] = items[index].copy(isPacked = isPacked)
        }
    }

    override suspend fun packAllForReminder(parentId: Long) {
        items.forEachIndexed { i, item ->
            if (item.parentId == parentId && item.parentType == "REMINDER") {
                items[i] = item.copy(isPacked = true)
            }
        }
    }

    override suspend fun deleteItemsForParent(parentId: Long, parentType: String) {
        items.removeAll { it.parentId == parentId && it.parentType == parentType }
    }

    override suspend fun deleteItemsForReminders(parentIds: List<Long>) {
        items.removeAll { parentIds.contains(it.parentId) && it.parentType == "REMINDER" }
    }
}

class ReminderRepositoryTest {

    private lateinit var fakeReminderDao: FakeReminderDao
    private lateinit var fakeChecklistItemDao: FakeChecklistItemDao
    private lateinit var repository: ReminderRepositoryImpl

    @Before
    fun setUp() {
        fakeChecklistItemDao = FakeChecklistItemDao()
        fakeReminderDao = FakeReminderDao { fakeChecklistItemDao }
        repository = ReminderRepositoryImpl(fakeReminderDao, fakeChecklistItemDao)
    }

    @Test
    fun `insertReminder saves reminder and items correctly`() = runTest {
        val reminder = ReminderEntity(contextId = 1, contextTitle = "College", contextIconName = "school", departureEpochMillis = 10000)
        val items = listOf(ChecklistItemEntity(parentId = 0, parentType = "REMINDER", text = "ID Card"))

        val id = repository.insertReminder(reminder, items)

        assertEquals(1L, id)
        assertEquals(1, fakeReminderDao.reminders.size)
        assertEquals(1, fakeChecklistItemDao.items.size)
        assertEquals("ID Card", fakeChecklistItemDao.items[0].text)
        assertEquals(1L, fakeChecklistItemDao.items[0].parentId)
    }

    @Test
    fun `deleteInactiveRemindersOlderThan deletes expired inactive reminders`() = runTest {
        val currentTime = System.currentTimeMillis()
        val oldCompletedTime = currentTime - (25 * 60 * 60 * 1000L) // 25 hours ago

        val reminderOld = ReminderEntity(id = 1, contextId = 1, contextTitle = "Work", contextIconName = "work", departureEpochMillis = 100, isCompleted = true, completedEpochMillis = oldCompletedTime)
        val reminderNew = ReminderEntity(id = 2, contextId = 1, contextTitle = "Home", contextIconName = "home", departureEpochMillis = 100, isCompleted = false)

        fakeReminderDao.insertReminder(reminderOld)
        fakeReminderDao.insertReminder(reminderNew)

        val deletedIds = repository.deleteInactiveRemindersOlderThan(currentTime - (24 * 60 * 60 * 1000L))

        assertEquals(listOf(1L), deletedIds)
        assertEquals(1, fakeReminderDao.reminders.size)
        assertEquals(2L, fakeReminderDao.reminders[0].id)
    }
}
