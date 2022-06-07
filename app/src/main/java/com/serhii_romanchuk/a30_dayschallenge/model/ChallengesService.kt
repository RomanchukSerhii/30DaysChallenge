package com.serhii_romanchuk.a30_dayschallenge.model

import java.util.*


typealias ChallengesListener = (challenges: List<Challenge>) -> Unit
class ChallengesService {
    private var challenges = mutableListOf<Challenge>()

    private var listeners = mutableSetOf<ChallengesListener>()

    fun addChallenge(challenge: Challenge) {
        challenges.add(challenge)
        notifyChanges()
    }

    fun updateChallenge(challenge: Challenge, currentRepsDone: Int, currentDay: Int) {
        val indexToUpdate = challenges.indexOfFirst { it.id == challenge.id }
        if (indexToUpdate != -1) {
            challenges[indexToUpdate].currentRepsDone += currentRepsDone
            challenges[indexToUpdate].currentDay += currentDay
        }
        notifyChanges()
    }

    fun getChallenges(): List<Challenge> {
        return challenges
    }

    fun deleteChallenge(challenge: Challenge) {
        val indexToDelete = challenges.indexOfFirst { it.id == challenge.id }
        if (indexToDelete != -1) {
            challenges.removeAt(indexToDelete)
        }
        notifyChanges()
    }

    fun moveChallenge(challenge: Challenge, moveBy: Int) {
        val oldIndex = challenges.indexOfFirst { it.id == challenge.id }
        if (oldIndex == -1) return
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= challenges.size) return
        Collections.swap(challenges, oldIndex, newIndex)
        notifyChanges()
    }

    fun addListener(listener: ChallengesListener) {
        listeners.add(listener)
        listener.invoke(challenges)
    }

    fun removeListener(listener: ChallengesListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(challenges) }
    }
}