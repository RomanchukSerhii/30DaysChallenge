package com.serhii_romanchuk.a30_dayschallenge

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.serhii_romanchuk.a30_dayschallenge.databinding.ItemChallengeBinding
import com.serhii_romanchuk.a30_dayschallenge.model.Challenge

interface ChallengeActionListener {

    fun onChallengeMove(challenge: Challenge, moveBy: Int)

    fun onChallengeDelete(challenge: Challenge)

    fun onChallengeDetails(challenge: Challenge)

}

class ChallengesAdapter(
    private val context: Context,
    private val actionListener: ChallengeActionListener
) : RecyclerView.Adapter<ChallengesAdapter.UsersViewHolder>(), View.OnClickListener {

    var challenges: List<Challenge> = emptyList()
    @SuppressLint("NotifyDataSetChanged")
    set(newValue) {
        field = newValue
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        val challenge = v.tag as Challenge
        when (v.id) {
            R.id.moreImageViewButton -> {
                showPopupMenu(v)
            }
            else -> {
                actionListener.onChallengeDetails(challenge)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChallengeBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener (this)
        binding.moreImageViewButton.setOnClickListener (this)

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val challenge = challenges[position]
        val maxProgressBar = challenge.repsPerMonthNeedToDo.filter { !it.isWhitespace() }.toInt()
        with(holder.binding) {
            holder.itemView.tag = challenge
            moreImageViewButton.tag = challenge
            repsDoneTextView.text = challenge.currentRepsDone.toString()
            numberOfRepsTextView.text = context.getString(R.string.number_of_reps, challenge.repsPerMonthNeedToDo)
            dayTextView.text = context.getString(R.string.day_of_challenge, challenge.currentDay)
            titleTextView.text = challenge.name
            progressBar.progress = challenge.currentRepsDone
            progressBar.max = maxProgressBar
        }
    }

    override fun getItemCount(): Int = challenges.size

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val challenge = view.tag as Challenge
        val position = challenges.indexOfFirst { it.id == challenge.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < challenges.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> {
                    actionListener.onChallengeMove(challenge, -1)
                }
                ID_MOVE_DOWN -> {
                    actionListener.onChallengeMove(challenge, 1)
                }
                ID_REMOVE -> {
                    actionListener.onChallengeDelete(challenge)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    class UsersViewHolder (
        val binding: ItemChallengeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }
}