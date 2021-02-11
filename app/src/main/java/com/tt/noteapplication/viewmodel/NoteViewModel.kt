package com.tt.noteapplication.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.tt.noteapplication.base.BaseViewModel
import com.tt.noteapplication.database.NoteRoomDatabase
import com.tt.noteapplication.model.*
import com.tt.noteapplication.repository.NoteRepository
import com.tt.noteapplication.util.Constants
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : BaseViewModel(application) {

    private val noteRepository: NoteRepository
    var noteLiveData: LiveData<NoteEntity> = MutableLiveData()
    var listLabelLiveData: LiveData<List<LabelEntity>> = MutableLiveData()
    var labelIdAddLiveData: MutableLiveData<Long> = MutableLiveData()
    var noteIdAddLiveData: MutableLiveData<Long> = MutableLiveData()
    var labelAssociateLiveData: MutableLiveData<List<LabelAssociate>> = MutableLiveData()
    var alarmOfNoteLiveData: LiveData<AlarmOfNoteEntity?> = MutableLiveData()
    var tabActiveLiveData: MutableLiveData<Pair<Constants.Tab, Long>> = MutableLiveData()
    var alarmLiveData: LiveData<List<AlarmOfNoteEntity>> = MutableLiveData()
    var alarmInDayLiveData: MutableLiveData<List<AlarmAndNote>> = MutableLiveData()
    var noteWithLabelsLiveData: LiveData<NoteWithLabels?> = MutableLiveData()

    var labelWithNotesLiveData: LiveData<LabelWithNotes> = MutableLiveData()
    var listResultNote: LiveData<PagedList<NoteEntity>> = MutableLiveData()
    var listNote: LiveData<PagedList<NoteEntity>> = MutableLiveData()

    init {
        val noteRoomDatabase =
            NoteRoomDatabase.getDatabase(
                application
            )
        noteRepository = NoteRepository(noteRoomDatabase.noteDao())
    }

    fun getAllNote() {
        listNote = noteRepository.getAllNoteOrderByModifiedDate().toLiveData(pageSize = 10)
    }

    fun getAllLockedNoteOrderByModifiedDate() {
        listNote =
            noteRepository.getAllLockedNoteOrderByModifiedDate(true).toLiveData(pageSize = 10)
    }

    fun getNoteById(id: Long) {
        noteLiveData = noteRepository.getNoteById(id)
    }

    fun insertNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteIdAddLiveData.value = noteRepository.insertNote(noteEntity)
        }
    }

    fun searchByKeyword(keyword: String) {
        listResultNote = noteRepository.searchKeyword(keyword).toLiveData(pageSize = 10)
    }

    fun deleteNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteEntity)
        }
    }

    fun getAllLabel() {
        listLabelLiveData = noteRepository.getAllLabelOrderByModifiedDate()
    }

    fun insertLabel(labelEntity: LabelEntity) {
        viewModelScope.launch {
            labelIdAddLiveData.value = noteRepository.insertLabel(labelEntity)
        }
    }

    fun deleteLabel(labelEntity: LabelEntity) {
        viewModelScope.launch {
            noteRepository.deleteLabel(labelEntity)
        }
    }

    fun getLabelWithNotes(labelId: Long) {
        labelWithNotesLiveData = noteRepository.getLabelWithListNotes(labelId)
        listNote = noteRepository.getLabelWithNotes(labelId).toLiveData(pageSize = 10)
    }

    fun getNoteWithLabels(noteId: Long) {
        noteWithLabelsLiveData = noteRepository.getNoteWithLabels(noteId)
    }

    fun getLabelAssociate(noteId: Long) {
        viewModelScope.launch {
            labelAssociateLiveData.value = noteRepository.getLabelAssociate(noteId)
        }
    }

    fun insertLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef) {
        viewModelScope.launch {
            noteRepository.insertLabelNoteCrossRef(labelNoteCrossRef)
        }
    }

    fun deleteLabelNoteCrossRef(labelNoteCrossRef: LabelNoteCrossRef) {
        viewModelScope.launch {
            noteRepository.deleteLabelNoteCrossRef(labelNoteCrossRef)
        }
    }

    fun deleteLabelNoteCrossRefByNoteId(noteId: Long) {
        viewModelScope.launch {
            noteRepository.deleteLabelNoteCrossRefByNoteId(noteId)
        }
    }

    fun updateLabelNoteCrossRefNoteId(newNoteId: Long, oldNoteId: Long) {
        viewModelScope.launch {
            noteRepository.updateLabelNoteCrossRefNoteId(newNoteId, oldNoteId)
        }
    }

    fun getAlarmOfNote(noteId: Long) {
        alarmOfNoteLiveData = noteRepository.getAlarmOfNote(noteId)
    }

    fun insertAlarm(alarmOfNoteEntity: AlarmOfNoteEntity) {
        viewModelScope.launch {
            noteRepository.insertAlarm(alarmOfNoteEntity)
        }
    }

    fun deleteAlarm(alarmOfNoteEntity: AlarmOfNoteEntity) {
        viewModelScope.launch {
            noteRepository.deleteAlarm(alarmOfNoteEntity)
        }
    }

    fun deleteAlarmByNoteId(noteId: Long) {
        viewModelScope.launch {
            noteRepository.deleteAlarmByNoteId(noteId)
        }
    }

    fun changeTab(tab: Constants.Tab, labelId: Long) {
        tabActiveLiveData.value = Pair(tab, labelId)
    }

    fun getAllAlarm() {
        alarmLiveData = noteRepository.getAllAlarm()
    }

    fun getAllAlarmInDay(today: Long, nextDay: Long) {
        viewModelScope.launch {
            alarmInDayLiveData.value = noteRepository.getAllAlarmInDay(today, nextDay)
        }
    }

    suspend fun getAllNotes(): List<NoteEntity> {
        return noteRepository.getAllNote()
    }

    suspend fun getAllLabels(): List<LabelEntity> {
        return noteRepository.getAllLabel()
    }

    suspend fun getAllLabelNote(): List<LabelNoteCrossRef> {
        return noteRepository.getAllLabelNote()
    }

    suspend fun getAllAlarms(): List<AlarmOfNoteEntity> {
        return noteRepository.getAllAlarms()
    }
}