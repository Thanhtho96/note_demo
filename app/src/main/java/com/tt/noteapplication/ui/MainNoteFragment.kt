package com.tt.noteapplication.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.GridSwipeNoteAdapter
import com.tt.noteapplication.base.BaseFragment
import com.tt.noteapplication.model.NoteEntity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.ItemDecorationGridLayout
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val OPEN_LOCK_NOTE_REQUEST_CODE = 77
private const val UNLOCK_NOTE_REQUEST_CODE = 777
private const val DELETE_LOCKED_NOTE_REQUEST_CODE = 7777

class MainNoteFragment : BaseFragment<NoteViewModel>() {

    private val sharePref: SharedPreferences by inject()
    private val noteViewModel by sharedViewModel<NoteViewModel>()
    private lateinit var noteAdapter: GridSwipeNoteAdapter
    private var requestListNote: MutableList<NoteEntity> = ArrayList()
    private var isUnlocked = false
    private var noteRequestId = 0L
    private var notePosition: Int = 0
    private var isAllNoteScreenActive = true
    private var isLabelNoteScreenActive = false
    private var isLockNoteScreenActive = false
    private var requestLabelId = 0L
    private var currentTab = Constants.Tab.NOTE
    private var currentLabel = 0L
    private lateinit var notePageList: PagedList<NoteEntity>

    override fun layoutRes() = R.layout.content_main

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun initView() {
        generateNoteRecyclerView()
    }

    override fun initData() {
        fetchAllNote()

        noteViewModel.tabActiveLiveData.observe(viewLifecycleOwner, Observer {
            when (it.first) {
                Constants.Tab.LABEL -> {
                    if (currentLabel != it.second) {
                        currentTab = Constants.Tab.LABEL
                        currentLabel = it.second
                        isUnlocked = false
                        fetchByLabel(it.second)
                    }
                }
                Constants.Tab.NOTE -> {
                    if (currentTab != Constants.Tab.NOTE) {
                        currentTab = Constants.Tab.NOTE
                        currentLabel = 0L
                        isUnlocked = false
                        fetchAllNote()
                    }
                }
                Constants.Tab.PRIVACY -> {
                    if (currentTab != Constants.Tab.PRIVACY) {
                        currentTab = Constants.Tab.PRIVACY
                        currentLabel = 0L
                        isUnlocked = true
                        (activity as NavigationDrawerActivity).showMainNoteFragment()
                        fetchAllLockNote()
                    }
                }
            }
        })
    }

    private fun fetchAllNote() {
        isAllNoteScreenActive = true
        isLabelNoteScreenActive = false
        isLockNoteScreenActive = false
        (activity as NavigationDrawerActivity).changeToolbarTitle(getString(R.string.txt_notes))
        viewModel.getAllNote()
        viewModel.listNote.observe(viewLifecycleOwner, Observer {
            if (isAllNoteScreenActive) {
                notePageList = it
                noteAdapter.submitList(notePageList)
            }
        })
    }

    private fun fetchAllLockNote() {
        isAllNoteScreenActive = false
        isLabelNoteScreenActive = false
        isLockNoteScreenActive = true
        (activity as NavigationDrawerActivity).changeToolbarTitle(getString(R.string.txt_privacy))
        viewModel.getAllLockedNoteOrderByModifiedDate()
        viewModel.listNote.observe(viewLifecycleOwner, Observer {
            if (isLockNoteScreenActive) {
                notePageList = it.apply {
                    forEach { note ->
                        note.isLocked = false
                    }
                }
                noteAdapter.submitList(notePageList)
            }
        })
    }

    private fun fetchByLabel(labelId: Long) {
        isAllNoteScreenActive = false
        isLabelNoteScreenActive = true
        isLockNoteScreenActive = false
        requestLabelId = labelId
        viewModel.getLabelWithNotes(labelId)
        viewModel.labelWithNotesLiveData.observe(viewLifecycleOwner, Observer { labelWithNotes ->
            if (isLabelNoteScreenActive) {
                if (labelWithNotes != null) {
                    if (labelWithNotes.label!!.labelId == requestLabelId) {
                        requestListNote.clear()
                        requestListNote.addAll(labelWithNotes.notes.sortedByDescending { it.modifiedDate })
                    }
                } else {
                    currentTab = Constants.Tab.NOTE
                    currentLabel = 0L
                    isUnlocked = false
                    fetchAllNote()
                }
            }
            viewModel.listNote.observe(viewLifecycleOwner, Observer { pagedList ->
                if (isLabelNoteScreenActive && requestListNote == pagedList.snapshot()) {
                    notePageList = pagedList
                    noteAdapter.submitList(notePageList)
                }
            })
        })
    }

    private fun generateNoteRecyclerView() {
        noteAdapter = GridSwipeNoteAdapter()
        noteAdapter.setOnItemClickListener(object : GridSwipeNoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val note = notePageList[position]
                if (note != null) {
                    if (note.isLocked && !isUnlocked) {
                        noteRequestId = note.noteId!!
                        startActivityForResult(
                            Intent(context, PrivacyActivity::class.java),
                            OPEN_LOCK_NOTE_REQUEST_CODE
                        )
                    } else
                        startActivity(
                            Intent(context, NoteActivity::class.java).apply {
                                putExtra(Constants.noteId, note.noteId)
                            })
                }
            }

            override fun onDeleteClick(position: Int) {
                val note = notePageList[position]
                AlertDialog.Builder(context)
                    .setMessage(R.string.txt_want_to_delete_note)
                    .setPositiveButton(
                        R.string.txt_yes
                    ) { _, _ ->
                        if (note != null) {
                            if (note.isLocked && !isUnlocked) {
                                noteRequestId = note.noteId!!
                                startActivityForResult(
                                    Intent(context, PrivacyActivity::class.java),
                                    DELETE_LOCKED_NOTE_REQUEST_CODE
                                )
                            } else {
                                deleteNote(note)
                            }
                        }
                    }
                    .setNegativeButton(R.string.txt_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }

            override fun onLockClick(position: Int) {
                val lockPattern = sharePref.getString(Constants.lockPattern, null)
                if (lockPattern.isNullOrBlank()) {
                    startActivity(
                        Intent(
                            context,
                            PrivacyActivity::class.java
                        )
                    )
                } else {
                    val note = notePageList[position]
                    if (note != null) {
                        if (note.isLocked) {
                            if (isUnlocked) {
                                note.isLocked = false
                                viewModel.insertNote(note)
                            } else {
                                notePosition = position
                                startActivityForResult(
                                    Intent(context, PrivacyActivity::class.java),
                                    UNLOCK_NOTE_REQUEST_CODE
                                )
                            }
                        } else {
                            note.isLocked = true
                            viewModel.insertNote(note)
                        }
                        noteAdapter.notifyItemChanged(position)
                    }
                }
            }
        })
        recycler_view_note.addItemDecoration(
            ItemDecorationGridLayout(
                resources.getDimensionPixelSize(R.dimen.grid_spacing),
                resources.getInteger(R.integer.note_grid_span_count)
            )
        )
        recycler_view_note.itemAnimator = DefaultItemAnimator()
        recycler_view_note.layoutManager =
            GridLayoutManager(context, resources.getInteger(R.integer.note_grid_span_count))
        recycler_view_note.adapter = noteAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                OPEN_LOCK_NOTE_REQUEST_CODE -> {
                    startActivity(
                        Intent(context, NoteActivity::class.java).apply {
                            putExtra(Constants.noteId, noteRequestId)
                        })
                }
                UNLOCK_NOTE_REQUEST_CODE -> {
                    val note = notePageList[notePosition]
                    if (note != null) {
                        note.isLocked = false
                        viewModel.insertNote(note)
                    }
                    noteAdapter.notifyItemChanged(notePosition)
                }
                DELETE_LOCKED_NOTE_REQUEST_CODE -> {
                    val note = notePageList[notePosition]
                    if (note != null) {
                        deleteNote(note)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deleteNote(note: NoteEntity) {
        viewModel.deleteNote(note)
        viewModel.deleteAlarmByNoteId(note.noteId!!)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainNoteFragment()
    }
}