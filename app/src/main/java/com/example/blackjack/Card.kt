package com.example.blackjack

import java.util.*

class Card (val numCards: Int = 13){
    fun draw(isRangesRandom: Boolean = true): Int {
        if(isRangesRandom) {
            return (1..numCards).random()
        }
        else {
            return Random().nextInt(numCards) + 1
        }
    }
}
