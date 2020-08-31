package com.example.myapplication

typealias Pokedex = List<Pokemon>

data class Pokemon(
    val name: String,
    val eintraege: List<String>
){}