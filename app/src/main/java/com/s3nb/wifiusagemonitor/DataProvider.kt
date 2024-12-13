package com.s3nb.wifiusagemonitor

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DataProvider {
    companion object {
        @RequiresApi(Build.VERSION_CODES.M)
        fun getTotalUsage(context: Context): String {
            val c2 = Calendar.getInstance()
            c2.add(Calendar.YEAR, -100)
            val startOfCentury = c2.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis
            return getUsage(context, startOfCentury, end)
        }


        @RequiresApi(Build.VERSION_CODES.M)
        fun getMonthUsage(context: Context): String {
            val c2 = Calendar.getInstance()
            c2.add(Calendar.DATE, -30)
            val startOfMonth = c2.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis

            return getUsage(context, startOfMonth, end)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun getDayUsage(context: Context): String {
            val c = Calendar.getInstance()
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            c[Calendar.MILLISECOND] = 0
            val startOfToday = c.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis

            return getUsage(context, startOfToday, end)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun getUsage(context: Context, start: Long, end: Long): String {
            var downloads = 0L
            var uploads = 0L
            val bucket = NetworkStats.Bucket()
            val networkStatsManager =
                context.getSystemService(AppCompatActivity.NETWORK_STATS_SERVICE) as NetworkStatsManager


            val stats = networkStatsManager.queryDetails(
                ConnectivityManager.TYPE_WIFI,
                null,
                start,
                end
            )
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                downloads += bucket.rxBytes
                uploads += bucket.txBytes
            }
            val resultInMB = (downloads + uploads) / (1024 * 1024.0)
            return if (resultInMB > 999) {
                String.format(
                    Locale.US,
                    "%.2f",
                    resultInMB / 1000.0
                ) + " GB"
            } else {
                String.format(
                    Locale.US,
                    "%.0f",
                    resultInMB
                ) + " MB"
            }
        }
    }
}
