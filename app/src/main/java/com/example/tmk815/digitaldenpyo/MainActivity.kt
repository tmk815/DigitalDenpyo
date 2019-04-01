package com.example.tmk815.digitaldenpyo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mTabsPagerAdapter: TabsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //タブで利用するFragmentのリストを作成
        val tabsFragments = arrayListOf(
            VoucherFragment::class.java,
            NewOrderFragment::class.java
        )

        //Adapterの生成
        mTabsPagerAdapter = TabsPagerAdapter(supportFragmentManager, tabsFragments)

        // ViewPagerにAdapterを設定
        container.adapter = mTabsPagerAdapter

        //ViewPagerのリスナを設定
        container.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                /*when (position) {
                    0 -> changeColor(Color.GRAY)
                    1 -> changeColor(Color.BLUE)
                }*/

            }
        })
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    //ステータスバー、アクションバー、タブの色変更
    @SuppressLint("ObsoleteSdkInt")
    private fun changeColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
        tabs.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}