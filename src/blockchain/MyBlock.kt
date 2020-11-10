package blockchain

import tx.Transaction
import tx.TransactionOutput
import util.HashAlgorithm
import java.sql.Timestamp
import kotlin.streams.asSequence

class MyBlock {

    companion object {
        private val blockchain: ArrayList<Block> = arrayListOf()
        val UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf()
        private val userList: ArrayList<User> = arrayListOf()
        private const val txDifficulty = 3

        @JvmStatic
        fun main(args: Array<String>) {
            blockchain.add(generateGenesisBlock())

            for (i in (0..3)) {
                blockchain.add(generateBlock())
                println("currentBlock: ${blockchain[i + 1].getHash()} previousBlock: ${blockchain[i + 1].getPreviousBlock()}")
            }

            for (block in blockchain) {
                println(block.getHash())
            }

            for (user in userList) {
                println(user.getBalance())
            }
        }

        private fun generateRandomString(): String {
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return java.util.Random().ints(64, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
        }

        private fun generateGenesisBlock(): Block {
            val genesisUser = User(HashAlgorithm().hashFunction("GENESIS"))
            val genesisBlock = Block(timeStamp = Timestamp(System.currentTimeMillis()))

            for (i in (0..9)) {
                userList.add(User(HashAlgorithm().hashFunction(generateRandomString())))
                genesisBlock.addTransaction(generateGenesisTransaction(genesisUser, userList[i]))
            }

            genesisBlock.mine(txDifficulty)

            return genesisBlock
        }

        private fun generateGenesisTransaction(sender: User, receiver: User): Transaction {
            val transaction = Transaction(sender.publicKey, receiver.publicKey, 1000.0, null)
            transaction.let {
                it.txOutputs.add(TransactionOutput(it.receiverId, it.value, it.txId!!))
                UTXOs.put(it.txOutputs[0].id!!, it.txOutputs[0])
            }
            return transaction
        }

        private fun generateBlock(): Block {
            val block = Block(previousBlock = blockchain[blockchain.size - 1].getHash(), timeStamp = Timestamp(System.currentTimeMillis()))

            for (i in (0..99)) {
                block.addTransaction(userList[(0..9).random()].sendMoney(userList[(0..9).random()].publicKey, userList[(0..9).random()].getBalance() * 0.05))
            }

            block.mine(txDifficulty)

            return block
        }

    }
}
