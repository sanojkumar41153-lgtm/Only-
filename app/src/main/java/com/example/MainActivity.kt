package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppTab
import com.example.data.GemWalletRepository
import com.example.ui.GemRewardsViewModel
import com.example.ui.screens.EarnTasksScreen
import com.example.ui.screens.InviteFriendsDialog
import com.example.ui.screens.TriviaChallengeDialog
import com.example.ui.screens.VaultLedgerScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.GemRewardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GemWalletRepository(applicationContext)
        setContent {
            GemRewardsTheme {
                val viewModel: GemRewardsViewModel = viewModel(
                    factory = GemRewardsViewModel.Factory(repository),
                )
                GemRewardsRoot(viewModel = viewModel)
            }
        }
    }
}

private data class NavItem(
    val tab: AppTab,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
)

private val navItems = listOf(
    NavItem(
        tab = AppTab.WALLET,
        labelRes = R.string.nav_wallet,
        selectedIcon = Icons.Filled.Diamond,
        unselectedIcon = Icons.Outlined.Diamond,
        testTag = "nav_wallet",
    ),
    NavItem(
        tab = AppTab.TASKS,
        labelRes = R.string.nav_tasks,
        selectedIcon = Icons.Filled.TaskAlt,
        unselectedIcon = Icons.Outlined.TaskAlt,
        testTag = "nav_tasks",
    ),
    NavItem(
        tab = AppTab.VAULT,
        labelRes = R.string.nav_rewards,
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        testTag = "nav_vault",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemRewardsRoot(viewModel: GemRewardsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = uiState.selectedTab != AppTab.WALLET) {
        viewModel.selectTab(AppTab.WALLET)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        navItems.forEach { item ->
                            val selected = uiState.selectedTab == item.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(item.labelRes),
                                    )
                                },
                                label = { Text(stringResource(item.labelRes)) },
                                modifier = Modifier.testTag(item.testTag),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useRail) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        navItems.forEach { item ->
                            val selected = uiState.selectedTab == item.tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(item.labelRes),
                                    )
                                },
                                label = { Text(stringResource(item.labelRes)) },
                                modifier = Modifier.testTag(item.testTag),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (uiState.selectedTab) {
                        AppTab.WALLET -> WalletScreen(
                            uiState = uiState,
                            onClaimDailyReward = viewModel::claimDailyDemoReward,
                            onOpenInvite = viewModel::openInviteSheet,
                            onNavigateToTab = viewModel::selectTab,
                            onStartRewardedPreview = viewModel::startRewardedPreview,
                        )
                        AppTab.TASKS -> EarnTasksScreen(
                            uiState = uiState,
                            onStartRewardedPreview = viewModel::startRewardedPreview,
                            onTaskClick = viewModel::handleTaskClick,
                        )
                        AppTab.VAULT -> VaultLedgerScreen(
                            uiState = uiState,
                            onUnlockOrEquipPerk = viewModel::unlockOrEquipPerk,
                            onResetDemo = viewModel::resetDemoWallet,
                        )
                    }
                }
            }
        }
    }

    if (uiState.showInviteSheet) {
        InviteFriendsDialog(
            inviteCode = uiState.inviteCode,
            inviteShareClaimed = uiState.inviteShareClaimed,
            friendCodeRedeemed = uiState.friendCodeRedeemed,
            onDismiss = viewModel::dismissInviteSheet,
            onInviteShared = viewModel::onInviteShared,
            onRedeemFriendCode = viewModel::redeemFriendCode,
        )
    }

    if (uiState.showTriviaDialog) {
        TriviaChallengeDialog(
            onDismiss = viewModel::dismissTriviaDialog,
            onSubmitAnswer = viewModel::submitTriviaAnswer,
        )
    }
}
