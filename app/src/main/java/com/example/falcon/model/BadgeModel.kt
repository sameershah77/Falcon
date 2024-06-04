package com.example.falcon.model

class BadgeModel {
    var badge:String = ""
    var date:String = ""

    constructor(){}

    constructor(badge:String, date:String) {
        this.badge = badge
        this.date = date
    }
}