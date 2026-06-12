package com.example.data

import kotlinx.coroutines.flow.Flow

class CandidateRepository(private val candidateDao: CandidateDao) {
    val allCandidates: Flow<List<Candidate>> = candidateDao.getAllCandidates()

    suspend fun getCandidateById(id: Int): Candidate? = candidateDao.getCandidateById(id)

    suspend fun insertCandidate(candidate: Candidate): Long = candidateDao.insertCandidate(candidate)

    suspend fun updateCandidate(candidate: Candidate) = candidateDao.updateCandidate(candidate)

    suspend fun deleteCandidate(candidate: Candidate) = candidateDao.deleteCandidate(candidate)

    suspend fun deleteCandidateById(id: Int) = candidateDao.deleteCandidateById(id)
}
