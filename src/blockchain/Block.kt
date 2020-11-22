package blockchain

import tx.Transaction
import util.HashAlgorithm
import util.MerkleRoot
import java.sql.Timestamp

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
        MerkleRoot().getMerkleTree(transactions.keys.toMutableList())
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

    fun getHash(): String {
        return currentBlock
    }
}
