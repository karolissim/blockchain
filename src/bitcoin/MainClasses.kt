package bitcoin

import util.HashAlgorithm
import java.sql.Timestamp

data class User(private val name: String, val publicKey: String, var balance: Double){
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
        private val previousBlock: String = "0",
        private var currentBlock: String = "",
        private val timeStamp: Timestamp,
        private val version: String = "0.1",
        private val merkelRoot: String,
        private var nonce: Int = 0,
        private val difficultyTarget: Int = 2,
        private val transactions: MutableList<Transaction>){

    fun mine(){
        val target = String(CharArray(difficultyTarget)).replace('\u0000', '0')
        calculateHash()
        while(currentBlock.substring(0, difficultyTarget) != target){
            nonce++
            calculateHash()
        }
    }

    private fun calculateHash(){
        currentBlock = HashAlgorithm().hashFunction(previousBlock + timeStamp.toString() + version + merkelRoot + difficultyTarget.toString() + nonce.toString())
    }

    fun getHash(): String {
        return currentBlock
    }

    fun getNonce(): Int {
        return nonce
    }
}



