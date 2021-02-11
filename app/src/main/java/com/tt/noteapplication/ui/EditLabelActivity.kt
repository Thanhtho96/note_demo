package com.tt.noteapplication.ui

import android.app.AlertDialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.EditLabelAdapter
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.model.LabelEntity
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.util.KeyboardUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_edit_label.*

class EditLabelActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {

    private val listLabels: MutableList<LabelEntity> = ArrayList()
    private lateinit var editLabelAdapter: EditLabelAdapter

    override fun layoutRes() = R.layout.activity_edit_label

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
        fetchAllLabel()
    }

    private fun fetchAllLabel() {
        viewModel.getAllLabel()
        viewModel.listLabelLiveData.observe(this, Observer {
            listLabels.clear()
            listLabels.addAll(it)
            editLabelAdapter.notifyDataSetChanged()
        })
    }

    private fun generateLabelRecyclerView() {
        editLabelAdapter = EditLabelAdapter(listLabels)
        editLabelAdapter.setOnItemClickListener(object : EditLabelAdapter.OnItemClickListener {
            override fun onDeleteClick(position: Int, isAppear: Boolean) {
                if (isAppear) {
                    AlertDialog.Builder(this@EditLabelActivity)
                        .setMessage(R.string.txt_want_to_delete_label)
                        .setPositiveButton(
                            R.string.txt_yes
                        ) { _, _ ->
                            viewModel.deleteLabel(listLabels[position])
                        }
                        .setNegativeButton(R.string.txt_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }
            }

            override fun onCheckClick(position: Int, newTitle: String, isAppear: Boolean) {
                if (!isAppear && newTitle.isNotBlank()) {
                    listLabels[position].apply {
                        title = newTitle
                        modifiedDate = DateUtil.getCurrentTimeInMillis()
                        viewModel.insertLabel(this)
                    }
                }
            }
        })
        val layoutManager = LinearLayoutManager(this)
        recycler_view_label_edit.layoutManager = layoutManager
        recycler_view_label_edit.itemAnimator = DefaultItemAnimator()
        recycler_view_label_edit.adapter = editLabelAdapter
    }


    override fun onClick(v: View?) {
        when (v) {
            text_view_back -> {
                onBackPressed()
            }
            image_clear -> {
                KeyboardUtil.closeSoftKeyboard(this, edit_text_add_label)
                edit_text_add_label.text = null
                edit_text_add_label.clearFocus()
            }
            image_ok -> {
                if (!edit_text_add_label.text.isNullOrBlank()) {
                    viewModel.insertLabel(
                        LabelEntity(
                            null,
                            edit_text_add_label.text.toString(),
                            DateUtil.getCurrentTimeInMillis(),
                            DateUtil.getCurrentTimeInMillis()
                        )
                    )
                    KeyboardUtil.closeSoftKeyboard(this, edit_text_add_label)
                    edit_text_add_label.text = null
                    edit_text_add_label.clearFocus()
                }
            }
        }
    }
}