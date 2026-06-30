package com.example.baitaplon

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.baitaplon.api.ApiService
import com.example.baitaplon.api.LeaveRequest
import com.example.baitaplon.api.LeaveResponse
import com.example.baitaplon.utils.DeviceInfoProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val rgType = findViewById<RadioGroup>(R.id.rgType)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etReason = findViewById<EditText>(R.id.etReason)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val type = if (findViewById<RadioButton>(R.id.rbLeave).isChecked) "Leave" else "Shift Change"
            val date = etDate.text.toString()
            val reason = etReason.text.toString()

            if (date.isNotEmpty() && reason.isNotEmpty()) {
                submitRequest(type, date, reason)
            } else {
                Toast.makeText(this, getString(R.string.request_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitRequest(type: String, date: String, reason: String) {
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val apiService = ApiService.create()
        val request = LeaveRequest(androidId, type, date, reason)

        apiService.createRequest(request).enqueue(object : Callback<LeaveResponse> {
            override fun onResponse(call: Call<LeaveResponse>, response: Response<LeaveResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@CreateRequestActivity, getString(R.string.request_success), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Error"
                    Toast.makeText(this@CreateRequestActivity, getString(R.string.request_failed, msg), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeaveResponse>, t: Throwable) {
                Toast.makeText(this@CreateRequestActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
