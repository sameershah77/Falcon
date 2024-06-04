package com.example.falcon.model

class ChatMessageData {
    var isGroup = false
    var chatId = ""
    var messageArr : MutableList<SingleChat> = mutableListOf()
    var uidArr : MutableList<String> = mutableListOf()

    constructor(){}

    constructor(isGroup:Boolean, chatId: String, messageArr:MutableList<SingleChat>, uidArr: MutableList<String>) {
        this.isGroup = isGroup
        this.chatId = chatId
        this.uidArr = uidArr
        this.messageArr = messageArr
    }
}