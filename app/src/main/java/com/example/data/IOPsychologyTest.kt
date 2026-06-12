package com.example.data

data class PsychQuestion(
    val id: Int,
    val text: String,
    val category: Category
) {
    enum class Category {
        ANXIETY,     // اضطراب شغلی
        RESILIENCE,  // تاب‌آوری
        JOB_SECURITY // امنیت شغلی و توانایی
    }
}

object IOPsychologyTest {
    val questions = listOf(
        // Category A: Workplace Anxiety (میزان اضطراب شغلی)
        PsychQuestion(1, "در محیط کار احساس فشردگی، تنش یا خستگی ذهنی مداوم دارم.", PsychQuestion.Category.ANXIETY),
        PsychQuestion(2, "نگران هستم که اشتباهاتم در کار با واکنش‌های شدید و منفی روبرو شود.", PsychQuestion.Category.ANXIETY),
        PsychQuestion(3, "حتی پس از پایان ساعت کاری، فکر کردن به مسائل کاری باعث اضطراب من می‌شود.", PsychQuestion.Category.ANXIETY),
        PsychQuestion(4, "قبل از شروع روز کاری جدید، احساس بی‌قراری یا نگرانی می‌کنم.", PsychQuestion.Category.ANXIETY),
        PsychQuestion(5, "هنگام صحبت با همکاران یا مدیران، ضربان قلب من افزایش می‌یابد یا مضطرب می‌شوم.", PsychQuestion.Category.ANXIETY),

        // Category B: Resilience (تاب‌آوری)
        PsychQuestion(6, "در مواجهه با مشکلات و موانع پیش‌بینی‌نشده در کار، سریع خودم را بازیابی می‌کنم.", PsychQuestion.Category.RESILIENCE),
        PsychQuestion(7, "تغییرات ناگهانی در روش‌ها و شرایط کاری را به عنوان یک چالش رشدبخش می‌بینم.", PsychQuestion.Category.RESILIENCE),
        PsychQuestion(8, "حتی در شرایط تحت فشار شدید هم می‌توانم خونسردی و تمرکز خود را حفظ کنم.", PsychQuestion.Category.RESILIENCE),
        PsychQuestion(9, "وقتی شکست می‌خورم، تسلیم نمی‌شوم و با انگیزه بیشتر تلاش می‌کنم.", PsychQuestion.Category.RESILIENCE),
        PsychQuestion(10, "معتقدم که توانایی غلبه بر چالش‌های مختلف در مسیر حرفه‌ای خود را دارم.", PsychQuestion.Category.RESILIENCE),

        // Category C: Job Security & Career Ability (امنیت شغلی و توانایی)
        PsychQuestion(11, "از ثبات جایگاه خود در مجموعه مطمئن هستم و ترس بیهوده از تعدیل یا اخراج ندارم.", PsychQuestion.Category.JOB_SECURITY),
        PsychQuestion(12, "فکر می‌کنم مهارت‌ها و توانایی‌های شغلی من برای نیازهای آینده بازار کار کاملاً کارآمد و به‌روز است.", PsychQuestion.Category.JOB_SECURITY),
        PsychQuestion(13, "احساس می‌کنم مدیریت به نظرات، ارزش‌آفرینی و مشارکت‌های شغلی من اعتماد جدی دارد.", PsychQuestion.Category.JOB_SECURITY),
        PsychQuestion(14, "با کمال میل مسئولیت‌های جدید و پیچیده مربوط به زمینه کاری‌ام را قبول می‌کنم.", PsychQuestion.Category.JOB_SECURITY),
        PsychQuestion(15, "نسبت به آینده پایدار شغلی در این سازمان کاملاً امیدوار و مثبت‌نگر هستم.", PsychQuestion.Category.JOB_SECURITY)
    )

    fun getAnxietyStatus(score: Int): String {
        return when {
            score <= 9 -> "پایین (مطلوب) 🟢"
            score <= 14 -> "متوسط (طبیعی) 🟡"
            else -> "بالا (نیازمند توجه) 🔴"
        }
    }

    fun getAnxietyInterpretation(score: Int): String {
        return when {
            score <= 9 -> "کاندیدا آرامش ذهنی عالی و پایداری روانی بالایی در محیط کار دارد. این فرد در شرایط بحرانی کمتر دچار فرسودگی شده و می‌تواند با خونسردی تصمیم‌گیری کند."
            score <= 14 -> "اضطراب شغلی در مرزهای طبیعی است. کاندیدا آمادگی کار را دارد، اما در شرایط پیک کاری یا تغییرات ساختاری ممکن است نیاز به تکنیک‌های خودکنترلی و مدیریت استرس داشته باشد."
            else -> "اضطراب شغلی کاندیدا بسیار بالا است. این میزان تنش مداوم فرسودگی شغلی سریعی ایجاد می‌کند و بازدهی را به شدت کاهش می‌دهد. نیاز مبرم به بازمهندسی وظایف، کاهش بار استرس و حمایت سازمانی دارد."
        }
    }

    fun getResilienceStatus(score: Int): String {
        return when {
            score <= 9 -> "پایین (نیازمند تقویت) 🔴"
            score <= 14 -> "متوسط (قابل قبول) 🟡"
            else -> "بالا (عالی) 🟢"
        }
    }

    fun getResilienceInterpretation(score: Int): String {
        return when {
            score <= 9 -> "انعطاف‌پذیری و تاب‌آوری کاندیدا در مواجهه با چالش‌ها بسیار پایین است. فرد در شرایط بحرانی سریعاً ناامید شده و توان برگشت‌پذیری ذهنی کمی دارد. نیازمند توسعه فردی در ابعاد حل مسئله است."
            score <= 14 -> "توانایی پذیرش چالش‌ها در حد متوسط است. فرد تلاش می‌کند ثبات درونی خود را حفظ کند، اما در بحران‌های بزرگ احتمال افت انگیزشی وجود دارد که کار روی هوش هیجانی آن را مرتفع می‌سازد."
            else -> "تاب‌آوری فوق‌العاده بالا. کاندیدا پایداری ذهنی فوق‌العاده‌ای دارد، تغییرات را فرصت می‌بیند و در بدترین سناریوهای تیمی نیز می‌تواند روحیه مثبت خود و بقیه را بازسازی کند."
        }
    }

    fun getJobSecurityStatus(score: Int): String {
        return when {
            score <= 9 -> "پایین (احساس ناامنی) 🔴"
            score <= 14 -> "متوسط (نسبتاً پایدار) 🟡"
            else -> "بالا (بسیار پایدار و توانمند) 🟢"
        }
    }

    fun getJobSecurityInterpretation(score: Int): String {
        return when {
            score <= 9 -> "کاندیدا احساس تزلزل شدید شغلی دارد و فکر می‌کند سازمان برای مهارت‌هایش ارزش چندانی قائل نیست. این ذهنیت سبب کاهش وفاداری و پنهان‌کاری ارادی در کارهای تیمی می‌شود."
            score <= 14 -> "اطمینان نسبی به جایگاه شغلی خود دارد. توانمندی کاندیدا مطلوب است اما تمایل دارد در دایره امن مهارتی خود بماند و از پذیرش ریسک‌ها و نوآوری‌های متهورانه می‌پرهیزد."
            else -> "حس عمیق ارزشمندی، ثبات شغلی و تطبیق‌پذیری بالا. کاندیدا پیوند استراتژیکی با ارزش‌های کلیدی سازمان برقرار است، خودش را موثر می‌بیند و آماده پذیرش سناریوهای نوآورانه است."
        }
    }
}
