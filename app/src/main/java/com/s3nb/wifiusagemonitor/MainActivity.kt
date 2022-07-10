package com.s3nb.wifiusagemonitor

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    @SuppressLint("WrongThread", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isAccessGranted()) {
            grantPermission()
        } else {
            val tvToday = findViewById<TextView>(R.id.tv_usage_today)
            val tvTotal = findViewById<TextView>(R.id.tv_usage_total)
            val tvMonth = findViewById<TextView>(R.id.tv_usage_month)

            tvToday.text = DataProvider.getDayUsage(this)
            tvMonth.text = DataProvider.getMonthUsage(this)
            tvTotal.text = DataProvider.getTotalUsage(this)
        }
    }

    private fun grantPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            1
        )
    }


    private fun isAccessGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        )
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED && permission == PackageManager.PERMISSION_GRANTED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}