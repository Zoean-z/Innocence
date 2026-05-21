package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.model.DiaryEntry
import com.example.myapplication.model.photoKeyOf
import com.example.myapplication.model.photoStyleForKey

@Entity(tableName = "diary_entries")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val weekday: String,
    val mood: String,
    val songName: String,
    val artist: String,
    val lyricSnippet: String,
    val reflection: String,
    val photoKey: String,
    val coverUri: String?,
    val createdAt: Long,
    val updatedAt: Long
)

fun DiaryEntity.toModel(): DiaryEntry = DiaryEntry(
    id = id,
    date = date,
    weekday = weekday,
    mood = mood,
    songName = songName,
    artist = artist,
    lyricSnippet = lyricSnippet,
    reflection = reflection,
    photo = photoStyleForKey(photoKey),
    coverUri = coverUri
)

fun DiaryEntry.toEntity(now: Long = System.currentTimeMillis()): DiaryEntity = DiaryEntity(
    id = id,
    date = date,
    weekday = weekday,
    mood = mood,
    songName = songName,
    artist = artist,
    lyricSnippet = lyricSnippet,
    reflection = reflection,
    photoKey = photoKeyOf(photo),
    coverUri = coverUri,
    createdAt = now,
    updatedAt = now
)
