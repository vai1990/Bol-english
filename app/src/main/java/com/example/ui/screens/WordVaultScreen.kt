package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VocabularyRepository
import com.example.model.ScreenRoute
import com.example.model.WordCategory
import com.example.model.WordItem
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.SkyBluePrimary
import com.example.viewmodel.MainViewModel

@Composable
fun WordVaultScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.vaultSearch.collectAsState()
    val selectedCategory by viewModel.vaultCategory.collectAsState()

    val filteredWords = VocabularyRepository.allWords.filter { word ->
        val matchesCategory = selectedCategory == null || word.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                word.word.contains(searchQuery, ignoreCase = true) ||
                word.marathiMeaning.contains(searchQuery, ignoreCase = true) ||
                word.marathiPronounce.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setVaultSearch(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vault_search_field"),
            placeholder = {
                Text(
                    text = "शब्द शोधा (Search word or meaning)…",
                    fontFamily = BalooFontFamily,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "शोधा",
                    tint = SkyBluePrimary
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selectedCategory == null) SkyBluePrimary else Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { viewModel.setVaultCategory(null) }
                ) {
                    Text(
                        text = "सर्व (${VocabularyRepository.allWords.size})",
                        fontFamily = BalooFontFamily,
                        fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (selectedCategory == null) Color.White else Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            items(WordCategory.values()) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) category.themeColor else Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { viewModel.setVaultCategory(category) }
                ) {
                    Text(
                        text = "${category.iconEmoji} ${category.marathiName}",
                        fontFamily = BalooFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Text(
            text = "${filteredWords.size} शब्द सापडले",
            fontFamily = BalooFontFamily,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        // Words List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredWords, key = { it.id }) { word ->
                VaultWordCard(
                    word = word,
                    onSpeak = { viewModel.speakWord(word.word) },
                    onPractice = {
                        viewModel.selectWord(word)
                        viewModel.navigateTo(ScreenRoute.SPEAK_PRACTICE)
                    }
                )
            }
        }
    }
}

@Composable
fun VaultWordCard(
    word: WordItem,
    onSpeak: () -> Unit,
    onPractice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_item_${word.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(word.category.themeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = word.emoji, fontSize = 28.sp)
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = word.word,
                            fontFamily = FredokaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "(${word.marathiPronounce})",
                            fontFamily = BalooFontFamily,
                            fontSize = 13.sp,
                            color = OrangeFun
                        )
                    }
                    Text(
                        text = "अर्थ: ${word.marathiMeaning}",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD))
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "ऐका",
                        tint = SkyBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onPractice,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "सराव करा",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
