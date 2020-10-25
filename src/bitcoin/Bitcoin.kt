package bitcoin

import hash.HashAlgorithm
import kotlin.streams.asSequence


private val hashAlgorithm = HashAlgorithm()
val userList: ArrayList<User> = arrayListOf()
val transactionList: ArrayList<Transaction> = arrayListOf()

fun main(args: Array<String>) {
    for(i in 0..10){
        userList.add(generateUser(i))
    }
    for(i in 0..100){
        transactionList.add(generateTransaction(userList))
    }
}


private fun generateUser(index: Int): User{
    val name = "user$index"
    val publicKey = hashAlgorithm.hashFunction(generateRandomString())
    val balance = (100..1000000).random()
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
    val user = users[(0..10).random()]
    val senderKey = user.publicKey
    val receiverKey = users[(0..10).random()].publicKey
    var amount = (100..1000000).random()

    while(amount > user.balance) amount = (100..1000000).random()

    val txId = hashAlgorithm.hashFunction(senderKey + receiverKey + amount.toString())

    return Transaction(txId, senderKey, receiverKey, amount)
}