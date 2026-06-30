package com.example.baitaplon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.baitaplon.api.ApiService
import com.example.baitaplon.api.LoginRequest
import com.example.baitaplon.api.LoginResponse
import com.example.baitaplon.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)
        if (preferenceManager.isLoggedIn()) {
            startMainActivity()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Pre-fill for demo
        etUsername.setText("demo")
        etPassword.setText("123456")

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val apiService = ApiService.create()
                val request = LoginRequest(username, password)

                apiService.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val token = response.body()?.token ?: ""
                            preferenceManager.saveAuthToken(token)
                            preferenceManager.saveUsername(username)
                            
                            Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                            startMainActivity()
                        } else {
                            val msg = response.body()?.message ?: "Lỗi"
                            Toast.makeText(this@LoginActivity, getString(R.string.login_failed, msg), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
