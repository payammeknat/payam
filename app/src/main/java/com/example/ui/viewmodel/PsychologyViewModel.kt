package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PsychologyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CandidateRepository(database.candidateDao())

    val candidates: StateFlow<List<Candidate>> = repository.allCandidates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCandidate = MutableStateFlow<Candidate?>(null)
    val selectedCandidate: StateFlow<Candidate?> = _selectedCandidate.asStateFlow()

    private val _comparisonsRole = MutableStateFlow<String>("factory_manager")
    val comparisonsRole: StateFlow<String> = _comparisonsRole.asStateFlow()

    // Test Taking State
    private val _testTakerName = MutableStateFlow("")
    val testTakerName: StateFlow<String> = _testTakerName.asStateFlow()

    private val _isTestActive = MutableStateFlow(false)
    val isTestActive: StateFlow<Boolean> = _isTestActive.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _tempAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // ID to Score (1 to 4)
    val tempAnswers: StateFlow<Map<Int, Int>> = _tempAnswers.asStateFlow()

    // AI Analysis Loading and Result States
    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Auto-seed candidates to demonstrate comparative analysis features instantly
        viewModelScope.launch {
            candidates.collect { list ->
                if (list.isEmpty()) {
                    seedDatabase()
                }
            }
        }
    }

    private suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        val seeded = listOf(
            Candidate(
                name = "امیر علوی",
                anxietyScore = 6,      // 1,1,2,1,1 -> Low Anxiety (6)
                resilienceScore = 19,   // 4,4,4,3,4 -> High Resilience (19)
                jobSecurityScore = 19,  // 4,4,4,3,4 -> High Career Ability (19)
                answers = "1,1,2,1,1,4,4,4,3,4,4,4,4,3,4",
                aiResponse = "امیر علوی نمونه برجسته‌ای از رهبری خط مقدم در محیط‌های صنعتی پر استرس است. بر اساس تحلیل سازمان‌های صنعتی، کاندیداهایی با تاب‌آوری بالا و اضطراب شغلی اندک تمایل ذاتی به مدیریت تولید و هدایت زنجیره هماهنگی دارند. پیشنهادات شامل دوره‌های راهبردهای رشد سازمانی پیشرفته است."
            ),
            Candidate(
                name = "مریم احمدی",
                anxietyScore = 5,      // 1,1,1,1,1 -> Very Low Anxiety (5)
                resilienceScore = 13,   // 3,2,3,2,3 -> Moderate Resilience (13)
                jobSecurityScore = 18,  // 4,4,3,3,4 -> High Technical Competence (18)
                answers = "1,1,1,1,1,3,2,3,2,3,4,4,3,3,4",
                aiResponse = "مریم احمدی به دلیل کنترل فوق‌العاده اضطراب و پایداری در دقت محاسباتی، بهترین گزینه ثبتی برای پست‌های تحقیق و توسعه و کارشناس ارشد آزمایشگاهی است. توانایی او در تمرکز صبورانه روی متدهای سنجش تضمین‌کننده کاهش معنی‌دار خطای کنترل کیفی خواهد بود."
            ),
            Candidate(
                name = "رضا کریمی",
                anxietyScore = 17,     // 3,4,3,4,3 -> High Anxiety (17)
                resilienceScore = 10,   // 2,2,2,2,2 -> Moderate-Low Resilience (10)
                jobSecurityScore = 9,    // 2,2,2,1,2 -> Low Job Security (9)
                answers = "3,4,3,4,3,2,2,2,2,2,2,2,2,1,2",
                aiResponse = "رضا کریمی دارای اضطراب مفرط ناشی از شرایط کاری است که تأثیر بسزایی بر حس امنیت شغلی او گذاشته است. دور نگاه داشتن وی از مشاغل خط اول کارگاه و قرارگیری در پروژه‌های متمرکز یا دریافت مشاوره کنترل اضطراب گام طلایی برای بازیابی بازدهی او است."
            )
        )
        for (c in seeded) {
            repository.insertCandidate(c)
        }
    }

    fun startTest(name: String) {
        if (name.trim().isNotEmpty()) {
            _testTakerName.value = name
            _isTestActive.value = true
            _currentQuestionIndex.value = 0
            _tempAnswers.value = emptyMap()
            _errorMessage.value = null
        }
    }

    fun selectAnswer(questionId: Int, score: Int) {
        val updated = _tempAnswers.value.toMutableMap()
        updated[questionId] = score
        _tempAnswers.value = updated
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < IOPsychologyTest.questions.indices.last) {
            _currentQuestionIndex.value += 1
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun cancelTest() {
        _isTestActive.value = false
        _testTakerName.value = ""
        _tempAnswers.value = emptyMap()
        _currentQuestionIndex.value = 0
    }

    fun completeTest() {
        val answersMap = _tempAnswers.value
        if (answersMap.size < IOPsychologyTest.questions.size) {
            _errorMessage.value = "لطفاً به تمامی سوالات پاسخ دهید."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            var anxiety = 0
            var resilience = 0
            var jobSecurity = 0
            val answersList = mutableListOf<String>()

            for (i in 1..15) {
                val score = answersMap[i] ?: 1
                answersList.add(score.toString())
                
                val q = IOPsychologyTest.questions.firstOrNull { it.id == i }
                if (q != null) {
                    when (q.category) {
                        PsychQuestion.Category.ANXIETY -> anxiety += score
                        PsychQuestion.Category.RESILIENCE -> resilience += score
                        PsychQuestion.Category.JOB_SECURITY -> jobSecurity += score
                    }
                }
            }

            val candidate = Candidate(
                name = _testTakerName.value,
                anxietyScore = anxiety,
                resilienceScore = resilience,
                jobSecurityScore = jobSecurity,
                answers = answersList.joinToString(",")
            )

            val insertedId = repository.insertCandidate(candidate)
            val updatedCandidate = candidate.copy(id = insertedId.toInt())
            
            withContext(Dispatchers.Main) {
                _selectedCandidate.value = updatedCandidate
                _isTestActive.value = false
                _testTakerName.value = ""
                _tempAnswers.value = emptyMap()
                _currentQuestionIndex.value = 0
            }
        }
    }

    fun selectCandidate(candidate: Candidate) {
        _selectedCandidate.value = candidate
        _aiResult.value = candidate.aiResponse
        _errorMessage.value = null
    }

    fun selectComparisonsRole(roleId: String) {
        _comparisonsRole.value = roleId
    }

    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCandidate(candidate)
            withContext(Dispatchers.Main) {
                if (_selectedCandidate.value?.id == candidate.id) {
                    _selectedCandidate.value = null
                    _aiResult.value = null
                }
            }
        }
    }

    fun clearSelectedCandidate() {
        _selectedCandidate.value = null
        _aiResult.value = null
    }

    // Google Gemini API integration
    fun runGeminiAnalysis(candidate: Candidate) {
        _aiLoading.value = true
        _errorMessage.value = null
        _aiResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val promptText = """
                    لطفا گزارش ارزیابی عمیق روانشناسی صنعتی و سازمانی را برای فرد زیر ارائه‌ کنید:
                    نام کاندیدا: ${candidate.name}
                    
                    نمرات آزمون استاندارد (بازه ۵ الی ۲۰):
                    ۱. اضطراب شغلی محیط کار: ${candidate.anxietyScore} از ۲۰ (${IOPsychologyTest.getAnxietyStatus(candidate.anxietyScore)})
                    ۲. تاب‌آوری در برابر فشار و ناملایمات: ${candidate.resilienceScore} از ۲۰ (${IOPsychologyTest.getResilienceStatus(candidate.resilienceScore)})
                    ۳. حس امنیت شغلی و توانمندی مهارتی: ${candidate.jobSecurityScore} از ۲۰ (${IOPsychologyTest.getJobSecurityStatus(candidate.jobSecurityScore)})
                    
                    بر اساس این نمرات و معیارهای علمی روانشناسی صنعتی برای ما مشخص کنید:
                    ۱. تحلیل عمیق شخصیت کاری فرد و سبک تعامل او در سازمان.
                    ۲. تناسب او با سمت های کلیدی مانند "مدیریت کارگاهی و تولید" یا "مسئول جزئیات و کنترل آزمایشگاه" با ذکر براهین علمی.
                    ۳. نقاط قوت مشهود و چالش های پیش رو کاندیدا.
                    ۴. برنامه پیشنهادی توسعه فردی (Coaching/Action Plan) اختصاصی جهت بهینه‌سازی قابلیت‌ها و کاهش اضطراب یا تقویت تاب‌آوری او.
                    
                    لطفاً پاسخ را بسیار شیک، خوانا، سازمان‌یافته با تیترهای منظم و جذاب به زبان فارسی بنویسید.
                """.trimIndent()

                val analysis = GeminiClient.getAiAnalysis(promptText)
                
                // Save AI response cache to Room
                val updatedCandidate = candidate.copy(aiResponse = analysis)
                repository.updateCandidate(updatedCandidate)
                
                withContext(Dispatchers.Main) {
                    _selectedCandidate.value = updatedCandidate
                    _aiResult.value = analysis
                    _aiLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _aiLoading.value = false
                    _errorMessage.value = "خطا در اتصال به هوش مصنوعی: ${e.message}"
                }
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PsychologyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PsychologyViewModel(application) as T
            }
            throw IllegalArgumentException("ViewModel class unknown")
        }
    }
}
