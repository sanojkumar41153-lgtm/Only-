package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppTab
import com.example.data.DefaultCatalog
import com.example.data.GemTransaction
import com.example.data.GemWalletRepository
import com.example.data.RewardTask
import com.example.data.VaultPerk
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GemRewardsUiState(
    val gems: Int = 100,
    val totalEarnedGems: Int = 100,
    val dailyClaimsCount: Int = 0,
    val statusMessage: String = "Welcome to Gem Rewards!",
    val selectedTab: AppTab = AppTab.WALLET,
    val tasks: List<RewardTask> = DefaultCatalog.sponsoredTasks,
    val completedTaskIds: Set<String> = emptySet(),
    val perks: List<VaultPerk> = DefaultCatalog.vaultPerks,
    val unlockedPerkIds: Set<String> = emptySet(),
    val equippedPerk: VaultPerk? = null,
    val inviteCode: String = "GEM-8020",
    val inviteShareClaimed: Boolean = false,
    val friendCodeRedeemed: Boolean = false,
    val transactions: List<GemTransaction> = emptyList(),
    val previewCountdownSeconds: Int? = null,
    val showInviteSheet: Boolean = false,
    val showTriviaDialog: Boolean = false,
) {
    val userShareGems: Int get() = (totalEarnedGems * 0.80).toInt()
    val ownerShareGems: Int get() = totalEarnedGems - userShareGems
}

class GemRewardsViewModel(
    private val repository: GemWalletRepository,
) : ViewModel() {

    private val localUiState = MutableStateFlow(LocalUiState())
    private var previewJob: Job? = null

    val uiState: StateFlow<GemRewardsUiState> = combine(
        repository.walletFlow,
        localUiState,
    ) { stored, local ->
        val equipped = DefaultCatalog.vaultPerks.find { it.id == stored.equippedPerkId }
        GemRewardsUiState(
            gems = stored.gems,
            totalEarnedGems = stored.totalEarnedGems,
            dailyClaimsCount = stored.dailyClaimsCount,
            statusMessage = local.statusMessage,
            selectedTab = local.selectedTab,
            tasks = DefaultCatalog.sponsoredTasks,
            completedTaskIds = stored.completedTaskIds,
            perks = DefaultCatalog.vaultPerks,
            unlockedPerkIds = stored.unlockedPerkIds,
            equippedPerk = equipped,
            inviteShareClaimed = stored.inviteShareClaimed,
            friendCodeRedeemed = stored.friendCodeRedeemed,
            transactions = stored.transactions,
            previewCountdownSeconds = local.previewCountdownSeconds,
            showInviteSheet = local.showInviteSheet,
            showTriviaDialog = local.showTriviaDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GemRewardsUiState(),
    )

    fun selectTab(tab: AppTab) {
        localUiState.update { it.copy(selectedTab = tab) }
    }

    fun claimDailyDemoReward() {
        viewModelScope.launch {
            repository.addDailyReward(5)
            localUiState.update { it.copy(statusMessage = "5 Demo Gems added!") }
        }
    }

    fun openInviteSheet() {
        localUiState.update { it.copy(showInviteSheet = true) }
    }

    fun dismissInviteSheet() {
        localUiState.update { it.copy(showInviteSheet = false) }
    }

    fun onInviteShared() {
        viewModelScope.launch {
            val awarded = repository.claimInviteShareBonus(10)
            localUiState.update {
                it.copy(
                    statusMessage = if (awarded) {
                        "Invite shared! +10 Demo Gems awarded."
                    } else {
                        "Invite link shared with friends!"
                    },
                )
            }
        }
    }

    fun redeemFriendCode(code: String) {
        val trimmed = code.trim().uppercase()
        if (trimmed.length < 4) {
            localUiState.update {
                it.copy(statusMessage = "Enter a friend code with at least 4 characters.")
            }
            return
        }
        if (trimmed == "GEM-8020") {
            localUiState.update {
                it.copy(statusMessage = "Enter a friend's code rather than your own code.")
            }
            return
        }
        viewModelScope.launch {
            val awarded = repository.redeemFriendCodeBonus(15)
            localUiState.update {
                it.copy(
                    statusMessage = if (awarded) {
                        "Friend code $trimmed applied! +15 Demo Gems added."
                    } else {
                        "Welcome referral bonus was already redeemed."
                    },
                    showInviteSheet = false,
                )
            }
        }
    }

    fun handleTaskClick(task: RewardTask) {
        if (uiState.value.completedTaskIds.contains(task.id)) {
            localUiState.update { it.copy(statusMessage = "${task.title} already completed.") }
            return
        }
        if (task.isTrivia) {
            localUiState.update { it.copy(showTriviaDialog = true) }
        } else {
            viewModelScope.launch {
                val awarded = repository.completeTask(task.id, task.title, task.rewardGems)
                if (awarded) {
                    localUiState.update {
                        it.copy(statusMessage = "${task.title} complete! +${task.rewardGems} Gems")
                    }
                }
            }
        }
    }

    fun dismissTriviaDialog() {
        localUiState.update { it.copy(showTriviaDialog = false) }
    }

    fun submitTriviaAnswer(selectedIndex: Int) {
        // Correct answer is index 1: "80% User / 20% Owner"
        if (selectedIndex == 1) {
            viewModelScope.launch {
                val awarded = repository.completeTask("task_split_trivia", "80/20 Share Trivia", 15)
                localUiState.update {
                    it.copy(
                        showTriviaDialog = false,
                        statusMessage = if (awarded) {
                            "Correct! +15 Demo Gems added."
                        } else {
                            "Trivia already completed."
                        },
                    )
                }
            }
        } else {
            localUiState.update {
                it.copy(statusMessage = "Not quite! Hint: Users receive the larger 80% share.")
            }
        }
    }

    fun startRewardedPreview() {
        if (previewJob?.isActive == true) return
        localUiState.update {
            it.copy(
                selectedTab = AppTab.TASKS,
                previewCountdownSeconds = 5,
                statusMessage = "Rewarded preview running (5s)...",
            )
        }
        previewJob = viewModelScope.launch {
            for (sec in 5 downTo 1) {
                localUiState.update { it.copy(previewCountdownSeconds = sec) }
                delay(1_000)
            }
            repository.awardPreviewReward(10)
            localUiState.update {
                it.copy(
                    previewCountdownSeconds = null,
                    statusMessage = "Rewarded preview complete! +10 Demo Gems added.",
                )
            }
        }
    }

    fun unlockOrEquipPerk(perk: VaultPerk) {
        viewModelScope.launch {
            val result = repository.unlockOrEquipPerk(perk)
            localUiState.update { it.copy(statusMessage = result) }
        }
    }

    fun resetDemoWallet() {
        previewJob?.cancel()
        viewModelScope.launch {
            repository.resetDemo()
            localUiState.update {
                LocalUiState(statusMessage = "Demo wallet reset to 100 Gems.")
            }
        }
    }

    private data class LocalUiState(
        val statusMessage: String = "Welcome to Gem Rewards!",
        val selectedTab: AppTab = AppTab.WALLET,
        val previewCountdownSeconds: Int? = null,
        val showInviteSheet: Boolean = false,
        val showTriviaDialog: Boolean = false,
    )

    class Factory(private val repository: GemWalletRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GemRewardsViewModel(repository) as T
        }
    }
}
