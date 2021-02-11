package com.tt.noteapplication.ui

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.tt.noteapplication.R
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.ThemeUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SettingActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {

    private val sharePref: SharedPreferences by inject()
    private lateinit var themeSetting: String
    private lateinit var fontSetting: String
    override fun layoutRes() = R.layout.activity_setting
    private val scope = CoroutineScope(SupervisorJob())

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun setTheme() {
    }

    override fun initView() {
    }

    override fun initData() {
        themeSetting =
            sharePref.getString(Constants.theme_sharePref, Constants.Theme.SYSTEM_DEFAULT.name)!!
        changeSubTextTheme()

        fontSetting =
            sharePref.getString(Constants.font_sharePref, Constants.Font.NORMAL.name)!!
        changeSubTextFont()
    }

    fun changeEditorFontSize() {
        fontSetting =
            sharePref.getString(Constants.font_sharePref, Constants.Font.NORMAL.name)!!
        changeSubTextFont()
    }

    fun changeTheme() {
        themeSetting = sharePref.getString(
            Constants.theme_sharePref,
            Constants.Theme.SYSTEM_DEFAULT.name
        )!!
        changeSubTextTheme()
        ThemeUtil.changeDefaultNightMode(themeSetting)
    }

    private fun changeSubTextTheme() {
        theme_selected.text = when (Constants.Theme.valueOf(themeSetting)) {
            Constants.Theme.LIGHT -> {
                getString(R.string.txt_light)
            }
            Constants.Theme.DARK -> {
                getString(R.string.txt_dark)
            }
            Constants.Theme.SYSTEM_DEFAULT -> {
                getString(R.string.txt_system_default)
            }
        }
    }

    private fun changeSubTextFont() {
        font_size_selected.text = when (Constants.Font.valueOf(fontSetting)) {
            Constants.Font.SMALL -> {
                getString(R.string.txt_small)
            }
            Constants.Font.NORMAL -> {
                getString(R.string.txt_normal)
            }
            Constants.Font.LARGE -> {
                getString(R.string.txt_large)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            backup_layout -> {
//                scope.launch(Dispatchers.IO) {
//
//                    val note = viewModel.getAllNotes()
//                    val label = viewModel.getAllLabels()
//                    val labelnotecrossref = viewModel.getAllLabelNote()
//                    val note_with_alarm = viewModel.getAllAlarms()
//
//                    val result = StringBuilder()
//
//                    result.append(Gson().toJson(note))
//                    result.append(Gson().toJson(label))
//                    result.append(Gson().toJson(labelnotecrossref))
//                    result.append(Gson().toJson(note_with_alarm))
//
//
//                    val file = FileOutputStream(
//                        createNoteJsonFile(this@SettingActivity).absolutePath
//                    )
//
//                    file.write(result.toString().toByteArray())
//                    file.close()
//                }
            }
            theme_layout -> {
                ChooseThemeDialogFragment.newInstance(themeSetting)
                    .show(supportFragmentManager, Constants.chooseThemeDialog)
            }
            font_size_layout -> {
                ChooseFontSizeDialogFragment.newInstance(fontSetting)
                    .show(supportFragmentManager, Constants.chooseFontDialog)
            }
            text_view_back -> {
                onBackPressed()
            }
        }
    }

    companion object {
        @Throws(IOException::class)
        fun createNoteJsonFile(context: Context): File {
            val timeStamp =
                SimpleDateFormat(
                    Constants.dateTimeSecondFileNamePattern,
                    Locale.getDefault()
                ).format(Date())
            val imageFileName = "BACK_UP_$timeStamp" + "_"
            val storageDir =
                context.getExternalFilesDir("Backup")

            return File.createTempFile(
                imageFileName,  /* prefix */
                ".json",  /* suffix */
                storageDir /* directory */
            )
        }
    }
}