package com.tt.noteapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.tt.noteapplication.R
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.model.AlarmOfNoteEntity
import com.tt.noteapplication.model.LabelNoteCrossRef
import com.tt.noteapplication.model.NoteEntity
import com.tt.noteapplication.util.AlarmManagerUtil
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import jp.wasabeef.richeditor.RichEditor.Type.*
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val CHANGE_LOCK_STATUS_REQUEST_CODE = 777

class NoteActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {

    private val sharePref: SharedPreferences by inject()
    private var isBold = false
    private var isItalic = false
    private var isUnderline = false
    private var isStrikeThrough = false
    private var createDate: Long? = null
    private var noteId: Long? = null
    private var initTitle: String? = null
    private var initContent: String? = null
    private var locked = false
    private lateinit var noteTemp: NoteEntity
    private val fm = supportFragmentManager
    private val scope = CoroutineScope(SupervisorJob())
    private var alarmManager: AlarmManager? = null
    private var alarmTime = 0L

    override fun layoutRes() = R.layout.activity_note

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun setTheme() {
        setTheme(R.style.AppThemeSunColor)
    }

    override fun initView() {
        richEditor.setPadding(10, 0, 10, 10)
        richEditor.setPlaceholder(getString(R.string.txt_please_input_here))
        richEditor.setBackgroundColor(ContextCompat.getColor(this, R.color.sun_color))
        richEditor.setEditorFontSize(
            when (Constants.Font.valueOf(
                sharePref.getString(
                    Constants.font_sharePref,
                    Constants.Font.NORMAL.name
                )!!
            )) {
                Constants.Font.SMALL -> {
                    15
                }
                Constants.Font.NORMAL -> {
                    17
                }
                Constants.Font.LARGE -> {
                    22
                }
            }
        )

        checkboxLock.isChecked = true
    }

    @SuppressLint("InflateParams")
    override fun initData() {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        noteId = intent.getLongExtra(Constants.noteId, 0L)
        val labelId = intent.getLongExtra(Constants.labelId, 0L)
        fetchNoteDataById()

        // Add label if use tap FAB button in Label Fragment
        if (labelId != 0L) {
            val labelNoteCrossRef = LabelNoteCrossRef(
                labelId,
                noteId!!
            )
            viewModel.insertLabelNoteCrossRef(labelNoteCrossRef)
        }

        richEditor.setOnInitialLoadListener { isReady ->
            if (isReady) {
                command_group_button.visibility = View.VISIBLE

                listenToRichEditorDecorationChange()
            }
        }

        viewModel.getNoteWithLabels(noteId!!)
        viewModel.noteWithLabelsLiveData.observe(this, Observer {
            chip_group.removeAllViews()
            if (it != null) {
                for (label in it.labels) {
                    val chip =
                        LayoutInflater.from(this).inflate(R.layout.chip_suncolor, null) as Chip
                    chip.text = label.title
                    chip_group.addView(chip)
                }
            }
        })

        checkboxLock.setOnCheckedChangeListener { buttonView, iconLocked ->
            if (iconLocked) {
                image_view_lock.visibility = View.GONE
            } else {
                image_view_lock.visibility = View.VISIBLE
            }
            if (!buttonView.isPressed) return@setOnCheckedChangeListener
            val lockPattern = sharePref.getString(Constants.lockPattern, null)
            if (lockPattern.isNullOrBlank()) {
                image_view_lock.visibility = View.GONE
                buttonView.isChecked = true
                startActivityForResult(
                    Intent(this, PrivacyActivity::class.java),
                    CHANGE_LOCK_STATUS_REQUEST_CODE
                )
            }
        }
    }

    private fun fetchNoteDataById() {
        if (noteId != 0L) {
            viewModel.getNoteById(noteId!!)
            viewModel.noteLiveData.observe(this, Observer {
                it.title?.let { string ->
                    edit_text_title.setText(string)
                    initTitle = it.title
                }
                richEditor.html = it.content
                createDate = it.createDate
                initContent = it.content
                locked = it.isLocked
                checkboxLock.isChecked = !it.isLocked
                modified_date.text = DateUtil.formatToString(
                    Constants.monthDayYear24HourPattern,
                    Date(it.modifiedDate * 1000)
                )
            })
        } else {
            // Why need noteTemp because: we need note with noteId = 0 to make a association between label's noteId
            noteTemp = NoteEntity(0, null, "", 0, 0, 0, false)
            viewModel.insertNote(noteTemp)
        }
    }

    private fun listenToRichEditorDecorationChange() {
        richEditor.setOnDecorationChangeListener { _, types ->
            resetCommandToolbar()
            for (type in types) {
                when (valueOf(type.name)) {
                    BOLD -> {
                        isBold = true
                        bold.setImageResource(R.drawable.ic_bold_enabled)
                    }
                    ITALIC -> {
                        isItalic = true
                        italic.setImageResource(R.drawable.ic_italic_enabled)
                    }
                    UNDERLINE -> {
                        isUnderline = true
                        underline.setImageResource(R.drawable.ic_underlined_enabled)
                    }
                    STRIKETHROUGH -> {
                        isStrikeThrough = true
                        strike_through.setImageResource(R.drawable.ic_strike_through_enabled)
                    }
                    SUBSCRIPT -> {
                    }
                    SUPERSCRIPT -> {
                    }
                    H1 -> {
                    }
                    H2 -> {
                    }
                    H3 -> {
                    }
                    H4 -> {
                    }
                    H5 -> {
                    }
                    H6 -> {
                    }
                    ORDEREDLIST -> {
                    }
                    UNORDEREDLIST -> {
                    }
                    JUSTIFYCENTER -> {
                    }
                    JUSTIFYFULL -> {
                    }
                    JUSTUFYLEFT -> {
                    }
                    JUSTIFYRIGHT -> {
                    }
                }
            }
        }
    }

    private fun resetCommandToolbar() {
        isBold = false
        isItalic = false
        isUnderline = false
        isStrikeThrough = false
        bold.setImageResource(R.drawable.ic_bold_disable)
        italic.setImageResource(R.drawable.ic_italic_disable)
        underline.setImageResource(R.drawable.ic_underlined_disable)
        strike_through.setImageResource(R.drawable.ic_strike_through_disable)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            bold -> {
                isBold = !isBold
                changeCommandToolbarIcon(
                    isBold,
                    bold,
                    R.drawable.ic_bold_enabled,
                    R.drawable.ic_bold_disable
                )
                richEditor.setBold()
            }
            italic -> {
                isItalic = !isItalic
                changeCommandToolbarIcon(
                    isItalic,
                    italic,
                    R.drawable.ic_italic_enabled,
                    R.drawable.ic_italic_disable
                )
                richEditor.setItalic()
            }
            underline -> {
                isUnderline = !isUnderline
                changeCommandToolbarIcon(
                    isUnderline,
                    underline,
                    R.drawable.ic_underlined_enabled,
                    R.drawable.ic_underlined_disable
                )
                richEditor.setUnderline()
            }
            strike_through -> {
                isStrikeThrough = !isStrikeThrough
                changeCommandToolbarIcon(
                    isStrikeThrough,
                    strike_through,
                    R.drawable.ic_strike_through_enabled,
                    R.drawable.ic_strike_through_disable
                )
                richEditor.setStrikeThrough()
            }
            label -> {
                startActivity(Intent(this, AddLabelActivity::class.java).apply {
                    noteId?.run {
                        putExtra(Constants.noteId, this)
                    }
                })
            }
            image -> {
                AttachmentDialogFragment.newInstance().show(fm, Constants.attachmentDialog)
            }
            notification -> {
                SetNotificationDialogFragment.newInstance(noteId, alarmTime)
                    .show(fm, Constants.notificationDialog)
            }
            back_button -> {
                onBackPressed()
            }
        }
    }

    private fun changeCommandToolbarIcon(
        value: Boolean,
        imgBtn: ImageButton,
        enableDrawable: Int,
        disableDrawable: Int
    ) {
        if (value)
            imgBtn.setImageResource(enableDrawable)
        else imgBtn.setImageResource(
            disableDrawable
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CHANGE_LOCK_STATUS_REQUEST_CODE -> {
                    checkboxLock.isChecked = false
                    image_view_lock.visibility = View.VISIBLE
                }
                REQUEST_FILE -> {
                    val dataUri = data.data
                    if (dataUri != null) {
                        val fileExtension =
                            MimeTypeMap.getSingleton()
                                .getExtensionFromMimeType(contentResolver.getType(dataUri))
                        AttachmentDialogFragment.createImageFile(this, fileExtension)
                        val inputStream = contentResolver.openInputStream(dataUri)!!
                        scope.launch {
                            inputStream.use { input ->
                                val file = File(AttachmentDialogFragment.currentPhotoPath)
                                FileOutputStream(file).use { output ->
                                    val buffer =
                                        ByteArray(4 * 1024) // or other buffer size
                                    var read: Int
                                    while (input.read(buffer).also { read = it } != -1) {
                                        output.write(buffer, 0, read)
                                    }
                                    output.flush()
                                    withContext(Dispatchers.Main) {
                                        richEditor.html = if (richEditor.html == null) {
                                            Constants.setImageStyleWebView(
                                                AttachmentDialogFragment.currentPhotoPath
                                            )
                                        } else
                                            richEditor.html + Constants.setImageStyleWebView(
                                                AttachmentDialogFragment.currentPhotoPath
                                            )
                                        (fm.findFragmentByTag(Constants.attachmentDialog) as? AttachmentDialogFragment)?.dismiss()
                                    }
                                }
                            }
                        }
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    richEditor.html = if (richEditor.html == null) {
                        Constants.setImageStyleWebView(
                            AttachmentDialogFragment.currentPhotoPath
                        )
                    } else
                        richEditor.html + Constants.setImageStyleWebView(
                            AttachmentDialogFragment.currentPhotoPath
                        )
                    (fm.findFragmentByTag(Constants.attachmentDialog) as? AttachmentDialogFragment)?.dismissAllowingStateLoss()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val (title, content) = getTitleAndContent()
        if (!content.isNullOrBlank()) {
            if (initTitle != title || initContent != content || checkboxLock.isChecked == locked) {
                viewModel.insertNote(
                    NoteEntity(
                        if (noteId != 0L) noteId else null,
                        title,
                        content,
                        createDate ?: DateUtil.getCurrentTimeInMillis(),
                        DateUtil.getCurrentTimeInMillis(),
                        0,
                        !checkboxLock.isChecked
                    )
                )
                if (noteId == 0L) {
                    viewModel.noteIdAddLiveData.observe(this, Observer {
                        viewModel.updateLabelNoteCrossRefNoteId(it, 0)
                        if (alarmTime != 0L) {
                            viewModel.insertAlarm(AlarmOfNoteEntity(null, it, alarmTime))
                            AlarmManagerUtil.setNotification(this, it, alarmManager, alarmTime)
                        }
                    })
                }
            }
        } else {
            if (noteId == 0L) viewModel.deleteLabelNoteCrossRefByNoteId(0)
        }
        if (this::noteTemp.isInitialized) viewModel.deleteNote(noteTemp)
        super.onBackPressed()
    }

    private fun getTitleAndContent(): Pair<String?, String?> {
        val title = if (edit_text_title.text.toString().isEmpty()) {
            null
        } else {
            edit_text_title.text.toString()
        }

        val content = richEditor.html
        return Pair(title, content)
    }

    fun setAlarm(time: Long) {
        alarmTime = time
    }
}