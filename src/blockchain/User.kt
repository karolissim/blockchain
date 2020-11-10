package blockchain

import tx.Transaction
import tx.TransactionInput
import tx.TransactionOutput

class User(val publicKey: String) {
    private val UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf()

    fun getBalance(): Double {
        var balance = 0.0
        for ((_, value) in MyBlock.UTXOs) {
            if (value.belongsToMe(publicKey)) {
                UTXOs[value.id!!] = value
                balance += value.value
            }
        }
        return balance
    }

    fun sendMoney(receiverId: String, amount: Double): Transaction? {
        if (getBalance() < amount) {
            println("Balance is too low send money")
            return null
        }
        val inputsList: ArrayList<TransactionInput> = arrayListOf()
        var userAmount = 0.0
        for ((_, value) in UTXOs) {
            userAmount += value.value
            inputsList.add(TransactionInput(value.id!!))
            if (userAmount >= amount) break
        }
        for (input in inputsList) {
            UTXOs.remove(input.transactionId)
        }
        return Transaction(publicKey, receiverId, amount, inputsList)
    }
}
