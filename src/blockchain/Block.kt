package blockchain

import tx.Transaction
import util.HashAlgorithm
import java.sql.Timestamp
import kotlin.collections.ArrayList


class Block(var previousBlock: String = MyBlock.previousBlockHash,
            private val timeStamp: Timestamp,
            private val version: String = "0.1",
            private var nonce: Int = 0) {

    private lateinit var currentBlock: String
    private val transactions: MutableMap<String, Transaction> = mutableMapOf()
    private var merkleRoot: String? = null

    fun addTransaction(transaction: Transaction?, index: Int) {
        if(transaction != null) {
            if (previousBlock != "0") {
                transaction.verifyTransaction(index)
            }
            transactions[transaction.txId!!] = transaction
        }
    }

    fun addTransaction(transaction: Transaction?) {
        if(transaction != null) {
            if (previousBlock != "0") {
                transaction.verifyTransaction()
            }
            transactions[transaction.txId!!] = transaction
        }
    }

    fun mine(difficultyTarget: Int, nonceLimit: Int): Boolean {
        getMerkleRoot(transactions.keys.toMutableList())
        val target = String(CharArray(difficultyTarget)).replace('\u0000', '0')
        calculateHash()
        while (currentBlock.substring(0, difficultyTarget) != target) {
            nonce++
            calculateHash()
        }
        return nonce <= nonceLimit
    }

    private fun calculateHash() {
        currentBlock = HashAlgorithm().hashFunction(previousBlock +
            timeStamp.toString() +
            version +
            merkleRoot +
            nonce.toString())
    }

    private fun getMerkleRoot(transaction: MutableList<String>): String? {
        var transactionId = transaction
        var count = transactions.size

        var treeLayer = transactionId
        while (count > 1) {
            treeLayer = ArrayList()
            var i = 1
            while (i < transactionId.size) {
                treeLayer.add(HashAlgorithm().hashFunction(transactionId[i - 1] + transactionId[i]))
                i += 2
            }
            count = treeLayer.size
            transactionId = treeLayer
        }
        return if (treeLayer.size == 1) treeLayer[0] else ""
    }

    fun getHash(): String {
        return currentBlock
    }

    fun getNonce(): Int {
        return nonce
    }
}
