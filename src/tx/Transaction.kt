package tx

import blockchain.MyBlock
import util.HashAlgorithm

class Transaction(val senderId: String, val receiverId: String, val value: Double, var txInputs: ArrayList<TransactionInput>?) {

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

    private fun verifyTransactionId(): Boolean {
        return txId == HashAlgorithm().hashFunction(senderId + receiverId + value + currentTxIndex)
    }

    fun verifyTransaction(): Boolean {
        if(!verifyTransactionId()) {
            println("Failed to verify transaction id")
            return false
        }

        for(inputs in txInputs!!) {
            inputs.UTXO = MyBlock.UTXOs[inputs.transactionId]
        }

        val leftBalance = getInputValue() - value
        txOutputs.add(TransactionOutput(senderId, leftBalance, txId!!))
        txOutputs.add(TransactionOutput(receiverId, value, txId!!))

        for(output in txOutputs) {
            MyBlock.UTXOs[output.id!!] = output
        }

        for(input in txInputs!!) {
            MyBlock.UTXOs.remove(input.UTXO!!.id)
        }

        return true
    }

    private fun getInputValue(): Double {
        var totalAmount = 0.0
        for(input in txInputs!!) {
            totalAmount += input.UTXO!!.value
        }
        return totalAmount
    }
}
