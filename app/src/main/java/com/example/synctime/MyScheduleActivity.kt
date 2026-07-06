package com.example.synctime

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.ScheduleDto
import com.example.synctime.utils.AuthManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScheduleActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Lịch làm việc của tôi"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        progressBar = ProgressBar(this).apply {
            visibility = View.VISIBLE
        }

        emptyText = TextView(this).apply {
            text = "Chưa có lịch làm việc"
            gravity = Gravity.CENTER
            textSize = 16f
            visibility = View.GONE
        }

        adapter = ScheduleAdapter()

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MyScheduleActivity)
            adapter = this@MyScheduleActivity.adapter
        }

        root.addView(
            progressBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            emptyText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            recyclerView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        loadMySchedule()
    }

    private fun loadMySchedule() {
        progressBar.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            progressBar.visibility = View.GONE

            Toast.makeText(
                this,
                "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        ApiClient.token = token

        val api = ApiClient.create(token)

        api.getMySchedulesCall().enqueue(object : Callback<List<ScheduleDto>> {
            override fun onResponse(
                call: Call<List<ScheduleDto>>,
                response: Response<List<ScheduleDto>>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val schedules = response.body().orEmpty()

                    if (schedules.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = "Chưa có lịch làm việc"
                    } else {
                        emptyText.visibility = View.GONE
                    }

                    adapter.submitList(schedules)
                } else {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "Không tải được lịch. Mã lỗi: ${response.code()}"

                    Toast.makeText(
                        this@MyScheduleActivity,
                        "Không tải được lịch: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ScheduleDto>>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Lỗi kết nối backend: ${t.message}"

                Toast.makeText(
                    this@MyScheduleActivity,
                    "Lỗi kết nối: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}

private class ScheduleAdapter : RecyclerView.Adapter<ScheduleViewHolder>() {

    private val items = mutableListOf<ScheduleDto>()

    fun submitList(newItems: List<ScheduleDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val root = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val title = TextView(parent.context).apply {
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val time = TextView(parent.context).apply {
            textSize = 15f
        }

        val status = TextView(parent.context).apply {
            textSize = 14f
        }

        root.addView(title)
        root.addView(time)
        root.addView(status)

        return ScheduleViewHolder(root, title, time, status)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = "${item.workDate} - ${item.shiftName}"
        holder.time.text = "Giờ làm: ${item.startTime} - ${item.endTime}"
        holder.status.text = "Trạng thái: ${item.status}"
    }

    override fun getItemCount(): Int = items.size
}

private class ScheduleViewHolder(
    itemView: View,
    val title: TextView,
    val time: TextView,
    val status: TextView
) : RecyclerView.ViewHolder(itemView)