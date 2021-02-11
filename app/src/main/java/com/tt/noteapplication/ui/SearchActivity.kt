package com.tt.noteapplication.ui

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.widget.textChanges
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.NoteResultAdapter
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.model.NoteEntity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.viewmodel.NoteViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

private const val OPEN_LOCK_NOTE_REQUEST_CODE = 77

class SearchActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {
    private lateinit var noteResultAdapter: NoteResultAdapter
    private var noteRequestId = 0L
    private lateinit var pageList: PagedList<NoteEntity>
    override fun layoutRes() = R.layout.activity_search

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun setTheme() {
    }

    override fun initView() {
        generateNoteResultRecyclerView()

        listenToEdtSearch()
    }

    override fun initData() {
        listenToEdtTextChangeRxBinding()
    }

    private fun listenToEdtTextChangeRxBinding() {
        edit_text_search.textChanges()
            .debounce(177, TimeUnit.MILLISECONDS)
            .map { charSequence ->
                charSequence.toString()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { string ->
                viewModel.searchByKeyword(string)
                viewModel.listResultNote.observe(this, Observer {
                    pageList = it
                    noteResultAdapter.submitList(it)
                })
                recycler_view_result.adapter = noteResultAdapter
            }
    }

    private fun listenToEdtSearch() {
        edit_text_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) clear_button.visibility = View.GONE
                else clear_button.visibility = View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun generateNoteResultRecyclerView() {
        noteResultAdapter = NoteResultAdapter()
        noteResultAdapter.setOnItemClickListener(object : NoteResultAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val note = pageList[position]
                if (note != null) {
                    if (note.isLocked) {
                        noteRequestId = note.noteId!!
                        startActivityForResult(
                            Intent(this@SearchActivity, PrivacyActivity::class.java),
                            OPEN_LOCK_NOTE_REQUEST_CODE
                        )
                    } else
                        startActivity(
                            Intent(this@SearchActivity, NoteActivity::class.java).apply {
                                putExtra(Constants.noteId, note.noteId)
                            })
                }
            }
        })
        val layoutManager = LinearLayoutManager(this)
        recycler_view_result.layoutManager = layoutManager
        recycler_view_result.itemAnimator = DefaultItemAnimator()
        recycler_view_result.adapter = noteResultAdapter
        recycler_view_result.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            ).apply {
                setDrawable(
                    ContextCompat.getDrawable(
                        this@SearchActivity,
                        R.drawable.item_divider_transparent
                    )!!
                )
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                OPEN_LOCK_NOTE_REQUEST_CODE -> {
                    startActivity(
                        Intent(this@SearchActivity, NoteActivity::class.java).apply {
                            putExtra(Constants.noteId, noteRequestId)
                        })
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View?) {
        when (view) {
            back_button -> {
                onBackPressed()
            }
            clear_button -> {
                edit_text_search.text = null
            }
        }
    }


}