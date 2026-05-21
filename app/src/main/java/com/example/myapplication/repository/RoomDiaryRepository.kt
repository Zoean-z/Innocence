package com.example.myapplication.repository

import com.example.myapplication.data.local.DiaryDao
import com.example.myapplication.data.local.toEntity
import com.example.myapplication.data.local.toModel
import com.example.myapplication.model.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDiaryRepository(
    private val dao: DiaryDao
) : DiaryRepository {
    override fun observeEntries(): Flow<List<DiaryEntry>> =
        dao.observeEntries().map { entries -> entries.map { it.toModel() } }

    override suspend fun addEntry(entry: DiaryEntry): DiaryEntry {
        val id = dao.insert(entry.copy(id = 0).toEntity()).toInt()
        return entry.copy(id = id)
    }

    override suspend fun updateEntry(entry: DiaryEntry) {
        dao.update(entry.toEntity())
    }

    override suspend fun deleteEntry(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun seedDefaultsIfNeeded(entries: List<DiaryEntry>, alreadySeeded: Boolean) {
        if (!alreadySeeded && dao.count() == 0) {
            dao.insertAll(entries.map { it.copy(id = 0).toEntity() })
        }
    }
}
