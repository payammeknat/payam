package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class Candidate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateCompleted: Long = System.currentTimeMillis(),
    
    // Scores
    val anxietyScore: Int,      // Range: 5 to 20 (sum of 5 Likert answers 1-4)
    val resilienceScore: Int,   // Range: 5 to 20
    val jobSecurityScore: Int,  // Range: 5 to 20
    
    // Detailed answers as a comma-separated string (e.g., "3,4,1,2,3...")
    val answers: String,
    
    // Detailed AI generated organizational feedback
    val aiResponse: String? = null
)
