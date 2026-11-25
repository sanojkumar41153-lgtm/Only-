package com.example.data

enum class AppTab {
    WALLET,
    TASKS,
    VAULT,
}

data class GemTransaction(
    val id: String,
    val title: String,
    val amount: Int,
    val timeLabel: String,
)

data class RewardTask(
    val id: String,
    val title: String,
    val description: String,
    val rewardGems: Int,
    val actionLabel: String,
    val isTrivia: Boolean = false,
)

data class VaultPerk(
    val id: String,
    val title: String,
    val description: String,
    val costGems: Int,
    val badgeTitle: String,
)

object DefaultCatalog {
    val sponsoredTasks = listOf(
        RewardTask(
            id = "task_profile_checkin",
            title = "Vault Security Check-In",
            description = "Verify your local demo vault status and claim a quick starter boost.",
            rewardGems = 10,
            actionLabel = "Complete +10",
        ),
        RewardTask(
            id = "task_split_trivia",
            title = "80/20 Share Trivia",
            description = "Answer one quick question about how Gem Rewards splits earnings.",
            rewardGems = 15,
            actionLabel = "Answer +15",
            isTrivia = true,
        ),
        RewardTask(
            id = "task_explorer_tour",
            title = "Explore Vault Perks",
            description = "Check out the virtual badges & transparent split ledger.",
            rewardGems = 10,
            actionLabel = "Claim +10",
        ),
    )

    val vaultPerks = listOf(
        VaultPerk(
            id = "perk_sapphire",
            title = "Sapphire Emblem",
            description = "Display a radiant Sapphire Collector badge on your Wallet card.",
            costGems = 50,
            badgeTitle = "Sapphire Collector",
        ),
        VaultPerk(
            id = "perk_emerald",
            title = "Emerald Crown",
            description = "Equip an Emerald Crown tier badge inside your Gem Vault.",
            costGems = 120,
            badgeTitle = "Emerald Crown",
        ),
        VaultPerk(
            id = "perk_golden",
            title = "Golden Vault Crest",
            description = "Unlock the prestigious Golden Crest title for top gem savers.",
            costGems = 200,
            badgeTitle = "Golden Crest",
        ),
    )
}
