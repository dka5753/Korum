package com.example.korum.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name ="users")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id :Long ? = null,
    var email : String,
    var username :String

)