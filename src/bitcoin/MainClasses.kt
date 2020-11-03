package bitcoin

import java.sql.Timestamp

data class User(val name: String, val publicKey: String, var balance: Double){
    fun isBalanceValid(amount: Double): Boolean{
        return balance >= amount
    }

    fun receive(amount: Double){
        balance += amount
    }

    fun pay(amount: Double){
        balance -= amount
    }
}

data class Transaction(val transactionId: String, val senderKey: String, val receiverKey: String, val sum: Double){
    fun validateTransaction(users: ArrayList<User>): Boolean{
        val userPosition: Int? = users.indices.find {
            users[it].publicKey == senderKey
        }
        return users[userPosition!!].balance >= sum
    }
}

data class Block(
        val previousBlock: String = "0",
        var currentBlock: String = "",
        val timeStamp: Timestamp,
        val version: String = "0.1",
        val merkelRoot: String = "",
        var nonce: Int = 0,
        val difficultyTarget: String = "",
        val transactions: ArrayList<Transaction> = arrayListOf()){
    fun mine(){

    }
}



