package com.tt.noteapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.LabelAdapter
import com.tt.noteapplication.base.BaseActivity
import com.tt.noteapplication.model.LabelEntity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.ThemeUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.android.ext.android.inject

private const val PRIVACY_REQUEST_CODE = 7

class NavigationDrawerActivity : BaseActivity<NoteViewModel>(), View.OnClickListener {

    private val sharePref: SharedPreferences by inject()
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private val listLabels: MutableList<LabelEntity> = ArrayList()
    private lateinit var labelAdapter: LabelAdapter
    private var labelId: Long? = null
    private var fm = supportFragmentManager
    private lateinit var fragmentMainNote: Fragment
    private lateinit var fragmentNotification: Fragment
    private lateinit var activeFragment: Fragment

    override fun layoutRes() = R.layout.activity_navigation_drawer

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun setTheme() {
        ThemeUtil.changeDefaultNightMode(
            sharePref.getString(
                Constants.theme_sharePref,
                Constants.Theme.SYSTEM_DEFAULT.name
            )!!
        )
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(drawerToggle)

        generateLabelRecyclerView()

        fragmentMainNote = MainNoteFragment.newInstance()
        fragmentNotification = NotificationFragment.newInstance()

        activeFragment = fragmentMainNote

        for (fragment in fm.fragments) {
            fm.beginTransaction().remove(fragment!!).commit()
        }
        manipulateFragment()
    }

    override fun initData() {
        fetchAllLabel()
    }

    override fun onClick(v: View?) {
        when (v) {
            txt_privacy -> {
                startActivityForResult(
                    Intent(this, PrivacyActivity::class.java),
                    PRIVACY_REQUEST_CODE
                )
                return
            }
            txt_notification -> {
                if (activeFragment != fragmentNotification) {
                    fm.beginTransaction().hide(activeFragment).show(fragmentNotification).commit()
                    activeFragment = fragmentNotification
                }
                changeToolbarTitle(getString(R.string.txt_notification))
                drawer_layout.closeDrawers()
                activeFragment = fragmentNotification
                fab.visibility = View.GONE
                return
            }
            txt_create_label -> {
                startActivity(Intent(this, EditLabelActivity::class.java))
                drawer_layout.closeDrawers()
                return
            }
            fab -> {
                startActivity(Intent(this, NoteActivity::class.java).apply {
                    labelId?.let {
                        putExtra(Constants.labelId, it)
                    }
                })
            }
            txt_note -> {
                viewModel.changeTab(Constants.Tab.NOTE, 0)
                labelId = null
            }
            txt_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                drawer_layout.closeDrawers()
                return
            }
        }
        showMainNoteFragment()
        fab.visibility = View.VISIBLE
        drawer_layout.closeDrawers()
    }

    fun changeToolbarTitle(title: String) {
        toolbar.title = title
    }

    fun showMainNoteFragment() {
        if (activeFragment != fragmentMainNote) {
            fm.beginTransaction().hide(activeFragment).show(fragmentMainNote).commit()
            activeFragment = fragmentMainNote
        }
    }

    private fun manipulateFragment() {
        fm.beginTransaction()
            .add(R.id.main_host_fragment, fragmentNotification, Constants.fragmentNotification)
            .hide(fragmentNotification).commit()
        fm.beginTransaction()
            .add(R.id.main_host_fragment, fragmentMainNote, Constants.fragmentMainNote).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PRIVACY_REQUEST_CODE -> {
                    viewModel.changeTab(Constants.Tab.PRIVACY, 0)
                    fab.visibility = View.GONE
                    drawer_layout.closeDrawers()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fetchAllLabel() {
        viewModel.getAllLabel()
        viewModel.listLabelLiveData.observe(this, Observer {
            listLabels.clear()
            listLabels.addAll(it)
            labelAdapter.notifyDataSetChanged()
        })
    }

    private fun generateLabelRecyclerView() {
        labelAdapter = LabelAdapter(listLabels)
        labelAdapter.setOnItemClickListener(object : LabelAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val labelEntity = listLabels[position]
                labelId = labelEntity.labelId
                changeToolbarTitle(labelEntity.title)
                showMainNoteFragment()
                viewModel.changeTab(Constants.Tab.LABEL, labelEntity.labelId!!)
                fab.visibility = View.VISIBLE
                drawer_layout.closeDrawers()
            }
        })
        val layoutManager = LinearLayoutManager(this)
        recycler_view_label.layoutManager = layoutManager
        recycler_view_label.itemAnimator = DefaultItemAnimator()
        recycler_view_label.adapter = labelAdapter
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.action_search) {
            startActivity(Intent(this, SearchActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}