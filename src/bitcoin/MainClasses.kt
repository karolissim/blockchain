package bitcoin

import java.sql.Timestamp

data class User(val name: String, val publicKey: String, val balance: Int)

data class Transaction(val transactionId: String, val senderKey: String, val receiverKey: String, val sum: Int)

data class Block(
        val previousBlock: String,
        val currentBlock: String,
        val timeStamp: Timestamp,
        val version: String,
        val merkelRoot: String,
        val nonce: Int,
        val difficultyTarget: String)



