package bitcoin

import util.HashAlgorithm
import java.sql.Timestamp
import kotlin.streams.asSequence

private val hashAlgorithm = HashAlgorithm()
val userList: ArrayList<User> = arrayListOf()
var transactionList: MutableList<Transaction> = arrayListOf()
val transactionListIds: ArrayList<String> = arrayListOf()

val blockchain: ArrayList<Block> = arrayListOf()

fun main() {

    for(i in 0..100) {
        userList.add(generateUser(i))
    }

    for(i in 0..1000) {
        transactionList.add(generateTransaction(userList))
        transactionListIds.add(transactionList[i].transactionId)
    }

    var index = 0
    while(transactionList.size >= 100) {
        val tempTransactionIdList = arrayListOf<String>()
        val tempTransactionList = transactionList.subList(0, 100)
        tempTransactionList.forEach { tempTransactionIdList.add(it.transactionId) }
        val transactionMerkleHash = merkleTree(tempTransactionIdList)
        when(blockchain.size) {
            0 -> {
                val block = Block(
                        timeStamp = Timestamp(System.currentTimeMillis()),
                        merkelRoot = transactionMerkleHash,
                        transactions = tempTransactionList
                )
                block.mine()
                blockchain.add(block)
            }
            else -> {
                val block = Block(
                        previousBlock = blockchain[blockchain.size - 1].getHash(),
                        timeStamp = Timestamp(System.currentTimeMillis()),
                        merkelRoot = transactionMerkleHash,
                        transactions = tempTransactionList
                )
                block.mine()
                blockchain.add(block)
            }
        }
        index++
        transactionList = transactionList.subList(100, transactionList.size)
    }

    blockchain.forEach {
        println("${it.getHash()} ${it.getNonce()} ")
    }
}

private fun generateUser(index: Int): User{
    val name = "user$index"
    val publicKey = hashAlgorithm.hashFunction(generateRandomString())
    val balance = (100..1000000).random().toDouble()
    return User(name, publicKey, balance)
}

private fun generateRandomString(): String{
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return java.util.Random().ints(64, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
}

private fun generateTransaction(users: ArrayList<User>): Transaction{
    val firstUserIndex = (0..100).random()
    val secondUserIndex = (0..100).random()
    val senderKey = users[firstUserIndex].publicKey
    val receiverKey = users[secondUserIndex].publicKey
    val amount = users[firstUserIndex].balance * 0.01

    val txId = hashAlgorithm.hashFunction(senderKey + receiverKey + amount.toString())

    userList[firstUserIndex].pay(amount)
    userList[secondUserIndex].receive(amount)

    return Transaction(txId, senderKey, receiverKey, amount)
}

private fun merkleTree(transactionsId: MutableList<String>): String{
    val tempTxList: ArrayList<String> = arrayListOf()
    for(i in 0 until transactionsId.size - 1 step 2){
        tempTxList.add(hashAlgorithm.hashFunction(transactionsId[i] + transactionsId[i + 1]))
    }
    return if(tempTxList.size == 1) tempTxList[0]
    else merkleTree(tempTxList)
}

