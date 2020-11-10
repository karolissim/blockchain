package blockchain

import util.HashAlgorithm
import tx.Transaction
import java.sql.Timestamp

class Block(private val previousBlock: String = "0",
            private val timeStamp: Timestamp,
            private val version: String = "0.1",
            private var nonce: Int = 0) {

    private lateinit var currentBlock: String
    private val transactions: MutableMap<String, Transaction> = mutableMapOf()
    private var merkleRoot: String? = null

    fun addTransaction(transaction: Transaction?) {
        if(transaction != null) {
            if (previousBlock != "0") {
                transaction.verifyTransaction()
            }
            transactions[transaction.txId!!] = transaction
        }
    }

    fun mine(difficultyTarget: Int) {
        calculateMerkleTree(transactions.keys.toMutableList())
        val target = String(CharArray(difficultyTarget)).replace('\u0000', '0')
        calculateHash()
        while (currentBlock.substring(0, difficultyTarget) != target) {
            nonce++
            calculateHash()
        }
    }

    private fun calculateHash() {
        currentBlock = HashAlgorithm().hashFunction(previousBlock +
            timeStamp.toString() +
            version +
            merkleRoot +
            nonce.toString())
    }

    private fun calculateMerkleTree(transactionsId: MutableList<String>) {
        val tempTxList: ArrayList<String> = arrayListOf()
        for (i in 0 until transactionsId.size - 1 step 2) {
            tempTxList.add(HashAlgorithm().hashFunction(transactionsId[i] + transactionsId[i + 1]))
        }
        if (tempTxList.size == 1) merkleRoot = tempTxList[0]
        else calculateMerkleTree(tempTxList)
    }

    fun getHash(): String {
        return currentBlock
    }

    fun getPreviousBlock(): String {
        return previousBlock
    }

    fun getNonce(): Int {
        return nonce
    }
}
