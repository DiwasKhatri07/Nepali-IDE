package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_files")
data class CodeFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val name: String,
    val path: String,
    val extension: String,
    val content: String,
    val isFolder: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)
