package com.example.synctime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.synctime.navigation.AppNavGraph
import com.example.synctime.viewmodel.ManagerAdminViewModel
import com.example.synctime.viewmodel.ManagerAdminViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
            Tạm thời dùng token giả để chạy UI.
            Khi ghép với màn hình login của nhóm, truyền token thật sau login vào đây.
        */
        val demoToken = "demo_token"

        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()

                    val managerAdminViewModel: ManagerAdminViewModel = viewModel(
                        factory = ManagerAdminViewModelFactory(demoToken)
                    )

                    AppNavGraph(
                        navController = navController,
                        viewModel = managerAdminViewModel
                    )
                }
            }
        }
    }
}