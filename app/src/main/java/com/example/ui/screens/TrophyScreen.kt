package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailableAvatars
import com.example.model.KidAvatar
import com.example.model.KidBadge
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.StarGold
import com.example.viewmodel.MainViewModel

@Composable
fun TrophyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val badges = viewModel.getAllBadges()

    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userProfile.kidName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kid Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with Role
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.selectedAvatar.emoji,
                        fontSize = 46.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = userProfile.kidName,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF0F172A)
                    )
                    IconButton(
                        onClick = {
                            tempName = userProfile.kidName
                            showNameDialog = true
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "नाव बदला",
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "${userProfile.selectedAvatar.marathiName} • ${userProfile.selectedAvatar.roleTitle}",
                    fontFamily = BalooFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Switcher Row
                Text(
                    text = "तुमचा आवडता मित्र निवडा:",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AvailableAvatars.forEach { avatar ->
                        val isChosen = avatar.id == userProfile.selectedAvatar.id
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isChosen) SkyBluePrimary.copy(alpha = 0.2f) else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isChosen) 2.dp else 1.dp,
                                    color = if (isChosen) SkyBluePrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.selectAvatar(avatar) }
                                .testTag("avatar_pick_${avatar.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatar.emoji, fontSize = 24.sp)
                        }
                    }
                }
            }
        }

        // Stats Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = "⭐",
                value = "${userProfile.totalStars}",
                label = "एकूण तारे",
                bg = Color(0xFFFFF8E1),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = "🎙️",
                value = "${userProfile.speechPracticeCount}",
                label = "उच्चार सराव",
                bg = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = "🏆",
                value = "${userProfile.quizzesWonCount}",
                label = "जिंकलेले खेळ",
                bg = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f)
            )
        }

        // Badges Section
        Text(
            text = "🏅 मिळालेली पदके (Trophies & Badges)",
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Color(0xFF0F172A)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            badges.forEach { badge ->
                BadgeRowItem(badge = badge)
            }
        }

        // Certificate Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
            border = androidx.compose.foundation.BorderStroke(2.dp, StarGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = OrangeFun,
                    modifier = Modifier.size(42.dp)
                )

                Text(
                    text = "इंग्लिश बालमित्र गौरव",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFE65100)
                )

                Text(
                    text = "विद्यार्थी: ${userProfile.kidName}\nउत्कृष्ट उच्चार आणि शब्दसंग्रहासाठी गौरविण्यात येत आहे!",
                    fontFamily = BalooFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = {
                Text(
                    text = "मुलाचे नाव बदला",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("नाव प्रविष्ट करा") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateKidName(tempName)
                        showNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Text("जतन करा (Save)", fontFamily = BalooFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("रद्द करा", fontFamily = BalooFontFamily)
                }
            }
        )
    }
}

@Composable
fun StatCard(
    icon: String,
    value: String,
    label: String,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = label,
                fontFamily = BalooFontFamily,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BadgeRowItem(badge: KidBadge) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_${badge.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (badge.isUnlocked) Color(0xFFFFF8E1) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                        fontSize = 24.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = badge.titleMarathi,
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (badge.isUnlocked) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                        Text(
                            text = "(${badge.titleEnglish})",
                            fontFamily = FredokaFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Text(
                        text = badge.description,
                        fontFamily = BalooFontFamily,
                        fontSize = 13.sp,
                        color = if (badge.isUnlocked) Color(0xFF475569) else Color(0xFF94A3B8)
                    )
                }
            }

            if (badge.isUnlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CorrectGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "मिळाले ✔️",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = CorrectGreen
                    )
                }
            }
        }
    }
}
