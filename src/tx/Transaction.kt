package tx

import util.HashAlgorithm

class Transaction(private val senderId: String, private val receiverId: String, private val value: Double, var txInputs: ArrayList<TransactionInput>) {

    companion object {
        var txIndex = 0
    }

    var txId: String? = null
    private var currentTxIndex: Int? = null
    var txOutputs: ArrayList<TransactionOutput> = arrayListOf()

    init {
        txId = HashAlgorithm().hashFunction(senderId + receiverId + value + txIndex.toString())
        currentTxIndex = txIndex
        txIndex++
    }

    fun verifyTransaction(): Boolean {
        return txId == HashAlgorithm().hashFunction(senderId + receiverId + value + currentTxIndex)
    }
}
