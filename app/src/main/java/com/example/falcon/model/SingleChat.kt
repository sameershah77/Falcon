package com.example.falcon.model

class SingleChat {
    var email:String = ""
    var name:String = ""
    var message:String = ""
    var time:String = "00:00"

    constructor() {}

    constructor(email:String,name:String,message:String,time:String) {
        this.email = email
        this.name = name
        this.message = message
        this.time = time
    }

}