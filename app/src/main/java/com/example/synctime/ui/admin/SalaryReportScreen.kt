package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.viewmodel.ManagerAdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalaryReportScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val salaryList by viewModel.salary.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSalaryReport()
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        Text(
            text = "Bảng lương nhân viên",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotBlank()) {
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(salaryList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Mã nhân viên: ${item.userId}")
                        Text("Tên nhân viên: ${item.fullName}")
                        Text("Tổng giờ làm: ${item.totalHours}")
                        Text("Lương: ${currencyFormat.format(item.salary)}")
                    }
                }
            }
        }
    }
}