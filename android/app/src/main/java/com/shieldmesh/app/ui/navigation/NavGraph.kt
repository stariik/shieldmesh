package com.shieldmesh.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shieldmesh.app.ui.screens.bounties.BountyScreen
import com.shieldmesh.app.ui.screens.dashboard.DashboardScreen
import com.shieldmesh.app.ui.screens.mesh.MeshScreen
import com.shieldmesh.app.ui.screens.scan.ScanScreen
import com.shieldmesh.app.ui.screens.threats.ThreatFeedScreen
import com.shieldmesh.app.ui.screens.wallet.WalletScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Scan : Screen("scan", "Scan", Icons.Default.QrCodeScanner)
    data object Threats : Screen("threats", "Threats", Icons.Default.BugReport)
    data object Bounties : Screen("bounties", "Bounties", Icons.Default.MonetizationOn)
    data object Wallet : Screen("wallet", "Wallet", Icons.Default.Wallet)
    data object Mesh : Screen("mesh", "Mesh", Icons.Default.Hub)
}

val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Scan,
    Screen.Threats,
    Screen.Bounties,
    Screen.Wallet,
    Screen.Mesh
)

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Scan.route) { ScanScreen() }
        composable(Screen.Threats.route) { ThreatFeedScreen() }
        composable(Screen.Bounties.route) { BountyScreen() }
        composable(Screen.Wallet.route) { WalletScreen() }
        composable(Screen.Mesh.route) { MeshScreen() }
    }
}
