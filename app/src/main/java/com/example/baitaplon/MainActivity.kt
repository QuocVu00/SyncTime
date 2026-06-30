package com.example.baitaplon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.baitaplon.utils.DeviceInfoProvider
import com.example.baitaplon.utils.PreferenceManager
import com.example.baitaplon.utils.WifiInfoProvider
import com.example.baitaplon.api.ApiService
import com.example.baitaplon.api.CheckInRequest
import com.example.baitaplon.api.CheckInResponse
import com.example.baitaplon.api.CheckOutRequest
import com.example.baitaplon.api.CheckOutResponse
import com.example.baitaplon.api.AttendanceStatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Màn hình chính của ứng dụng dành cho nhân viên.
 * Cho phép thực hiện các thao tác chấm công và điều hướng đến các tính năng khác.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var tvStatus: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvAndroidId = findViewById<TextView>(R.id.tvAndroidId)
        val tvBSSID = findViewById<TextView>(R.id.tvBSSID)
        tvStatus = findViewById(R.id.tvStatus)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)
        val btnCheckIn = findViewById<Button>(R.id.btnCheckIn)
        val btnCheckOut = findViewById<Button>(R.id.btnCheckOut)
        val btnViewSchedule = findViewById<Button>(R.id.btnViewSchedule)
        val btnCreateRequest = findViewById<Button>(R.id.btnCreateRequest)
        val btnViewRequests = findViewById<Button>(R.id.btnViewRequests)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)

        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_logout) {
                logout()
                true
            } else false
        }

        swipeRefresh.setOnRefreshListener {
            updateInfo(tvAndroidId, tvBSSID)
            fetchAttendanceStatus()
        }

        btnRefresh.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            } else {
                updateInfo(tvAndroidId, tvBSSID)
                fetchAttendanceStatus()
            }
        }

        btnCheckIn.setOnClickListener {
            performCheckIn()
        }

        btnCheckOut.setOnClickListener {
            performCheckOut()
        }

        btnViewSchedule.setOnClickListener {
            val intent = Intent(this, MyScheduleActivity::class.java)
            startActivity(intent)
        }

        btnCreateRequest.setOnClickListener {
            val intent = Intent(this, CreateRequestActivity::class.java)
            startActivity(intent)
        }

        btnViewRequests.setOnClickListener {
            val intent = Intent(this, ViewRequestsActivity::class.java)
            startActivity(intent)
        }

        // Initial fetch
        fetchAttendanceStatus()
    }

    private fun fetchAttendanceStatus() {
        swipeRefresh.isRefreshing = true
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val apiService = ApiService.create()
        val request = mapOf("androidId" to androidId)

        apiService.getAttendanceStatus(request).enqueue(object : Callback<AttendanceStatusResponse> {
            override fun onResponse(call: Call<AttendanceStatusResponse>, response: Response<AttendanceStatusResponse>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    val data = response.body()
                    val statusText = when (data?.status) {
                        "In progress" -> getString(R.string.status_in_progress, data.checkInTime)
                        "Finished" -> getString(R.string.status_finished, data.checkInTime, data.checkOutTime)
                        else -> getString(R.string.status_not_started)
                    }
                    tvStatus.text = getString(R.string.status_label, statusText)
                } else {
                    tvStatus.text = getString(R.string.status_label, getString(R.string.status_error))
                }
            }

            override fun onFailure(call: Call<AttendanceStatusResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                tvStatus.text = getString(R.string.status_label, getString(R.string.status_connection_error))
            }
        })
    }

    private fun performCheckIn() {
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val bssid = WifiInfoProvider.getBSSID(this)

        if (bssid == null || bssid == "Permission Denied" || bssid == "GPS Disabled" || bssid == "02:00:00:00:00:00") {
            val message = when(bssid) {
                "Permission Denied" -> getString(R.string.permission_denied)
                "GPS Disabled" -> "Vui lòng bật GPS để xác định vị trí WiFi"
                else -> getString(R.string.wifi_error)
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        val apiService = ApiService.create()
        val request = CheckInRequest(androidId, bssid)

        apiService.checkIn(request).enqueue(object : Callback<CheckInResponse> {
            override fun onResponse(
                call: Call<CheckInResponse>,
                response: Response<CheckInResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MainActivity, getString(R.string.check_in_success), Toast.LENGTH_SHORT).show()
                    fetchAttendanceStatus()
                } else {
                    val msg = response.body()?.message ?: "Error"
                    Toast.makeText(this@MainActivity, getString(R.string.check_in_failed, msg), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckInResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performCheckOut() {
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val bssid = WifiInfoProvider.getBSSID(this)

        if (bssid == null || bssid == "Permission Denied" || bssid == "GPS Disabled" || bssid == "02:00:00:00:00:00") {
            val message = when(bssid) {
                "Permission Denied" -> getString(R.string.permission_denied)
                "GPS Disabled" -> "Vui lòng bật GPS để xác định vị trí WiFi"
                else -> getString(R.string.wifi_error)
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        val apiService = ApiService.create()
        val request = CheckOutRequest(androidId, bssid)

        apiService.checkOut(request).enqueue(object : Callback<CheckOutResponse> {
            override fun onResponse(call: Call<CheckOutResponse>, response: Response<CheckOutResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MainActivity, getString(R.string.check_out_success), Toast.LENGTH_SHORT).show()
                    fetchAttendanceStatus()
                } else {
                    val msg = response.body()?.message ?: "Error"
                    Toast.makeText(this@MainActivity, getString(R.string.check_out_failed, msg), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckOutResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateInfo(tvAndroidId: TextView, tvBSSID: TextView) {
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val bssid = WifiInfoProvider.getBSSID(this) ?: "N/A"

        tvAndroidId.text = getString(R.string.android_id_label, androidId)
        tvBSSID.text = getString(R.string.bssid_label, bssid)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val tvAndroidId = findViewById<TextView>(R.id.tvAndroidId)
            val tvBSSID = findViewById<TextView>(R.id.tvBSSID)
            updateInfo(tvAndroidId, tvBSSID)
            fetchAttendanceStatus()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        PreferenceManager(this).clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
