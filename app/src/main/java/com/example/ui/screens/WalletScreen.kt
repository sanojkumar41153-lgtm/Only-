package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppTab
import com.example.ui.GemRewardsUiState
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GemEmerald
import com.example.ui.theme.GemGold

@Composable
fun WalletScreen(
    uiState: GemRewardsUiState,
    onClaimDailyReward: () -> Unit,
    onOpenInvite: () -> Unit,
    onNavigateToTab: (AppTab) -> Unit,
    onStartRewardedPreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedGems by animateIntAsState(
        targetValue = uiState.gems,
        animationSpec = spring(stiffness = 380f),
        label = "gem_balance_counter",
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Hero Gem Wallet Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(236.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gem_hero),
                            contentDescription = "Luminous sapphire and emerald gems vault banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xBB080F1D),
                                            Color(0x990B162B),
                                            Color(0xEE080F1D),
                                        ),
                                    ),
                                ),
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0x660F172A),
                                    modifier = Modifier.border(
                                        width = 1.dp,
                                        color = GemCyan.copy(alpha = 0.45f),
                                        shape = RoundedCornerShape(50),
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Diamond,
                                            contentDescription = null,
                                            tint = GemCyan,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.wallet_title),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                        )
                                    }
                                }

                                if (uiState.equippedPerk != null) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = GemGold.copy(alpha = 0.2f),
                                        modifier = Modifier.border(
                                            width = 1.dp,
                                            color = GemGold.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(50),
                                        ),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Verified,
                                                contentDescription = "Equipped badge",
                                                tint = GemGold,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Text(
                                                text = uiState.equippedPerk.badgeTitle,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = GemGold,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "$animatedGems ${stringResource(R.string.gems_unit)}",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White,
                                    modifier = Modifier.testTag("gem_balance_text"),
                                )
                                AnimatedContent(
                                    targetState = uiState.statusMessage,
                                    label = "status_message_anim",
                                ) { msg ->
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE2E8F0),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.testTag("wallet_status_message"),
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ShareChip(
                                    label = stringResource(R.string.user_share_label),
                                    accent = GemEmerald,
                                )
                                ShareChip(
                                    label = stringResource(R.string.owner_share_label),
                                    accent = GemGold,
                                )
                            }
                        }
                    }
                }

                // Primary Daily Demo Reward Button
                Button(
                    onClick = onClaimDailyReward,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("daily_reward_button"),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.daily_reward_action),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                // Quick Actions Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Earn & Manage",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Claims: ${uiState.dailyClaimsCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // 2x2 Action Cards Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionTileCard(
                        title = stringResource(R.string.invite_friends_action),
                        subtitle = if (uiState.inviteShareClaimed) "Share code ${uiState.inviteCode}" else "+10 Gems on share",
                        icon = Icons.Filled.GroupAdd,
                        accentColor = GemCyan,
                        onClick = onOpenInvite,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("invite_friends_button"),
                    )
                    ActionTileCard(
                        title = stringResource(R.string.sponsored_tasks_action),
                        subtitle = "${uiState.completedTaskIds.size}/${uiState.tasks.size} completed",
                        icon = Icons.Filled.TaskAlt,
                        accentColor = GemEmerald,
                        onClick = { onNavigateToTab(AppTab.TASKS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sponsored_tasks_button"),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionTileCard(
                        title = stringResource(R.string.watch_ad_action),
                        subtitle = "5s timer • +10 Gems",
                        icon = Icons.Filled.PlayCircle,
                        accentColor = GemGold,
                        onClick = onStartRewardedPreview,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("watch_ad_button"),
                    )
                    ActionTileCard(
                        title = stringResource(R.string.withdrawal_action),
                        subtitle = "80/20 split & perks",
                        icon = Icons.Filled.AccountBalanceWallet,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTab(AppTab.VAULT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vault_split_button"),
                    )
                }

                // Transparent Split & Demo Mode Footer Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.owner_share_label),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(R.string.user_share_label),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        Text(
                            text = stringResource(R.string.demo_mode_notice),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.copyright_footer),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareChip(
    label: String,
    accent: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = 0.16f),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun ActionTileCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun InviteFriendsDialog(
    inviteCode: String,
    inviteShareClaimed: Boolean,
    friendCodeRedeemed: Boolean,
    onDismiss: () -> Unit,
    onInviteShared: () -> Unit,
    onRedeemFriendCode: (String) -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var friendCodeInput by remember { mutableStateOf("") }
    var copiedMessage by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = "Invite Friends",
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Share your Gem Rewards invite code. You earn +10 Demo Gems on your first share, and friends can try entering a referral code for +15 Demo Gems.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "YOUR CODE",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = inviteCode,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        IconButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(inviteCode))
                                copiedMessage = true
                            },
                            modifier = Modifier.testTag("copy_invite_code_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy invite code",
                            )
                        }
                    }
                }

                if (copiedMessage) {
                    Text(
                        text = "Invite code copied to clipboard.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                FilledTonalButton(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join me on Gem Rewards! Use demo invite code $inviteCode to collect virtual gems.",
                            )
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Gem Rewards"))
                        onInviteShared()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_invite_button"),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (inviteShareClaimed) "Share Invite Link" else "Share Invite (+10 Gems)",
                    )
                }

                if (!friendCodeRedeemed) {
                    OutlinedTextField(
                        value = friendCodeInput,
                        onValueChange = { friendCodeInput = it },
                        label = { Text("Have a friend's code?") },
                        placeholder = { Text("e.g. GEM-2026") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("friend_code_input"),
                    )
                    Button(
                        onClick = { onRedeemFriendCode(friendCodeInput) },
                        enabled = friendCodeInput.trim().length >= 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_friend_code_button"),
                    ) {
                        Text("Apply Code (+15 Gems)")
                    }
                } else {
                    Text(
                        text = "Welcome referral bonus already redeemed.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}
