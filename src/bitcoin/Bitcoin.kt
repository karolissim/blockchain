package bitcoin

import hash.HashAlgorithm
import kotlin.streams.asSequence

private val hashAlgorithm = HashAlgorithm()
val userList: ArrayList<User> = arrayListOf()
val transactionList: ArrayList<Transaction> = arrayListOf()
val transactionListIds: ArrayList<String> = arrayListOf()

val blockchain: ArrayList<Block> = arrayListOf()

fun main() {

    for(i in 0..100){
        userList.add(generateUser(i))
    }
    for(i in 0..9) println(userList[i].balance)
    println()

    for(i in 0..1000){
        transactionList.add(generateTransaction(userList))
        transactionListIds.add(transactionList[i].transactionId)
    }



    println(merkleTree(transactionListIds))
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

private fun merkleTree(transactionsId: ArrayList<String>): String{
    val tempTxList: ArrayList<String> = arrayListOf()
    for(i in 0 until transactionsId.size - 1 step 2){
        tempTxList.add(hashAlgorithm.hashFunction(transactionsId[i]+ transactionsId[i + 1]))
    }
    return if(tempTxList.size == 1) tempTxList[0]
    else merkleTree(tempTxList)
}

