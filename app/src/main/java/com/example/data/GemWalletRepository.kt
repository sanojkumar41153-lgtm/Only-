package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Context.gemDataStore: DataStore<Preferences> by preferencesDataStore(name = "gem_rewards_vault")

data class StoredWalletState(
    val gems: Int = 100,
    val totalEarnedGems: Int = 100,
    val dailyClaimsCount: Int = 0,
    val completedTaskIds: Set<String> = emptySet(),
    val unlockedPerkIds: Set<String> = emptySet(),
    val equippedPerkId: String? = null,
    val inviteShareClaimed: Boolean = false,
    val friendCodeRedeemed: Boolean = false,
    val transactions: List<GemTransaction> = emptyList(),
)

class GemWalletRepository(private val context: Context) {

    private object Keys {
        val GEMS = intPreferencesKey("gems_balance")
        val TOTAL_EARNED = intPreferencesKey("total_earned_gems")
        val DAILY_CLAIMS = intPreferencesKey("daily_claims_count")
        val COMPLETED_TASKS = stringSetPreferencesKey("completed_task_ids")
        val UNLOCKED_PERKS = stringSetPreferencesKey("unlocked_perk_ids")
        val EQUIPPED_PERK = stringPreferencesKey("equipped_perk_id")
        val INVITE_SHARE_CLAIMED = booleanPreferencesKey("invite_share_claimed")
        val FRIEND_CODE_REDEEMED = booleanPreferencesKey("friend_code_redeemed")
        val TRANSACTIONS = stringPreferencesKey("transactions_log")
    }

    val walletFlow: Flow<StoredWalletState> = context.gemDataStore.data.map { prefs ->
        StoredWalletState(
            gems = prefs[Keys.GEMS] ?: 100,
            totalEarnedGems = prefs[Keys.TOTAL_EARNED] ?: 100,
            dailyClaimsCount = prefs[Keys.DAILY_CLAIMS] ?: 0,
            completedTaskIds = prefs[Keys.COMPLETED_TASKS] ?: emptySet(),
            unlockedPerkIds = prefs[Keys.UNLOCKED_PERKS] ?: emptySet(),
            equippedPerkId = prefs[Keys.EQUIPPED_PERK]?.takeIf { it.isNotBlank() },
            inviteShareClaimed = prefs[Keys.INVITE_SHARE_CLAIMED] ?: false,
            friendCodeRedeemed = prefs[Keys.FRIEND_CODE_REDEEMED] ?: false,
            transactions = decodeTransactions(prefs[Keys.TRANSACTIONS].orEmpty()),
        )
    }

    suspend fun addDailyReward(amount: Int = 5) {
        context.gemDataStore.edit { prefs ->
            val current = prefs[Keys.GEMS] ?: 100
            val totalEarned = prefs[Keys.TOTAL_EARNED] ?: 100
            val claims = prefs[Keys.DAILY_CLAIMS] ?: 0
            prefs[Keys.GEMS] = current + amount
            prefs[Keys.TOTAL_EARNED] = totalEarned + amount
            prefs[Keys.DAILY_CLAIMS] = claims + 1
            prefs[Keys.TRANSACTIONS] = prependTransaction(
                existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                title = "Daily Demo Reward",
                amount = amount,
            )
        }
    }

    suspend fun completeTask(taskId: String, title: String, rewardGems: Int): Boolean {
        var awarded = false
        context.gemDataStore.edit { prefs ->
            val completed = prefs[Keys.COMPLETED_TASKS] ?: emptySet()
            if (!completed.contains(taskId)) {
                val current = prefs[Keys.GEMS] ?: 100
                val totalEarned = prefs[Keys.TOTAL_EARNED] ?: 100
                prefs[Keys.COMPLETED_TASKS] = completed + taskId
                prefs[Keys.GEMS] = current + rewardGems
                prefs[Keys.TOTAL_EARNED] = totalEarned + rewardGems
                prefs[Keys.TRANSACTIONS] = prependTransaction(
                    existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                    title = title,
                    amount = rewardGems,
                )
                awarded = true
            }
        }
        return awarded
    }

    suspend fun awardPreviewReward(amount: Int = 10) {
        context.gemDataStore.edit { prefs ->
            val current = prefs[Keys.GEMS] ?: 100
            val totalEarned = prefs[Keys.TOTAL_EARNED] ?: 100
            prefs[Keys.GEMS] = current + amount
            prefs[Keys.TOTAL_EARNED] = totalEarned + amount
            prefs[Keys.TRANSACTIONS] = prependTransaction(
                existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                title = "Rewarded Preview Bonus",
                amount = amount,
            )
        }
    }

    suspend fun claimInviteShareBonus(amount: Int = 10): Boolean {
        var awarded = false
        context.gemDataStore.edit { prefs ->
            val alreadyClaimed = prefs[Keys.INVITE_SHARE_CLAIMED] ?: false
            if (!alreadyClaimed) {
                val current = prefs[Keys.GEMS] ?: 100
                val totalEarned = prefs[Keys.TOTAL_EARNED] ?: 100
                prefs[Keys.INVITE_SHARE_CLAIMED] = true
                prefs[Keys.GEMS] = current + amount
                prefs[Keys.TOTAL_EARNED] = totalEarned + amount
                prefs[Keys.TRANSACTIONS] = prependTransaction(
                    existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                    title = "Invite Share Bonus",
                    amount = amount,
                )
                awarded = true
            }
        }
        return awarded
    }

    suspend fun redeemFriendCodeBonus(amount: Int = 15): Boolean {
        var awarded = false
        context.gemDataStore.edit { prefs ->
            val alreadyRedeemed = prefs[Keys.FRIEND_CODE_REDEEMED] ?: false
            if (!alreadyRedeemed) {
                val current = prefs[Keys.GEMS] ?: 100
                val totalEarned = prefs[Keys.TOTAL_EARNED] ?: 100
                prefs[Keys.FRIEND_CODE_REDEEMED] = true
                prefs[Keys.GEMS] = current + amount
                prefs[Keys.TOTAL_EARNED] = totalEarned + amount
                prefs[Keys.TRANSACTIONS] = prependTransaction(
                    existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                    title = "Referral Welcome Bonus",
                    amount = amount,
                )
                awarded = true
            }
        }
        return awarded
    }

    suspend fun unlockOrEquipPerk(perk: VaultPerk): String {
        var resultMessage = ""
        context.gemDataStore.edit { prefs ->
            val unlocked = prefs[Keys.UNLOCKED_PERKS] ?: emptySet()
            val currentGems = prefs[Keys.GEMS] ?: 100
            if (unlocked.contains(perk.id)) {
                prefs[Keys.EQUIPPED_PERK] = perk.id
                resultMessage = "Equipped ${perk.title}!"
            } else if (currentGems >= perk.costGems) {
                prefs[Keys.GEMS] = currentGems - perk.costGems
                prefs[Keys.UNLOCKED_PERKS] = unlocked + perk.id
                prefs[Keys.EQUIPPED_PERK] = perk.id
                prefs[Keys.TRANSACTIONS] = prependTransaction(
                    existing = prefs[Keys.TRANSACTIONS].orEmpty(),
                    title = "Unlocked ${perk.title}",
                    amount = -perk.costGems,
                )
                resultMessage = "Unlocked & equipped ${perk.title} (-${perk.costGems} Gems)!"
            } else {
                val needed = perk.costGems - currentGems
                resultMessage = "Need $needed more Gems for ${perk.title}."
            }
        }
        return resultMessage
    }

    suspend fun resetDemo() {
        context.gemDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun prependTransaction(existing: String, title: String, amount: Int): String {
        val safeTitle = title.replace("|", " ").replace("\n", " ")
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val entry = "${UUID.randomUUID()}|$safeTitle|$amount|$time"
        val currentLines = existing.lines().filter { it.isNotBlank() }
        return (listOf(entry) + currentLines).take(25).joinToString("\n")
    }

    private fun decodeTransactions(raw: String): List<GemTransaction> {
        if (raw.isBlank()) return emptyList()
        return raw.lines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size >= 4) {
                    GemTransaction(
                        id = parts[0],
                        title = parts[1],
                        amount = parts[2].toIntOrNull() ?: 0,
                        timeLabel = parts[3],
                    )
                } else {
                    null
                }
            }
    }
}
