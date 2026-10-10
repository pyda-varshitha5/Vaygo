package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TransitEnterexit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TransportType
import com.example.ui.theme.ElectricLavender
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.MidnightNavyCard
import com.example.ui.theme.MidnightNavyDark
import com.example.ui.theme.MidnightNavyElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.RichViolet
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.TravelViewModel
import com.example.ui.viewmodel.VaygoScreen

@Composable
fun HomeScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var voicePromptText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    // Speech recognizer launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your trip (e.g., Vizag to Hyderabad under ₹1500 tomorrow evening)")
            }
            try {
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { isListening = true }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { isListening = false }
                    override fun onError(error: Int) { isListening = false }
                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val spoken = matches?.firstOrNull() ?: ""
                        if (spoken.isNotBlank()) {
                            voicePromptText = spoken
                            viewModel.processNaturalLanguageQuery(spoken)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                recognizer.startListening(intent)
            } catch (e: Exception) {
                isListening = false
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vaygo_hero_banner_1791660545403),
                    contentDescription = "Vaygo Smart Travel Panorama",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MidnightNavyDark.copy(alpha = 0.9f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Every Journey, Smarter.",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Your Journey. Your Budget. Your Way.",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Natural Language & Voice Search Bar (Gemini Powered)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Natural Voice or Text Search",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isAiLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = voicePromptText,
                            onValueChange = { voicePromptText = it },
                            placeholder = {
                                Text(
                                    text = "e.g. Visakhapatnam to Hyderabad tomorrow evening under ₹1500",
                                    color = TextMutedDark,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("natural_language_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MidnightNavyElevated,
                                unfocusedContainerColor = MidnightNavyElevated,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = MidnightNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Mic button
                        IconButton(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isListening) Color(0xFFEF4444) else RichViolet)
                                .testTag("voice_search_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Quick AI prompts chips
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val prompts = listOf(
                            "Vizag to Hyderabad under ₹1500 sleeper",
                            "Bengaluru to Vijayawada tomorrow morning",
                            "Chennai to Bengaluru fastest train"
                        )
                        items(prompts) { p ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MidnightNavyElevated)
                                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(20.dp))
                                    .clickable {
                                        voicePromptText = p
                                        viewModel.processNaturalLanguageQuery(p)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = p,
                                    color = ElectricLavender,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Trip Planner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MidnightNavyBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize Your Journey",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time transport inventory matched with your exact budget",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Origin & Destination Inputs with Swap Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FROM", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = searchQuery.origin,
                                onValueChange = { viewModel.updateSearchQuery(searchQuery.copy(origin = it)) },
                                modifier = Modifier.fillMaxWidth().testTag("origin_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MidnightNavyElevated,
                                    unfocusedContainerColor = MidnightNavyElevated,
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = MidnightNavyBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true
                            )
                        }

                        IconButton(
                            onClick = { viewModel.swapOriginDestination() },
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MidnightNavyElevated)
                                .border(1.dp, MidnightNavyBorder, CircleShape)
                                .testTag("swap_locations_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Origin and Destination",
                                tint = NeonCyan
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("TO", color = ElectricLavender, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = searchQuery.destination,
                                onValueChange = { viewModel.updateSearchQuery(searchQuery.copy(destination = it)) },
                                modifier = Modifier.fillMaxWidth().testTag("destination_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MidnightNavyElevated,
                                    unfocusedContainerColor = MidnightNavyElevated,
                                    focusedBorderColor = ElectricLavender,
                                    unfocusedBorderColor = MidnightNavyBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Popular Indian City Pairs
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("POPULAR ROUTES IN INDIA", color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val routes = listOf(
                            Pair("Visakhapatnam", "Hyderabad"),
                            Pair("Bengaluru", "Vijayawada"),
                            Pair("Chennai", "Bengaluru"),
                            Pair("Mumbai", "Pune"),
                            Pair("Delhi", "Jaipur")
                        )
                        items(routes) { (from, to) ->
                            val isSelected = searchQuery.origin.equals(from, true) && searchQuery.destination.equals(to, true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) RichViolet else MidnightNavyElevated)
                                    .border(1.dp, if (isSelected) NeonCyan else MidnightNavyBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setQuickCity(from, to) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$from → $to",
                                    color = if (isSelected) Color.White else TextSecondaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date & Departure Time Chips
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DATE", color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Today", "Tomorrow").forEach { d ->
                                    val sel = searchQuery.date == d
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) NeonCyan else MidnightNavyElevated)
                                            .clickable { viewModel.updateSearchQuery(searchQuery.copy(date = d)) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = d,
                                            color = if (sel) MidnightNavyDark else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Travelers Counter
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TRAVELERS", color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MidnightNavyElevated)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (searchQuery.travelers > 1) {
                                            viewModel.updateSearchQuery(searchQuery.copy(travelers = searchQuery.travelers - 1))
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${searchQuery.travelers}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = {
                                        if (searchQuery.travelers < 6) {
                                            viewModel.updateSearchQuery(searchQuery.copy(travelers = searchQuery.travelers + 1))
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Budget Slider (Total Trip Budget)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL TRIP BUDGET",
                            color = TextMutedDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${searchQuery.totalBudget}",
                            color = NeonCyanGlow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Slider(
                        value = searchQuery.totalBudget.toFloat(),
                        onValueChange = { newBudget ->
                            viewModel.updateSearchQuery(searchQuery.copy(totalBudget = newBudget.toInt()))
                        },
                        valueRange = 500f..5000f,
                        steps = 45,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = MidnightNavyBorder
                        ),
                        modifier = Modifier.testTag("budget_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("₹500 (Economy)", color = TextMutedDark, fontSize = 10.sp)
                        Text("₹2,500 (Comfort)", color = TextMutedDark, fontSize = 10.sp)
                        Text("₹5,000 (Premium)", color = TextMutedDark, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transport Mode Filters
                    Text("TRANSPORT MODES", color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf(
                            Triple(TransportType.BUS, "Bus", Icons.Default.DirectionsBus),
                            Triple(TransportType.TRAIN, "Train", Icons.Default.Train),
                            Triple(TransportType.CAB, "Cab", Icons.Default.DirectionsCar),
                            Triple(TransportType.MULTIMODAL, "Combo", Icons.Default.TransitEnterexit)
                        )
                        modes.forEach { (mode, label, icon) ->
                            val isSelected = searchQuery.transportModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) RichViolet else MidnightNavyElevated)
                                    .border(1.dp, if (isSelected) NeonCyan else MidnightNavyBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val set = searchQuery.transportModes.toMutableSet()
                                        if (isSelected) {
                                            if (set.size > 1) set.remove(mode)
                                        } else {
                                            set.add(mode)
                                        }
                                        viewModel.updateSearchQuery(searchQuery.copy(transportModes = set))
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color.White else TextSecondaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else TextSecondaryDark,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary CTA: Plan My Journey
                    Button(
                        onClick = {
                            viewModel.performSearch(searchQuery)
                            viewModel.navigateTo(VaygoScreen.SEARCH_RESULTS)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RichViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("plan_my_journey_cta")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Find Best Plans in Budget (₹${searchQuery.totalBudget})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Feature Highlight: Journey Guardian Disruption Assistant
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(VaygoScreen.JOURNEY_TRACKING) },
                colors = CardDefaults.cardColors(containerColor = MidnightNavyCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x2210B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Guardian",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Vaygo Journey Guardian™",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldSuccess)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("ACTIVE", color = MidnightNavyDark, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Automatic 40-minute stationary stop alert & instant replacement rebooking if disrupted.",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
