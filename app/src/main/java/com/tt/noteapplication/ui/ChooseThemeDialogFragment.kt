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
import kotlinx.android.synthetic.main.fragment_choose_theme_dialog.*
import org.koin.android.ext.android.inject

private const val THEME_PARAM = "theme"

class ChooseThemeDialogFragment : DialogFragment(), View.OnClickListener {

    private val sharePref: SharedPreferences by inject()
    private var themeParam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            themeParam = it.getString(THEME_PARAM)
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
        return inflater.inflate(R.layout.fragment_choose_theme_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            if (!view.findViewById<RadioButton>(checkedId).isPressed) return@setOnCheckedChangeListener
            when (checkedId) {
                radioButtonLight.id -> {
                    sharePref.edit()
                        .putString(Constants.theme_sharePref, Constants.Theme.LIGHT.toString())
                        .apply()
                }
                radioButtonDark.id -> {
                    sharePref.edit()
                        .putString(Constants.theme_sharePref, Constants.Theme.DARK.toString())
                        .apply()
                }
                radioButtonSystemDefault.id -> {
                    sharePref.edit()
                        .putString(
                            Constants.theme_sharePref, Constants.Theme.SYSTEM_DEFAULT.toString()
                        ).apply()
                }
            }
            (activity as SettingActivity).changeTheme()
            dismiss()
        }

        when (Constants.Theme.valueOf(themeParam!!)) {
            Constants.Theme.LIGHT -> {
                radioButtonLight.isChecked = true
            }
            Constants.Theme.DARK -> {
                radioButtonDark.isChecked = true
            }
            Constants.Theme.SYSTEM_DEFAULT -> {
                radioButtonSystemDefault.isChecked = true
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
        fun newInstance(theme: String) =
            ChooseThemeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(THEME_PARAM, theme)
                }
            }
    }
}