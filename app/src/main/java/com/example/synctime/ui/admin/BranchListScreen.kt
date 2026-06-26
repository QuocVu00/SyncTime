package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun BranchListScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val branches by viewModel.branches.collectAsState()
    val message by viewModel.message.collectAsState()

    val bssidInputs = remember {
        mutableStateMapOf<Int, String>()
    }

    LaunchedEffect(Unit) {
        viewModel.loadBranches()
    }

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
            text = "Quản lý chi nhánh / BSSID",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotBlank()) {
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(branches) { branch ->

                val currentText = bssidInputs[branch.id] ?: branch.wifiBssid

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Mã chi nhánh: ${branch.id}")
                        Text("Tên chi nhánh: ${branch.name}")
                        Text("Địa chỉ: ${branch.address}")

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentText,
                            onValueChange = {
                                bssidInputs[branch.id] = it
                            },
                            label = {
                                Text("BSSID Wi-Fi")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.updateBranchBssid(
                                    branch = branch,
                                    newBssid = currentText
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cập nhật BSSID")
                        }
                    }
                }
            }
        }
    }
}