package com.example.synctime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.synctime.api.ApiService
import com.example.synctime.api.ScheduleItem
import com.example.synctime.api.ScheduleResponse
import com.example.synctime.utils.DeviceInfoProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScheduleActivity : AppCompatActivity() {

    private lateinit var rvSchedule: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)

        rvSchedule = findViewById(R.id.rvSchedule)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        rvSchedule.layoutManager = LinearLayoutManager(this)
        adapter = ScheduleAdapter(emptyList())
        rvSchedule.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            fetchSchedule()
        }

        fetchSchedule()
    }

    private fun fetchSchedule() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val apiService = ApiService.create()
        val request = mapOf("androidId" to androidId)

        apiService.getMySchedule(request).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.schedules ?: emptyList()
                    adapter.updateData(list)
                } else {
                    Toast.makeText(this@MyScheduleActivity, getString(R.string.schedule_load_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@MyScheduleActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class ScheduleAdapter(private var items: List<ScheduleItem>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShift: TextView = view.findViewById(R.id.tvShift)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.tvDate.text = context.getString(R.string.schedule_date, item.date)
        holder.tvShift.text = context.getString(R.string.schedule_shift, item.shift)
        holder.tvTime.text = context.getString(R.string.schedule_time, item.startTime, item.endTime)
        holder.tvNote.text = context.getString(R.string.schedule_note, item.note ?: context.getString(R.string.no_note))
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ScheduleItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
