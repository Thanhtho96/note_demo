package com.tt.noteapplication.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.tt.noteapplication.dao.NoteDao
import com.tt.noteapplication.model.*

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNoteOrderByModifiedDate(): DataSource.Factory<Int, NoteEntity> {
        return noteDao.getAllNoteOrderByModifiedDate()
    }

    fun getAllLockedNoteOrderByModifiedDate(isLocked: Boolean): DataSource.Factory<Int, NoteEntity> {
        return noteDao.getAllLockedNoteOrderByModifiedDate(isLocked)
    }

    fun getNoteById(id: Long): LiveData<NoteEntity> {
        return noteDao.getNoteById(id)
    }

    suspend fun getNoteByNoteId(noteId: Long): NoteEntity? {
        return noteDao.getNoteByNoteId(noteId)
    }

    suspend fun insertNote(noteEntity: NoteEntity): Long {
        return noteDao.insertNote(noteEntity)
    }

    fun searchKeyword(keyword: String): DataSource.Factory<Int, NoteEntity> {
        return noteDao.searchKeyword(keyword)
    }

    suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

    fun getAllLabelOrderByModifiedDate(): LiveData<List<LabelEntity>> {
        return noteDao.getAllLabelOrderByModifiedDate()
    }

    suspend fun insertLabel(labelEntity: LabelEntity): Long {
        return noteDao.insertLabel(labelEntity)
    }

    suspend fun deleteLabel(labelEntity: LabelEntity) {
        noteDao.deleteLabel(labelEntity)
    }

    fun getLabelWithNotes(labelId: Long): DataSource.Factory<Int, NoteEntity> {
        return noteDao.getLabelWithNotes(labelId)
    }

    fun getLabelWithListNotes(labelId: Long): LiveData<LabelWithNotes> {
        return noteDao.getLabelWithListNotes(labelId)
    }

    fun getNoteWithLabels(noteId: Long): LiveData<NoteWithLabels?> {
        return noteDao.getNoteWithLabels(noteId)
    }

    suspend fun getLabelAssociate(noteId: Long): List<LabelAssociate> {
        return noteDao.getLabelAssociate(noteId)
    }

    suspend fun insertLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef) {
        noteDao.insertLabelNoteCrossRef(labelNoteCrossRef)
    }

    suspend fun deleteLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef) {
        noteDao.deleteLabelNoteCrossRef(labelNoteCrossRef)
    }

    suspend fun deleteLabelNoteCrossRefByNoteId(noteId: Long) {
        noteDao.deleteLabelNoteCrossRefByNoteId(noteId)
    }

    suspend fun updateLabelNoteCrossRefNoteId(newNoteId: Long, oldNoteId: Long) {
        noteDao.updateLabelNoteCrossRefNoteId(newNoteId, oldNoteId)
    }

    fun getAlarmOfNote(noteId: Long): LiveData<AlarmOfNoteEntity?> {
        return noteDao.getAlarmOfNote(noteId)
    }

    suspend fun insertAlarm(alarmOfNoteEntity: AlarmOfNoteEntity) {
        noteDao.insertAlarm(alarmOfNoteEntity)
    }

    suspend fun deleteAlarm(alarmOfNoteEntity: AlarmOfNoteEntity) {
        noteDao.deleteAlarm(alarmOfNoteEntity)
    }

    suspend fun deleteAlarmByNoteId(noteId: Long) {
        noteDao.deleteAlarmByNoteId(noteId)
    }

    suspend fun getNoteAlarmAppropriate(now: Long): List<AlarmOfNoteEntity> {
        return noteDao.getNoteAlarmAppropriate(now)
    }

    suspend fun getAllNoteAndAlarm(): List<AlarmAndNote> {
        return noteDao.getAllNoteAndAlarm()
    }

    fun getAllAlarm(): LiveData<List<AlarmOfNoteEntity>> {
        return noteDao.getAllAlarm()
    }

    suspend fun getAllAlarmInDay(today: Long, nextDay: Long): List<AlarmAndNote> {
        return noteDao.getAllAlarmInDay(today, nextDay)
    }

    suspend fun getAllNote(): List<NoteEntity> {
        return noteDao.getAllNote()
    }

    suspend fun getAllLabel(): List<LabelEntity> {
        return noteDao.getAllLabel()
    }

    suspend fun getAllLabelNote(): List<LabelNoteCrossRef> {
        return noteDao.getAllLabelNote()
    }

    suspend fun getAllAlarms(): List<AlarmOfNoteEntity> {
        return noteDao.getAllAlarms()
    }
}