package com.serhii_romanchuk.a30_dayschallenge

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.serhii_romanchuk.a30_dayschallenge.databinding.FragmentNewChallengeBinding
import com.serhii_romanchuk.a30_dayschallenge.model.Challenge
import com.serhii_romanchuk.a30_dayschallenge.model.ChallengesService
import kotlin.random.Random

class NewChallengeFragment : Fragment(R.layout.fragment_new_challenge) {
    private lateinit var binding: FragmentNewChallengeBinding

    private val challengesService: ChallengesService
        get() = (activity?.applicationContext as App).challengesService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewChallengeBinding.bind(view)

        val numberOfReps = resources.getStringArray(R.array.quantity_array)
        val mode = resources.getStringArray(R.array.mode)

        val quantityAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            R.id.text1,
            numberOfReps)

        val modeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            R.id.text1,
            mode
        )

        binding.numberOfRepsAutoComplete.setAdapter(quantityAdapter)
        binding.modeAutoComplete.setAdapter(modeAdapter)

        binding.saveSettingsButton.setOnClickListener {
            onSaveSettingsPressed()
        }
    }

    private fun onSaveSettingsPressed() = with(binding){
        var uniqueId = Random.nextInt(100)
        while (true) {
            val challenges = ChallengesService().getChallenges()
            val indexToCheck = challenges.indexOfFirst { it.id == uniqueId }
            if (indexToCheck == -1) break
            else uniqueId = Random.nextInt(100)
        }

        challengesService.addChallenge(Challenge(
            name = nameExerciseEditText.text.toString().uppercase(),
            id = uniqueId,
            repsPerMonthNeedToDo = numberOfRepsAutoComplete.text.toString(),
            schedule = modeAutoComplete.text.toString()
        ))
        findNavController().popBackStack()
    }
}