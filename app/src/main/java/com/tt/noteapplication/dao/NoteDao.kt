package com.tt.noteapplication.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.tt.noteapplication.model.*

@Dao
interface NoteDao {

    @Query("SELECT n.* FROM note n WHERE n.noteId != 0 ORDER BY n.modifiedDate DESC")
    fun getAllNoteOrderByModifiedDate(): DataSource.Factory<Int, NoteEntity>

    @Query("SELECT n.* FROM note n WHERE n.isLocked = :isLocked AND n.noteId != 0 ORDER BY n.modifiedDate DESC")
    fun getAllLockedNoteOrderByModifiedDate(isLocked: Boolean): DataSource.Factory<Int, NoteEntity>

    @Query("SELECT n.* FROM note n WHERE n.noteId = :id")
    fun getNoteById(id: Long): LiveData<NoteEntity>

    @Query("SELECT n.* FROM note n WHERE n.noteId = :noteId")
    suspend fun getNoteByNoteId(noteId: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteEntity: NoteEntity): Long

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("SELECT n.* FROM note n WHERE n.title LIKE '%' || :keyword || '%' OR n.content LIKE '%' || :keyword || '%' AND n.noteId != 0 ORDER BY n.modifiedDate DESC")
    fun searchKeyword(keyword: String): DataSource.Factory<Int, NoteEntity>

    @Query("SELECT * FROM label ORDER BY modifiedDate DESC")
    fun getAllLabelOrderByModifiedDate(): LiveData<List<LabelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(labelEntity: LabelEntity): Long

    @Delete
    suspend fun deleteLabel(labelEntity: LabelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef)

    @Delete
    suspend fun deleteLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef)

    @Query("SELECT n.* FROM note n INNER JOIN labelnotecrossref lncr ON lncr.noteId = n.noteId INNER JOIN label lb ON lb.labelId = lncr.labelId WHERE n.noteId != 0 AND lb.labelId = :labelId ORDER BY n.modifiedDate DESC")
    fun getLabelWithNotes(labelId: Long): DataSource.Factory<Int, NoteEntity>

    @Transaction
    @Query("SELECT * FROM label WHERE labelId = :labelId")
    fun getLabelWithListNotes(labelId: Long): LiveData<LabelWithNotes>

    @Transaction
    @Query("SELECT * FROM note WHERE noteId = :noteId")
    fun getNoteWithLabels(noteId: Long): LiveData<NoteWithLabels?>

    @Query("SELECT lb.labelId as labelId, lb.title as labelTitle, (SELECT CASE WHEN lncr.noteId IS NULL THEN 0 ELSE 1 END FROM labelnotecrossref lncr WHERE lncr.labelId = lb.labelId AND lncr.noteId = :noteId) AS isLabelOfNote FROM label lb ORDER BY lb.modifiedDate DESC")
    suspend fun getLabelAssociate(noteId: Long): List<LabelAssociate>

    @Query("DELETE FROM labelnotecrossref WHERE noteId = :noteId")
    suspend fun deleteLabelNoteCrossRefByNoteId(noteId: Long)

    @Query("UPDATE labelnotecrossref SET noteId = :newNoteId WHERE noteId = :oldNoteId")
    suspend fun updateLabelNoteCrossRefNoteId(newNoteId: Long, oldNoteId: Long)

    @Query("SELECT * FROM note_with_alarm WHERE noteId = :noteId")
    fun getAlarmOfNote(noteId: Long): LiveData<AlarmOfNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmOfNoteEntity: AlarmOfNoteEntity)

    @Delete
    suspend fun deleteAlarm(alarmOfNoteEntity: AlarmOfNoteEntity)

    @Query("DELETE FROM note_with_alarm WHERE noteId = :noteId")
    suspend fun deleteAlarmByNoteId(noteId: Long)

    @Query("SELECT * FROM note_with_alarm nA WHERE nA.alarmTime > :now")
    suspend fun getNoteAlarmAppropriate(now: Long): List<AlarmOfNoteEntity>

    @Transaction
    @Query("SELECT * FROM note_with_alarm")
    suspend fun getAllNoteAndAlarm(): List<AlarmAndNote>

    @Query("SELECT * FROM note_with_alarm")
    fun getAllAlarm(): LiveData<List<AlarmOfNoteEntity>>

    @Query("SELECT nA.*, n.* FROM note_with_alarm nA INNER JOIN note n ON nA.noteId = n.noteId WHERE n.noteId != 0 AND na.alarmTime >= :today AND nA.alarmTime < :nextDay ORDER BY nA.alarmTime")
    suspend fun getAllAlarmInDay(today: Long, nextDay: Long): List<AlarmAndNote>

    @Query("SELECT * from note")
    suspend fun getAllNote(): List<NoteEntity>

    @Query("SELECT * from label")
    suspend fun getAllLabel(): List<LabelEntity>

    @Query("SELECT * from labelnotecrossref")
    suspend fun getAllLabelNote(): List<LabelNoteCrossRef>

    @Query("SELECT * from note_with_alarm")
    suspend fun getAllAlarms(): List<AlarmOfNoteEntity>
}
