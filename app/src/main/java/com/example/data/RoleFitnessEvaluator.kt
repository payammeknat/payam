package com.example.data

import kotlin.math.max
import kotlin.math.min

data class JobRole(
    val id: String,
    val title: String,
    val iconName: String,
    val description: String,
    val requirements: List<String>
)

data class CandidateRoleFitness(
    val candidate: Candidate,
    val fitnessPercentage: Int,
    val suitabilityLevel: String, // عالی، بسیار خوب، متوسط، پایین
    val description: String
)

object RoleFitnessEvaluator {
    val roles = listOf(
        JobRole(
            id = "factory_manager",
            title = "مدیر تولید کارخانه",
            iconName = "precision_manufacturing",
            description = "مسئول هدایت کل عملیات تولید، هماهنگی خطوط فنی، مدیریت کارگران و واکنش سریع به توقف فنی خط تولید یا رخدادهای بحرانی.",
            requirements = listOf(
                "تاب‌آوری بسیار بالا در زمان بحران و خرابی دستگاه‌ها",
                "ترجیحاً سطح اضطراب شغلی پایین یا متوسط جهت حفظ تمرکز تیمی",
                "توانمندی بالا در تصمیم‌گیری‌های پرریسک کارگاهی"
            )
        ),
        JobRole(
            id = "lab_expert",
            title = "کارشناس آزمایشگاه و کنترل کیفی",
            iconName = "biotech",
            description = "طراحی و اجرای تست‌های دقیق کیفی و پایش فرآیندهای حساس کارخانه که نیازمند تمرکز خارق‌العاده، دقت میکروسکوپی و عدم تعجیل و خطا است.",
            requirements = listOf(
                "اضطراب بسیار پایین به منظور پیشگیری از خطای ناشی از استرس",
                "مهارت و توانمندی تخصصی بسیار عمیق و به‌روز",
                "تاب‌آوری متوسط جهت اجرای روتین‌های کیفی تکرارشونده"
            )
        ),
        JobRole(
            id = "hr_specialist",
            title = "کارشناس منابع انسانی و مشاور سازمانی",
            iconName = "groups",
            description = "متولی بهبود انگیزه همکاران، میانجی‌گری در اختلافات سازمانی، پایش روحیه پرسنل و هدایت برنامه‌های توسعه فردی کادر اداری.",
            requirements = listOf(
                "تاب‌آوری روانی بالا جهت همدردی و مواجهه با گلایه‌های همکاران",
                "امنیت شغلی و توانمندی متقابل بالا جهت تعامل با سطوح کلان مدیریت",
                "مهارت خود‌کنترلی اضطراب در شرایط تشنج روابط کار"
            )
        )
    )

    fun calculateFitness(candidate: Candidate, roleId: String): CandidateRoleFitness {
        val r = candidate.resilienceScore.toDouble()
        val s = candidate.jobSecurityScore.toDouble()
        val a = candidate.anxietyScore.toDouble() // Lower is better, so we use (21 - a)

        val rawScore: Double
        val minRaw: Double
        val maxRaw: Double

        when (roleId) {
            "factory_manager" -> {
                // Focus: high resilience (1.5), good security (1.0), low anxiety (1.2)
                rawScore = (r * 1.5) + (s * 1.0) + ((21.0 - a) * 1.2)
                minRaw = (5.0 * 1.5) + (5.0 * 1.0) + (1.0 * 1.2) // 13.7
                maxRaw = (20.0 * 1.5) + (20.0 * 1.0) + (16.0 * 1.2) // 69.2
            }
            "lab_expert" -> {
                // Focus: low anxiety is critical (2.0), high ability (1.2), normal resilience (1.0)
                rawScore = (r * 1.0) + (s * 1.2) + ((21.0 - a) * 2.0)
                minRaw = (5.0 * 1.0) + (5.0 * 1.2) + (1.0 * 2.0) // 13.0
                maxRaw = (20.0 * 1.0) + (20.0 * 1.2) + (16.0 * 2.0) // 76.0
            }
            else -> {
                // Focus: human relations -> high resilience (1.8), low anxiety (1.0), good security (1.0)
                rawScore = (r * 1.8) + (s * 1.0) + ((21.0 - a) * 1.0)
                minRaw = (5.0 * 1.8) + (5.0 * 1.0) + (1.0 * 1.0) // 15.0
                maxRaw = (20.0 * 1.8) + (20.0 * 1.0) + (16.0 * 1.0) // 72.0
            }
        }

        val percentage = (((rawScore - minRaw) / (maxRaw - minRaw)) * 100).toInt()
        val finalPercentage = max(0, min(100, percentage))

        val level = when {
            finalPercentage >= 85 -> "شایستگی عالی (منطبق) 🏆"
            finalPercentage >= 70 -> "شایستگی بسیار خوب 👍"
            finalPercentage >= 50 -> "شایستگی متوسط 🟡"
            else -> "شایستگی محدود (نیاز به ارتقا) ⚠️"
        }

        val description = when (roleId) {
            "factory_manager" -> {
                if (finalPercentage >= 80) {
                    "تناسب عالی! تاب‌آوری بسیار بالا و اضطراب کارآمد این کاندیدا را به بهترین هدایت‌گر لاین‌های پرفشار صنعتی تبدیل می‌کند."
                } else if (finalPercentage >= 50) {
                    "تناسب متوسط. کاندیدا توانایی اولیه را دارد، اما در شرایط بحرانی نیازمند دوره‌های توانمندسازی مدیریت تنش کارگاهی است."
                } else {
                    "تناسب نامناسب. سطح تاب‌آوری پایین یا اضطراب بالا با ماهیت پر ریسک و پویای کارگاه همخوانی ندارد."
                }
            }
            "lab_expert" -> {
                if (finalPercentage >= 80) {
                    "گزینه ایده‌آل! تمرکز عمیق، خونسردی بالا و اضطراب محدود سبب ارزیابی ایمن و به دور از خطای ابزاری کنترل کیفیت می‌شود."
                } else if (finalPercentage >= 50) {
                    "تناسب متوسط. تسلط کافی بر مهارت‌ها وجود دارد ولی احتمال بروز خطا هنگام خستگی شدید ذهنی منتفی نیست."
                } else {
                    "تناسب نامناسب. اضطراب شغلی بالا ممکن است بر عملکرد و دقت کاندیدا در کار با تجهیزات حساس آزمایشگاهی اثر منفی بگذارد."
                }
            }
            else -> {
                if (finalPercentage >= 80) {
                    "تناسب فوق‌العاده برای حل تعارضات اداری و توسعه سرمایه انسانی. او در مواجهه با هیجانات کارکنان انعطاف‌پذیر و حامی است."
                } else if (finalPercentage >= 50) {
                    "تناسب متوسط. او می‌تواند روندهای روتین اداری را کنترل کند اما در حل چالش‌های عمیق عاطفی کارکنان ممکن است فرسوده شود."
                } else {
                    "شخصیت این فرد مایل به کارهای متمرکز و مستقل است و فضای تعاملی و پرچالش مناسب او نیست."
                }
            }
        }

        return CandidateRoleFitness(candidate, finalPercentage, level, description)
    }
}
