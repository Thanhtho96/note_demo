package com.tt.noteapplication.ui

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.widget.textChanges
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.ChooseLabelAdapter
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.model.LabelAssociate
import com.tt.noteapplication.model.LabelEntity
import com.tt.noteapplication.model.LabelNoteCrossRef
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.util.KeyboardUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_label.*
import java.util.concurrent.TimeUnit

class AddLabelActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {
    private val listLabelAssociate: MutableList<LabelAssociate> = ArrayList()
    private lateinit var chooseLabelAdapter: ChooseLabelAdapter
    private var isEditInFocus = false
    private var noteId = 0L
    private lateinit var labelAssociate: LabelAssociate

    override fun layoutRes() = R.layout.activity_add_label

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun setTheme() {
    }

    override fun initView() {
        generateLabelRecyclerView()
    }

    override fun initData() {
        noteId = intent.getLongExtra(Constants.noteId, 0L)

        viewModel.getLabelAssociate(noteId)
        viewModel.labelAssociateLiveData.observe(this, Observer {
            listLabelAssociate.clear()
            listLabelAssociate.addAll(it)
            chooseLabelAdapter.notifyDataSetChanged()
        })

        viewModel.labelIdAddLiveData.observe(this, Observer {
            // Thêm vào bảng nối giữa label và note
            viewModel.insertLabelNoteCrossRef(LabelNoteCrossRef(it, noteId))
            // Update labelAssociate với id của label vừa thêm
            labelAssociate.labelId = it
            listLabelAssociate.add(
                0,
                labelAssociate
            )
            chooseLabelAdapter.notifyItemInserted(0)
        })

        listenToEditAddLabel()
    }

    private fun listenToEditAddLabel() {
        edit_text_add_label.setOnFocusChangeListener { _, isFocus ->
            isEditInFocus = isFocus
            if (isFocus) {
                recycler_view_label.visibility = View.GONE
                layout_add_label.visibility = View.VISIBLE
            } else {
                recycler_view_label.visibility = View.VISIBLE
                layout_add_label.visibility = View.GONE
                KeyboardUtil.closeSoftKeyboard(this, edit_text_add_label)
            }
        }

        edit_text_add_label.textChanges()
            .debounce(77, TimeUnit.MILLISECONDS)
            .map { charSequence ->
                charSequence.toString()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { string ->
                create_label_name.text = getString(R.string.txt_create_block_quote, string)
            }
    }

    private fun generateLabelRecyclerView() {
        chooseLabelAdapter = ChooseLabelAdapter(listLabelAssociate)
        chooseLabelAdapter.setOnItemClickListener(object : ChooseLabelAdapter.OnItemClickListener {
            override fun onCheckBoxCheck(position: Int, cb: CompoundButton, isChecked: Boolean) {
                if (!cb.isPressed) return
                val labelNoteCrossRef = LabelNoteCrossRef(
                    listLabelAssociate[position].labelId,
                    noteId
                )
                val labelAssociate = listLabelAssociate[position]
                if (isChecked) {
                    // Cập nhật isLabelOfNote 1 == true
                    labelAssociate.isLabelOfNote = 1
                    // Thêm vào bảng nối giữa label và note
                    viewModel.insertLabelNoteCrossRef(labelNoteCrossRef)
                } else {
                    // Cập nhật isLabelOfNote 0 == false
                    labelAssociate.isLabelOfNote = 0
                    // Xoá trong bảng nối giữa label và note
                    viewModel.deleteLabelNoteCrossRef(labelNoteCrossRef)
                }
            }
        })
        val layoutManager = LinearLayoutManager(this)
        recycler_view_label.layoutManager = layoutManager
        recycler_view_label.itemAnimator = DefaultItemAnimator()
        recycler_view_label.adapter = chooseLabelAdapter
    }

    override fun onBackPressed() {
        if (isEditInFocus) {
            edit_text_add_label.clearFocus()
            edit_text_add_label.text = null
        } else {
            finish()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            back_button -> {
                onBackPressed()
            }
            layout_add_label -> {
                val title = edit_text_add_label.text.toString()
                if (title.isNotEmpty()) {
                    labelAssociate = LabelAssociate(
                        1,
                        title,
                        1
                    )

                    if (!listLabelAssociate.contains(labelAssociate)) {
                        viewModel.insertLabel(
                            LabelEntity(
                                null,
                                title,
                                DateUtil.getCurrentTimeInMillis(),
                                DateUtil.getCurrentTimeInMillis()
                            )
                        )
                    }

                    edit_text_add_label.clearFocus()
                    edit_text_add_label.text = null
                }
            }
        }
    }
}