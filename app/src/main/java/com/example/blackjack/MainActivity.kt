package com.example.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.blackjack.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var numPlayers = 2
    private var numDecks = 1
    private var playerScore = 0
    private var krupierScore = 0

    //TODO MULTIPLE PLAYERS
    //TODO KEEPING TRACK OF CARDS - MAX 4 CARDS OF THE SAME TYPE
    //TODO MULTIPLE DECKS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(localClassName, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.card2.visibility = View.GONE
        binding.krupier2.visibility = View.GONE
        binding.hitButton.visibility = View.GONE
        binding.standButton.visibility = View.GONE

        binding.startButton.setOnClickListener {
            binding.card2.visibility = View.GONE
            binding.krupier2.visibility = View.GONE
            binding.krupier3.visibility = View.GONE
            binding.hitButton.visibility = View.VISIBLE
            binding.standButton.visibility = View.VISIBLE

            resetGame()
            startCards()

            binding.startButton.visibility = View.GONE

            binding.hitButton.setOnClickListener {
                binding.hitButton.visibility = View.GONE
                binding.standButton.visibility = View.GONE
                binding.krupier3.visibility = View.VISIBLE

                drawCard(true, findViewById(R.id.card2))
                binding.card2.visibility = View.VISIBLE
                if(krupierScore < 17) {
                    drawCard(false, findViewById(R.id.krupier2))
                    binding.krupier2.visibility = View.VISIBLE
                }

                updateText()

                endTurn()

            }

            binding.standButton.setOnClickListener {
                binding.hitButton.visibility = View.GONE
                binding.standButton.visibility = View.GONE
                binding.krupier3.visibility = View.VISIBLE

                if(krupierScore < 17) {
                    drawCard(false, findViewById(R.id.krupier2))
                    binding.krupier2.visibility = View.VISIBLE
                }

                updateText()

                endTurn()
            }

        }
    }

    private fun drawCard(forPlayer: Boolean, image: ImageView) {
        val card = Card()
        val draw = card.draw()
        updateImg(draw, image)
        updateScore(draw, forPlayer)
    }

    private fun startCards() {

        drawCard(true, findViewById(R.id.card1))
        drawCard(true, findViewById(R.id.card3))
        drawCard(false, findViewById(R.id.krupier1))
        drawCard(false, findViewById(R.id.krupier3))

    }

    private fun updateScore(draw: Int = 0, forPlayer: Boolean = true) {
        var score: Int

        if(!forPlayer)
            score = krupierScore
        else
            score = playerScore

        when(draw) {
            1 -> score += 2
            2 -> score += 3
            3 -> score += 4
            4 -> score += 5
            5 -> score += 6
            6 -> score += 7
            7 -> score += 8
            8 -> score += 9
            9 -> score += 10
            10 -> score += 10
            11 -> score += 10
            12 -> score += 10
            13 -> if(score <= 10) score += 11
                    else if(score in 11 .. 20) score += 1
            else -> score += 0
        }
        if(!forPlayer)
            krupierScore = score
        else
            playerScore = score
    }

    @SuppressLint("SetTextI18n")
    private fun updateText() {

        binding.playerText.text = "Suma kart gracza: $playerScore"
        binding.krupierText.text = "Suma kart krupiera: $krupierScore"

    }

    private fun updateImg(draw: Int, image: ImageView) {
        image.setImageResource(resolveDrawable(draw))
    }

    private fun resolveDrawable(value: Int): Int {
        return when (value) {
            1 -> R.drawable.two
            2 -> R.drawable.three
            3 -> R.drawable.four
            4 -> R.drawable.five
            5 -> R.drawable.six
            6 -> R.drawable.seven
            7 -> R.drawable.eight
            8 -> R.drawable.nine
            9 -> R.drawable.ten
            10 -> R.drawable.jack
            11 -> R.drawable.queen
            12 -> R.drawable.king
            13 -> R.drawable.ace
            else -> R.drawable.ace

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> startSettingsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSettingsActivity() {
        val intent: Intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra(getString(R.string.num_player_key), numPlayers)
            putExtra(getString(R.string.num_deck_key), numDecks)
        }
        launchSettingsActivity.launch(intent)
    }

    private val launchSettingsActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(localClassName, "onActivityResult")
            if(result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    numPlayers = data.getIntExtra(getString(R.string.num_player_key), 2)
                    numDecks =  data.getIntExtra(getString(R.string.num_deck_key), 1)
                }
                applySettings()
                resetGame()

                Snackbar.make(
                    binding.root,
                    "Current settings: numPlayers: $numPlayers, numDecks: $numDecks",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    private fun applySettings() {
        binding.startButton.isEnabled = true
        resetGame()
    }

    @SuppressLint("SetTextI18n")
    private fun resetGame() {
        playerScore = 0
        krupierScore = 0
        binding.playerText.text = "Karty gracza"
        binding.krupierText.text = "Karty krupiera"
    }

    @SuppressLint("SetTextI18n")
    private fun endTurn() {
        val winner = when {
            krupierScore > 21 -> "gracz"
            playerScore > 21 -> "krupier"
            playerScore > krupierScore ->  "gracz"
            playerScore < krupierScore -> "krupier"
            playerScore and krupierScore > 21 -> "remis"
            else -> "remis"
        }
        Snackbar.make(
            binding.root,
            "Zwycięzca rozdania: $winner",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Play again") {
            binding.startButton.visibility = View.VISIBLE
            binding.startButton.text = "Play again"
        }.show()

    }

    override fun onStart() {
        super.onStart()
        Log.i(localClassName, "onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.i(localClassName, "onResume")
    }
    override fun onPause() {
        super.onPause()
        Log.i(localClassName, "onPause")
    }
    override fun onStop() {
        super.onStop()
        Log.i(localClassName, "onStop")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(localClassName, "onDestroy")
    }
}