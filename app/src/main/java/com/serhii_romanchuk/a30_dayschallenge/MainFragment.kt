package com.serhii_romanchuk.a30_dayschallenge


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.serhii_romanchuk.a30_dayschallenge.databinding.FragmentMainBinding
import com.serhii_romanchuk.a30_dayschallenge.model.Challenge
import com.serhii_romanchuk.a30_dayschallenge.model.ChallengesListener
import com.serhii_romanchuk.a30_dayschallenge.model.ChallengesService
import java.lang.reflect.Type



class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: ChallengesAdapter
    private lateinit var preferences: SharedPreferences

    private val challengesService: ChallengesService
        get() = (activity?.applicationContext as App).challengesService

    private val challengesListener: ChallengesListener =  {
        adapter.challenges = it
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        preferences = view.context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val savedChallenges = loadData()
        savedChallenges?.forEach { challenge ->
            val saveChallengeId = challengesService.getChallenges().indexOfFirst { it.id == challenge.id }
            if (saveChallengeId == -1) {
                challengesService.addChallenge(challenge)
            }
        }

        adapter = ChallengesAdapter(requireContext(), object : ChallengeActionListener {
            override fun onChallengeMove(challenge: Challenge, moveBy: Int) {
                challengesService.moveChallenge(challenge, moveBy)
            }

            override fun onChallengeDelete(challenge: Challenge) {
                challengesService.deleteChallenge(challenge)
            }

            override fun onChallengeDetails(challenge: Challenge) {
                findNavController().navigate(
                    R.id.action_mainFragment_to_challengeDayFragment,
                    bundleOf(ChallengeDayFragment.KEY_CHALLENGE to challenge,
                    ChallengeDayFragment.ARG_CHALLENGE_DAY to challenge.currentDay)
                )
            }

        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        with(binding) {
            if (challengesService.getChallenges().isNotEmpty()) {
                addButton.visibility = View.GONE
                addImageView.visibility = View.GONE
                secondAddButton.visibility = View.VISIBLE
                secondaryAddImageView.visibility = View.VISIBLE
                divider.visibility = View.VISIBLE

                secondAddButton.setOnClickListener { openNewChallenge() }
                addChallengeTextView.setOnClickListener { openNewChallenge() }
            } else addButton.setOnClickListener { openNewChallenge() }
        }

        challengesService.addListener(challengesListener)
    }

    private fun openNewChallenge() {
        findNavController().navigate(R.id.action_mainFragment_to_newChallengeFragment)
    }

    private fun loadData(): List<Challenge>? {
        val gson = Gson()
        val json = preferences.getString(PREF_CHALLENGES_VALUE, null)
        val type: Type = object : TypeToken<List<Challenge>?>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveData() {
        val gson = Gson()
        val json = gson.toJson(challengesService.getChallenges())
        preferences.edit()
            .putString(PREF_CHALLENGES_VALUE, json)
            .apply()
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        challengesService.removeListener(challengesListener)
    }

    companion object {
        const val SHARED_PREFERENCES = "APP_PREFERENCES"
        const val PREF_CHALLENGES_VALUE = "PREF_CHALLENGES_VALUE"
    }
}