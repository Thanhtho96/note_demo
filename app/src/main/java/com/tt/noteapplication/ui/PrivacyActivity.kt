package com.tt.noteapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.tt.noteapplication.R
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_privacy.*
import org.koin.android.ext.android.inject

class PrivacyActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {

    private var lockPattern: String? = null
    private var confirmPattern: String? = null
    private var isConfirmMode = false
    private val sharePref: SharedPreferences by inject()

    override fun layoutRes() = R.layout.activity_privacy

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
    }

    override fun setTheme() {
        setTheme(R.style.AppThemeDark)
    }

    override fun initView() {
    }

    override fun initData() {
        lockPattern = sharePref.getString(Constants.lockPattern, null)
        if (lockPattern.isNullOrBlank()) {
            text_privacy_description.text = getString(R.string.txt_please_draw_pattern)
        } else {
            text_privacy_description.text = getString(R.string.txt_please_draw_unlock_pattern)
        }
        pattern_lock_view.addPatternLockListener(patternLockViewListener)
    }

    private val patternLockViewListener: PatternLockViewListener =
        object : PatternLockViewListener {
            override fun onStarted() {
            }

            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
            }

            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                if (lockPattern.isNullOrBlank()) {
                    if (confirmPattern.isNullOrBlank())
                        confirmPattern =
                            PatternLockUtils.patternToString(pattern_lock_view, pattern)

                    if (!confirmPattern.isNullOrBlank() && isConfirmMode) {
                        if (PatternLockUtils.patternToString(
                                pattern_lock_view,
                                pattern
                            ) == confirmPattern
                        ) {
                            Toast.makeText(
                                this@PrivacyActivity,
                                "Pattern confirm match",
                                Toast.LENGTH_SHORT
                            ).show()
                            pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                            sharePref.edit().putString(
                                Constants.lockPattern,
                                PatternLockUtils.patternToString(pattern_lock_view, pattern)
                            ).apply()
                            Intent().apply {
                                putExtra(Constants.isUnlocked, true)
                                setResult(Activity.RESULT_OK, this)
                            }
                            finish()
                        } else {
                            Toast.makeText(
                                this@PrivacyActivity,
                                "Pattern confirm not match",
                                Toast.LENGTH_SHORT
                            ).show()
                            pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        }
                    }

                    if (!confirmPattern.isNullOrBlank() && lockPattern.isNullOrBlank() && !isConfirmMode) {
                        text_privacy_description.text =
                            getString(R.string.txt_please_confirm_pattern)
                        pattern_lock_view.clearPattern()
                        isConfirmMode = true
                    }
                } else {
                    if (PatternLockUtils.patternToString(
                            pattern_lock_view,
                            pattern
                        ) == lockPattern
                    ) {
                        Toast.makeText(
                            this@PrivacyActivity,
                            "Pattern unlock match",
                            Toast.LENGTH_SHORT
                        ).show()
                        pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                        Intent().apply {
                            putExtra(Constants.isUnlocked, true)
                            setResult(Activity.RESULT_OK, this)
                        }
                        finish()
                    } else {
                        Toast.makeText(
                            this@PrivacyActivity,
                            "Pattern unlock not match",
                            Toast.LENGTH_SHORT
                        ).show()
                        pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.WRONG)
                    }
                }
            }

            override fun onCleared() {
            }
        }

    override fun onClick(v: View?) {
        when (v) {
            back_button -> {
                onBackPressed()
            }
        }
    }
}