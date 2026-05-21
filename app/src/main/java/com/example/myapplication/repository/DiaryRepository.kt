package com.example.myapplication.repository

import com.example.myapplication.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun observeEntries(): Flow<List<DiaryEntry>>
    suspend fun addEntry(entry: DiaryEntry): DiaryEntry
    suspend fun updateEntry(entry: DiaryEntry)
    suspend fun deleteEntry(id: Int)
    suspend fun seedDefaultsIfNeeded(entries: List<DiaryEntry>, alreadySeeded: Boolean)
}
