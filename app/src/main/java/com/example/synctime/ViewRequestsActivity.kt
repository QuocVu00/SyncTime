package com.example.synctime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.synctime.api.ApiService
import com.example.synctime.api.StaffRequestItem
import com.example.synctime.api.StaffRequestsResponse
import com.example.synctime.utils.DeviceInfoProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewRequestsActivity : AppCompatActivity() {

    private lateinit var rvRequests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: RequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_requests)

        rvRequests = findViewById(R.id.rvRequests)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        rvRequests.layoutManager = LinearLayoutManager(this)
        adapter = RequestsAdapter(emptyList())
        rvRequests.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            fetchRequests()
        }

        fetchRequests()
    }

    private fun fetchRequests() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        val androidId = DeviceInfoProvider.getAndroidId(this)
        val apiService = ApiService.create()
        val request = mapOf("androidId" to androidId)

        apiService.getMyRequests(request).enqueue(object : Callback<StaffRequestsResponse> {
            override fun onResponse(call: Call<StaffRequestsResponse>, response: Response<StaffRequestsResponse>) {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.requests ?: emptyList()
                    adapter.updateData(list)
                } else {
                    Toast.makeText(this@ViewRequestsActivity, getString(R.string.requests_load_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StaffRequestsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@ViewRequestsActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class RequestsAdapter(private var items: List<StaffRequestItem>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvRequestType)
        val tvDate: TextView = view.findViewById(R.id.tvRequestDate)
        val tvReason: TextView = view.findViewById(R.id.tvRequestReason)
        val tvStatus: TextView = view.findViewById(R.id.tvRequestStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.tvType.text = context.getString(R.string.request_type_label) + " " +
                (if (item.type == "Leave") context.getString(R.string.request_type_leave) else context.getString(R.string.request_type_change))
        holder.tvDate.text = context.getString(R.string.schedule_date, item.date)
        holder.tvReason.text = context.getString(R.string.request_reason_hint) + ": " + item.reason
        holder.tvStatus.text = context.getString(R.string.request_status_label, item.status)

        val statusColor = when (item.status) {
            "Approved" -> android.R.color.holo_green_dark
            "Rejected" -> android.R.color.holo_red_dark
            else -> android.R.color.holo_orange_dark
        }
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, statusColor))
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<StaffRequestItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
