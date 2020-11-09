package tx

import hash.HashAlgorithm

class Transaction(private val senderId: String, private val receiverId: String, private val value: Double, var transactionInputs: ArrayList<TransactionInput>) {
    var transactionId: String? = null
    var transactionOutputs: ArrayList<TransactionOutput> = arrayListOf()

    fun generateTransactionId() {
        transactionId = HashAlgorithm().hashFunction(senderId + receiverId + value)
    }

    fun verifyTransaction(): Boolean {
        return transactionId == HashAlgorithm().hashFunction(senderId + receiverId + value)
    }

}
