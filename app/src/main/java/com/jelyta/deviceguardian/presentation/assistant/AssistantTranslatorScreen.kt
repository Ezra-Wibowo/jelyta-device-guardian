package com.jelyta.deviceguardian.presentation.assistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini AI Assistant & Translator", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chat Section
            Text("AI Device Assistant Chat", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages) { msg ->
                    AlignBox(isUser = msg.isUser) {
                        Surface(
                            color = if (msg.isUser) PrimaryBlue else CardSurface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                msg.content,
                                color = TextPrimary,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
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
                        focusedTextColor = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(userQuery)
                        userQuery = ""
                    },
                    modifier = Modifier.testTag("send_query_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryCyan)
                }
            }

            Divider(color = SurfaceDark)

            // Translator Section
            Text("Multilingual AI Translator", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = translateInput,
                    onValueChange = { translateInput = it },
                    placeholder = { Text("Enter text to translate...", color = TextSecondary) },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.translateText(translateInput, targetLang) },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = DarkBackground)
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
