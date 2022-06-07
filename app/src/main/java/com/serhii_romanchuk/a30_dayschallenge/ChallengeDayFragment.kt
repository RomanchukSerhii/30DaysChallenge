package com.serhii_romanchuk.a30_dayschallenge

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.serhii_romanchuk.a30_dayschallenge.databinding.FragmentChallengeDayBinding
import com.serhii_romanchuk.a30_dayschallenge.model.Challenge
import com.serhii_romanchuk.a30_dayschallenge.model.ChallengesService
import kotlin.properties.Delegates.notNull

class ChallengeDayFragment : Fragment(R.layout.fragment_challenge_day) {
    private lateinit var binding: FragmentChallengeDayBinding
    private lateinit var challenge: Challenge
    private var repsPerDay by notNull<Int>()
    private var repsPerSet = 10


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChallengeDayBinding.bind(view)

        challenge = requireArguments().getParcelable<Challenge>(KEY_CHALLENGE) ?:
            Challenge(-1, "-", "0", "")
        repsPerDay = savedInstanceState?.getInt(KEY_REPS_PER_DAY) ?: challenge.currentRepsPerDayDone
        repsPerSet = savedInstanceState?.getInt(KEY_REPS_PER_SET) ?: 10
        updateUi()

        with(binding) {
            challenge.setRepsPerDayNeedToDo()
            nameExerciseTextView.text = challenge.name
            progressBar.max = challenge.repsPerDayNeedToDo
            numberOfRepsTextView.text = getString(R.string.number_of_reps, challenge.repsPerDayNeedToDo.toString())

            minusImageView.setOnClickListener { onMinusPressed() }
            plusImageView.setOnClickListener { onPlusPressed() }
            okImageView.setOnClickListener { onOkPressed() }
            saveDayButton.setOnClickListener { onSaveDayPressed() }
            backButton.setOnClickListener { onBackPressed() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REPS_PER_DAY, repsPerDay)
        outState.putInt(KEY_REPS_PER_SET, repsPerSet)
    }

    private fun onMinusPressed() {
        repsPerSet--
        if (repsPerSet < 0)
            Toast.makeText(
                requireContext(),
                R.string.toast_reps_must_be_positive,
                Toast.LENGTH_SHORT
            ).show()
        else updateUi()
    }

    private fun onPlusPressed() {
        repsPerSet++
        updateUi()
    }

    private fun onOkPressed() = with(binding) {
        repsPerDay += countRepsTextView.text.toString().toInt()
        updateUi()
    }

    private fun onSaveDayPressed() {
        challenge.currentRepsDone += repsPerDay
        ChallengesService().updateChallenge(
            challenge = challenge,
            currentRepsDone = challenge.currentRepsDone,
            currentDay = challenge.currentDay++
        )
        challenge.currentRepsPerDayDone = 0
        findNavController().popBackStack()
    }

    private fun onBackPressed() {
        findNavController().popBackStack()
    }

    private fun updateUi() = with(binding){
        challenge.currentRepsPerDayDone = repsPerDay
        progressBar.progress = repsPerDay
        countRepsTextView.text = repsPerSet.toString()
        repsPerDayTextView.text = repsPerDay.toString()
    }

    companion object {
        const val KEY_CHALLENGE = "KEY_CHALLENGE"
        const val KEY_REPS_PER_DAY = "KEY_REPS_PER_DAY"
        const val KEY_REPS_PER_SET = "KEY_REPS_PER_SET"
        const val ARG_CHALLENGE_DAY = "challengeDay"
    }
}