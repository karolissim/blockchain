package tx

import util.HashAlgorithm

class TransactionOutput(val receiverId: String, val value: Double, val transactionId: String) {
    var id: String? = null

    init {
        this.id = HashAlgorithm().hashFunction(receiverId + value + transactionId)
    }

    fun belongsToMe(publicKey: String): Boolean {
        return publicKey == receiverId
    }
}
