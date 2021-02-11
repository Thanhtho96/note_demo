package com.tt.noteapplication.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.tt.noteapplication.R
import com.tt.noteapplication.util.Constants
import kotlinx.android.synthetic.main.fragment_choose_font_size_dialog.*
import org.koin.android.ext.android.inject

private const val FONT_PARAM = "font"

class ChooseFontSizeDialogFragment : DialogFragment(), View.OnClickListener {

    private val sharePref: SharedPreferences by inject()
    private var fontSizeParam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fontSizeParam = it.getString(FONT_PARAM)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_font_size_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroupFontSize.setOnCheckedChangeListener { _, checkedId ->
            if (!view.findViewById<RadioButton>(checkedId).isPressed) return@setOnCheckedChangeListener
            when (checkedId) {
                radioButtonSmall.id -> {
                    sharePref.edit()
                        .putString(Constants.font_sharePref, Constants.Font.SMALL.toString())
                        .apply()
                }
                radioButtonNormal.id -> {
                    sharePref.edit()
                        .putString(Constants.font_sharePref, Constants.Font.NORMAL.toString())
                        .apply()
                }
                radioButtonLarge.id -> {
                    sharePref.edit()
                        .putString(
                            Constants.font_sharePref, Constants.Font.LARGE.toString()
                        ).apply()
                }
            }
            (activity as SettingActivity).changeEditorFontSize()
            dismiss()
        }

        when (Constants.Font.valueOf(fontSizeParam!!)) {
            Constants.Font.SMALL -> {
                radioButtonSmall.isChecked = true
            }
            Constants.Font.NORMAL -> {
                radioButtonNormal.isChecked = true
            }
            Constants.Font.LARGE -> {
                radioButtonLarge.isChecked = true
            }
        }

        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            cancel -> dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(fontSetting: String) =
            ChooseFontSizeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(FONT_PARAM, fontSetting)
                }
            }
    }
}
