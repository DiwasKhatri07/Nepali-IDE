package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_versions")
data class FileVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileId: Long,
    val content: String,
    val commitMessage: String = "Auto-save snapshot",
    val timestamp: Long = System.currentTimeMillis()
)
