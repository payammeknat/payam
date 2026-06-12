package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.viewmodel.PsychologyViewModel

// Modern Premium Colors
val PrimaryColor = Color(0xFF1E3A8A)       // Deep Indigo Royal
val PrimaryLight = Color(0xFF3B82F6)       // Vibrant Blue Accent
val SecondarySlate = Color(0xFF475569)     // Slate
val LightCoral = Color(0xFFF07167)         // Warm alert
val MintGreen = Color(0xFF2EC4B6)          // Healthy green
val AmberWarning = Color(0xFFFFB703)       // Warning amber
val PaleBackground = Color(0xFFF8FAFC)     // Elegant surface bg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PsychologyViewModel) {
    // Force RTL for Farsi Language
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        
        val candidates by viewModel.candidates.collectAsState()
        val selectedCandidate by viewModel.selectedCandidate.collectAsState()
        val comparisonsRole by viewModel.comparisonsRole.collectAsState()
        
        val isTestActive by viewModel.isTestActive.collectAsState()
        val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
        val tempAnswers by viewModel.tempAnswers.collectAsState()
        val testTakerName by viewModel.testTakerName.collectAsState()
        
        val aiResult by viewModel.aiResult.collectAsState()
        val aiLoading by viewModel.aiLoading.collectAsState()
        val errorMessage by viewModel.errorMessage.collectAsState()
        
        var currentTab by remember { mutableStateOf(0) } // 0: Dashboard, 1: Take Test, 2: Role Matching

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = "ارزیابی روانشناسی صنعتی و سازمانی",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "سامانه هوشمند هوش مصنوعی و تطبیق شغلی",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryColor
                    )
                )
            },
            bottomBar = {
                if (!isTestActive) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0; viewModel.clearSelectedCandidate() },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "داشبورد") },
                            label = { Text("داشبورد کاندیداها", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1; viewModel.clearSelectedCandidate() },
                            icon = { Icon(Icons.Default.AddAlert, contentDescription = "آزمون جدید") },
                            label = { Text("آزمون روانسنجی", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2; viewModel.clearSelectedCandidate() },
                            icon = { Icon(Icons.Default.CompareArrows, contentDescription = "تطبیق مشاغل") },
                            label = { Text("شایستگی کاندیداها", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PaleBackground)
                    .padding(innerPadding)
            ) {
                if (isTestActive) {
                    // Force display test interface if active
                    TestInterfaceView(
                        takerName = testTakerName,
                        questionIndex = currentQuestionIndex,
                        answers = tempAnswers,
                        errorMessage = errorMessage,
                        onAnswer = { qId, score -> viewModel.selectAnswer(qId, score) },
                        onNext = { viewModel.nextQuestion() },
                        onPrev = { viewModel.prevQuestion() },
                        onCancel = { viewModel.cancelTest() },
                        onComplete = { viewModel.completeTest() }
                    )
                } else {
                    when (currentTab) {
                        0 -> {
                            if (selectedCandidate != null) {
                                CandidateDetailView(
                                    candidate = selectedCandidate!!,
                                    aiResult = aiResult,
                                    aiLoading = aiLoading,
                                    errorMessage = errorMessage,
                                    onBack = { viewModel.clearSelectedCandidate() },
                                    onRunAI = { viewModel.runGeminiAnalysis(it) },
                                    onDelete = { viewModel.deleteCandidate(it) }
                                )
                            } else {
                                DashboardView(
                                    candidates = candidates,
                                    onCandidateClick = { viewModel.selectCandidate(it) },
                                    onStartTestTab = { currentTab = 1 }
                                )
                            }
                        }
                        1 -> {
                            NewTestRegistrationView(
                                onStartTest = { name -> viewModel.startTest(name) }
                            )
                        }
                        2 -> {
                            RoleMatchingView(
                                candidates = candidates,
                                currentRole = comparisonsRole,
                                onRoleSelected = { viewModel.selectComparisonsRole(it) },
                                onCandidateClick = { viewModel.selectCandidate(it); currentTab = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ======================== DASHBOARD VIEW ========================
@Composable
fun DashboardView(
    candidates: List<Candidate>,
    onCandidateClick: (Candidate) -> Unit,
    onStartTestTab: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // University Information Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryColor, PrimaryLight)
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "دانشگاه شهید چمران اهواز",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "دانشکده علوم تربیتی و روانشناسی - سمینار روانشناسی صنعتی و سازمانی",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "پروژه تحقیقاتی علمی و ارزیابی شایستگی شغلی",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "طراح برنامه: پیام مکنت‌خواه",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Summary Stats Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("کاندیداهای ارزیابی", fontSize = 11.sp, color = SecondarySlate)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = candidates.size.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("آزمون معتبر مرجع", fontSize = 11.sp, color = SecondarySlate)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MintGreen.copy(alpha = 0.15f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "استاندارد I-O",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor
                            )
                        }
                    }
                }
            }
        }

        // Candidates List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لیست افراد ارزیابی شده در این پروژه",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                TextButton(onClick = onStartTestTab) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("آزمون جدید", fontSize = 13.sp)
                }
            }
        }

        if (candidates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = SecondarySlate.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "هیچ پرونده ارزیابی ثبت نشده است.",
                            fontSize = 14.sp,
                            color = SecondarySlate
                        )
                    }
                }
            }
        } else {
            items(candidates) { candidate ->
                CandidateListItem(
                    candidate = candidate,
                    onClick = { onCandidateClick(candidate) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateListItem(candidate: Candidate, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("candidate_item_${candidate.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = candidate.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ScoreTextTiny("اضطراب: ${candidate.anxietyScore}", LightCoral)
                        ScoreTextTiny("تاب‌آوری: ${candidate.resilienceScore}", MintGreen)
                        ScoreTextTiny("توانمندی: ${candidate.jobSecurityScore}", PrimaryLight)
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "مشاهده جزئیات",
                tint = SecondarySlate.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ScoreTextTiny(text: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, fontSize = 10.sp, color = backgroundColor, fontWeight = FontWeight.SemiBold)
    }
}

// ======================== NEW TEST REGISTRATION VIEW ========================
@Composable
fun NewTestRegistrationView(onStartTest: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ثبت‌نام کاندیدای ارزیابی جدید",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "آزمون روانشناسی صنعتی با ۱۵ معیار سنجش بین‌المللی",
                    fontSize = 12.sp,
                    color = SecondarySlate,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        validationError = false
                    },
                    label = { Text("نام و نام خانوادگی کاندیدا") },
                    isError = validationError,
                    placeholder = { Text("مثال: مهدی رضایی") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("name_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    }
                )
                if (validationError) {
                    Text(
                        text = "وارد کردن نام کاندیدا جهت شروع آزمون الزامی است.",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.trim().isEmpty()) {
                            validationError = true
                        } else {
                            onStartTest(name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_test_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ورود به آزمون و شروع ارزیابی", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SecondarySlate, modifier = Modifier.size(16.dp))
                    Text(
                        text = "نمرات آزمون در دیتابیس لوکال ذخیره شده و حفظ اطلاعات شخصی تضمین می‌شود.",
                        fontSize = 10.sp,
                        color = SecondarySlate
                    )
                }
            }
        }
    }
}

// ======================== ACTIVE TEST INTERFACE ========================
@Composable
fun TestInterfaceView(
    takerName: String,
    questionIndex: Int,
    answers: Map<Int, Int>,
    errorMessage: String?,
    onAnswer: (Int, Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    val totalQuestions = IOPsychologyTest.questions.size
    val currentQuestion = IOPsychologyTest.questions[questionIndex]
    val selectedScore = answers[currentQuestion.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Test Header Status
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("کاندیدا: $takerName", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when(currentQuestion.category) {
                            PsychQuestion.Category.ANXIETY -> "بخش ۱: اضطراب شغلی (درحال سنجش)"
                            PsychQuestion.Category.RESILIENCE -> "بخش ۲: مکانیزم‌های تاب‌آوری (درحال سنجش)"
                            PsychQuestion.Category.JOB_SECURITY -> "بخش ۳: ثبات و توانمندی شغلی (درحال سنجش)"
                        },
                        fontSize = 11.sp,
                        color = PrimaryLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(PrimaryColor.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${questionIndex + 1} از $totalQuestions",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Beautiful Gradient Progress Indicator
            val progress = (questionIndex + 1).toFloat() / totalQuestions.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryLight,
                trackColor = PaleBackground,
            )
        }

        // Active Question Container
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Circle category tag
                Box(
                    modifier = Modifier
                        .background(
                            when (currentQuestion.category) {
                                PsychQuestion.Category.ANXIETY -> LightCoral.copy(alpha = 0.15f)
                                PsychQuestion.Category.RESILIENCE -> MintGreen.copy(alpha = 0.15f)
                                PsychQuestion.Category.JOB_SECURITY -> PrimaryLight.copy(alpha = 0.15f)
                            },
                            CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (currentQuestion.category) {
                            PsychQuestion.Category.ANXIETY -> "اضطراب شغلی 💻"
                            PsychQuestion.Category.RESILIENCE -> "تاب‌آوری 🛡️"
                            PsychQuestion.Category.JOB_SECURITY -> "امنیت و توانایی مهارتی ⚡"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentQuestion.category) {
                            PsychQuestion.Category.ANXIETY -> LightCoral
                            PsychQuestion.Category.RESILIENCE -> MintGreen
                            PsychQuestion.Category.JOB_SECURITY -> PrimaryColor
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentQuestion.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    lineHeight = 26.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = PaleBackground)
                Spacer(modifier = Modifier.height(16.dp))

                // Options (Likert Scale 1 to 4)
                val options = listOf(
                    1 to "هرگز (کاملاً مخالف)",
                    2 to "گاهی اوقات (مخالف نسبی)",
                    3 to "بیشتر اوقات (موافق نسبی)",
                    4 to "همیشه (کاملاً موافق)"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    options.forEach { (score, label) ->
                        val isSelected = selectedScore == score
                        OutlinedCard(
                            onClick = { onAnswer(currentQuestion.id, score) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("option_${currentQuestion.id}_$score"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryColor.copy(alpha = 0.08f) else Color.White
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryColor else SecondarySlate.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = if (isSelected) PrimaryColor else SecondarySlate,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onAnswer(currentQuestion.id, score) },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Stepper Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cancel
            OutlinedButton(
                onClick = onCancel,
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("لغو آزمون")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Prev
                if (questionIndex > 0) {
                    OutlinedButton(
                        onClick = onPrev,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("قبلی")
                    }
                }

                // Next or Complete
                if (questionIndex < totalQuestions - 1) {
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        enabled = selectedScore != null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("بعدی")
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        enabled = answers.size == totalQuestions,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("مشاهده نتایج و ثبت")
                    }
                }
            }
        }
    }
}

// ======================== CANDIDATE DETAIL VIEW ========================
@Composable
fun CandidateDetailView(
    candidate: Candidate,
    aiResult: String?,
    aiLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRunAI: (Candidate) -> Unit,
    onDelete: (Candidate) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "بازگشت")
            }
            Text(
                text = "کارنامه ارزیابی کندیدا",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.testTag("delete_candidate_button")
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف کاندیدا", tint = Color.Red)
            }
        }

        // Summary Card Name
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(32.dp))
                }
                Column {
                    Text(candidate.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تاریخ تکمیل آزمون: " + android.text.format.DateFormat.format("yyyy/MM/dd HH:mm", candidate.dateCompleted),
                        fontSize = 11.sp,
                        color = SecondarySlate
                    )
                }
            }
        }

        // Standard Chart / Score Gauges Section
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "نمودار نمرات ابعاد سه گانه کاندیدا",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Gauge 1: Anxiety
                ScoreGaugeItem(
                    title = "اضطراب شغلی (انگیزه مخل)",
                    score = candidate.anxietyScore,
                    maxScore = 20,
                    statusText = IOPsychologyTest.getAnxietyStatus(candidate.anxietyScore),
                    accentColor = LightCoral,
                    descText = IOPsychologyTest.getAnxietyInterpretation(candidate.anxietyScore)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = PaleBackground)
                Spacer(modifier = Modifier.height(16.dp))

                // Gauge 2: Resilience
                ScoreGaugeItem(
                    title = "تاب‌آوری روانی (قابلیت تطبیق)",
                    score = candidate.resilienceScore,
                    maxScore = 20,
                    statusText = IOPsychologyTest.getResilienceStatus(candidate.resilienceScore),
                    accentColor = MintGreen,
                    descText = IOPsychologyTest.getResilienceInterpretation(candidate.resilienceScore)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = PaleBackground)
                Spacer(modifier = Modifier.height(16.dp))

                // Gauge 3: Job Security & Competency
                ScoreGaugeItem(
                    title = "امنیت شغلی و ادراک توانمندی",
                    score = candidate.jobSecurityScore,
                    maxScore = 20,
                    statusText = IOPsychologyTest.getJobSecurityStatus(candidate.jobSecurityScore),
                    accentColor = PrimaryLight,
                    descText = IOPsychologyTest.getJobSecurityInterpretation(candidate.jobSecurityScore)
                )
            }
        }

        // Online Gemini AI Analysis Section
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryLight)
                        Text(
                            text = "تحلیل توسعه فردی هوش مصنوعی (گوگل جمینی)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }

                    if (aiResult == null && !aiLoading) {
                        Button(
                            onClick = { onRunAI(candidate) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("analyze_ai_button")
                        ) {
                            Text("دریافت تحلیل هوشمند AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (aiLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryLight)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "در حال تجمیع نمرات و تحلیل عمیق شخصیتی با مدل هوش مصنوعی گوگل...",
                            fontSize = 12.sp,
                            color = SecondarySlate,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (aiResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PaleBackground, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = aiResult,
                            fontSize = 13.sp,
                            color = SecondarySlate,
                            lineHeight = 22.sp,
                            modifier = Modifier.testTag("ai_result_text")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = { onRunAI(candidate) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بروزرسانی تحلیل با هوش مصنوعی", fontSize = 11.sp)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PaleBackground, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "برای تحلیل پیشرفته، مقایسه تخصصی تر و برنامه مدیریت اضطراب و تاب‌آوری کاندیدا، دکمه دریافت تحلیل هوشمند را کلیک کنید.",
                            fontSize = 12.sp,
                            color = SecondarySlate,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف پرونده ارزیابی", fontWeight = FontWeight.Bold) },
            text = { Text("آیا مطمئن هستید که می‌خواهید کارنامه ارزیابی ${candidate.name} را برای همیشه حذف کنید؟ این عمل غیرقابل بازگشت است.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(candidate)
                    },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("بله، حذف کن", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("خیر")
                }
            }
        )
    }
}

@Composable
fun ScoreGaugeItem(
    title: String,
    score: Int,
    maxScore: Int,
    statusText: String,
    accentColor: Color,
    descText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SecondarySlate)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "وضعیت: $statusText", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
            Text(
                text = "$score / $maxScore",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar indicator
        val progressPercent = (score.toFloat() - 5f) / (maxScore.toFloat() - 5f) // Scale for minimum 5
        val progressCoopted = progressPercent.coerceIn(0f, 1f)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(PaleBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressCoopted)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        // Contextual Interpretation
        Text(
            text = descText,
            fontSize = 11.sp,
            color = SecondarySlate,
            lineHeight = 18.sp,
            textAlign = TextAlign.Justify
        )
    }
}

// ======================== ROLE MATCHING & COMPARISON VIEW ========================
@Composable
fun RoleMatchingView(
    candidates: List<Candidate>,
    currentRole: String,
    onRoleSelected: (String) -> Unit,
    onCandidateClick: (Candidate) -> Unit
) {
    val currentRoleMeta = RoleFitnessEvaluator.roles.first { it.id == currentRole }
    
    // Sort candidates based on calculated suitability score for current role
    val fitnessList = candidates.map {
        RoleFitnessEvaluator.calculateFitness(it, currentRole)
    }.sortedByDescending { it.fitnessPercentage }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selector for Roles
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "سمت سازمانی مورد تقاضا را انتخاب کنید:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondarySlate,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleFitnessEvaluator.roles.forEach { role ->
                            val isSelected = currentRole == role.id
                            OutlinedButton(
                                onClick = { onRoleSelected(role.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("role_tab_${role.id}"),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PrimaryColor else SecondarySlate.copy(alpha = 0.2f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) PrimaryColor.copy(alpha = 0.05f) else Color.Transparent,
                                    contentColor = if (isSelected) PrimaryColor else SecondarySlate
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = role.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Role Requirement Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (currentRole) {
                                    "factory_manager" -> Icons.Default.PrecisionManufacturing
                                    "lab_expert" -> Icons.Default.Science
                                    else -> Icons.Default.Groups
                                },
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "جایگاه: ${currentRoleMeta.title}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentRoleMeta.description,
                        fontSize = 12.sp,
                        color = SecondarySlate,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("نیازمندی‌های روانشناختی مهم:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    currentRoleMeta.requirements.forEach { req ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(PrimaryLight, CircleShape))
                            Text(text = req, fontSize = 11.sp, color = SecondarySlate)
                        }
                    }
                }
            }
        }

        // Comparison Output Heading
        item {
            Text(
                text = "رتبه‌بندی کاندیداهای پروژه براساس شایستگی و تناسب",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        if (candidates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("هیچ تستی برای مقایسه یافت نشد.", fontSize = 13.sp, color = SecondarySlate)
                }
            }
        } else {
            // Display ranked suitability of candidates
            items(fitnessList) { fitness ->
                CandidateFitnessItem(
                    fitness = fitness,
                    onClick = { onCandidateClick(fitness.candidate) }
                )
            }
        }
    }
}

@Composable
fun CandidateFitnessItem(fitness: CandidateRoleFitness, onClick: () -> Unit) {
    val meterColor = when {
        fitness.fitnessPercentage >= 85 -> MintGreen
        fitness.fitnessPercentage >= 70 -> PrimaryLight
        fitness.fitnessPercentage >= 50 -> AmberWarning
        else -> LightCoral
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(meterColor.copy(alpha = 0.15f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${fitness.fitnessPercentage}% تطبیق",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = meterColor
                        )
                    }
                    Text(
                        text = fitness.candidate.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                }

                Text(
                    text = fitness.suitabilityLevel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = meterColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Percentage Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(PaleBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fitness.fitnessPercentage.toFloat() / 100f)
                        .clip(CircleShape)
                        .background(meterColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Evaluator recommendation text
            Text(
                text = fitness.description,
                fontSize = 11.sp,
                color = SecondarySlate,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مشاهده کارنامه شغلی",
                    fontSize = 10.sp,
                    color = PrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
