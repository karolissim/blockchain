package tx

import blockchain.MyBlock
import util.HashAlgorithm

class Transaction(private val senderId: String, val receiverId: String, val value: Double, private var txInputs: ArrayList<TransactionInput>?) {
    var txId: String? = null
    var txOutputs: ArrayList<TransactionOutput> = arrayListOf()

    init {
        txId = HashAlgorithm().hashFunction(senderId + receiverId + value)
    }

    private fun verifyTransactionId(): Boolean {
        return txId == HashAlgorithm().hashFunction(senderId + receiverId + value)
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
            if(input.UTXO != null) {
                MyBlock.UTXOs.remove(input.UTXO!!.id)
            }
        }

        return true
    }

    fun verifyTransaction(index: Int): Boolean {
        for(inputs in txInputs!!) {
            inputs.UTXO = MyBlock.tempUTXOList[index][inputs.transactionId]
        }

        val leftBalance = getInputValue() - value
        txOutputs.add(TransactionOutput(senderId, leftBalance, txId!!))
        txOutputs.add(TransactionOutput(receiverId, value, txId!!))

        for(output in txOutputs) {
            MyBlock.tempUTXOList[index][output.id!!] = output
        }

        for(input in txInputs!!) {
            if(input.UTXO != null) {
                MyBlock.tempUTXOList[index].remove(input.UTXO!!.id)
            }
        }

        return true
    }

    private fun getInputValue(): Double {
        var totalAmount = 0.0
        for(input in txInputs!!) {
            if(input.UTXO != null) {
                totalAmount += input.UTXO!!.value
            }
        }
        return totalAmount
    }
}
