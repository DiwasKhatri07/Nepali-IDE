package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileVersionDao {
    @Query("SELECT * FROM file_versions WHERE fileId = :fileId ORDER BY timestamp DESC LIMIT 20")
    fun getVersionsForFile(fileId: Long): Flow<List<FileVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: FileVersionEntity): Long

    @Query("DELETE FROM file_versions WHERE fileId = :fileId")
    suspend fun deleteVersionsForFile(fileId: Long)
}
