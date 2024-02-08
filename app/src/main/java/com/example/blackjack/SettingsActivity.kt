package com.example.blackjack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blackjack.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numPlayers = intent.getIntExtra(getString(R.string.num_player_key), 2)
        val numDecks = intent.getIntExtra(getString(R.string.num_deck_key), 1)

        if(numPlayers in 2..4)
            binding.numOfPlayers.setSelection(numPlayers - 2)
        if(numDecks in 1..3)
            binding.numOfDecks.setSelection(numDecks - 1)

        binding.confirmButton.setOnClickListener {
            val spinnerSelection = binding.numOfPlayers.selectedItem.toString().toInt()
            val spinnerSelection2 = binding.numOfDecks.selectedItem.toString().toInt()
            val result = Intent().apply {
                putExtra(getString(R.string.num_player_key), spinnerSelection)
                putExtra(getString(R.string.num_deck_key), spinnerSelection2)
            }
            setResult(RESULT_OK, result)
            finish()
        }

    }
}