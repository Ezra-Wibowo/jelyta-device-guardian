package com.jelyta.deviceguardian.presentation.assistant

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantTranslatorScreen(
    viewModel: AssistantViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var userQuery by remember { mutableStateOf("") }
    var translateInput by remember { mutableStateOf("") }
    var targetLang by remember { mutableStateOf("English") }
    var showClearDialog by remember { mutableStateOf(false) }

    val chatListState = rememberLazyListState()

    // Auto-scroll chat list to bottom on new message
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            chatListState.animateScrollToItem(state.messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "⚡ Analisis RAM & Baterai",
        "🛡️ Periksa Keamanan HP",
        "🧹 Kiat Bersihkan Cache",
        "🚀 Cara Tingkatkan Kinerja"
    )

    val languages = listOf("English", "Indonesia", "Japanese", "Arabic", "Spanish")

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Bersihkan Riwayat Chat",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus seluruh pesan percakapan dengan AI Assistant?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearChatHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Hapus", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = CardSurface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini AI Assistant & Translator", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_chat_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = WarningOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chat Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AI Device Assistant Chat", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PrimaryCyan, strokeWidth = 2.dp)
                }
            }

            val context = LocalContext.current

            LazyColumn(
                state = chatListState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages) { msg ->
                    AlignBox(isUser = msg.isUser) {
                        Surface(
                            color = if (msg.isUser) PrimaryBlue else CardSurface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    msg.content,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f, fill = false),
                                    fontSize = 14.sp
                                )
                                if (!msg.isUser) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("AI Message", msg.content)
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "Pesan disalin ke papan klip", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy AI Message",
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Prompt Suggestion Chips (Scrollable LazyRow)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPrompts) { prompt ->
                    SuggestionChip(
                        onClick = {
                            viewModel.sendMessage(prompt)
                        },
                        label = {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryCyan
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = SurfaceDark
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = PrimaryCyan.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = userQuery,
                    onValueChange = { userQuery = it },
                    placeholder = { Text("Ask about RAM, battery...", color = TextSecondary) },
                    modifier = Modifier.weight(1f).testTag("assistant_query_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (userQuery.isNotBlank()) {
                            viewModel.sendMessage(userQuery)
                            userQuery = ""
                        }
                    },
                    modifier = Modifier.testTag("send_query_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryCyan)
                }
            }

            Divider(color = SurfaceDark)

            // Translator Section
            Text("Multilingual AI Translator", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            
            // Target Language Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(languages) { lang ->
                    FilterChip(
                        selected = targetLang == lang,
                        onClick = { targetLang = lang },
                        label = {
                            Text(
                                text = lang,
                                fontSize = 11.sp,
                                fontWeight = if (targetLang == lang) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = translateInput,
                    onValueChange = { translateInput = it },
                    placeholder = { Text("Enter text to translate ($targetLang)...", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryGreen,
                        unfocusedBorderColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = TextSecondary
                    )
                )
                Button(
                    onClick = { viewModel.translateText(translateInput, targetLang) },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = "Translate", tint = DarkBackground)
                }
            }

            state.translationResult?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Text(
                        res,
                        color = SecondaryGreen,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AlignBox(isUser: Boolean, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        content()
    }
}
