package blockchain

import kotlinx.coroutines.*
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
        private val transactionPool: ArrayList<Transaction?> = arrayListOf()
        private const val txDifficulty = 2
        var previousBlockHash = "0"
        private var tempTransactionPool: ArrayList<MutableMap<Int, Transaction?>> = arrayListOf()
        var tempUTXOList: ArrayList<MutableMap<String, TransactionOutput>> = arrayListOf()

        @JvmStatic
        fun main(args: Array<String>) {
            blockchain.add(generateGenesisBlock())

            for(i in (0..99)) {
                transactionPool.add(userList[(0..9).random()].sendMoney(userList[(0..9).random()].publicKey, userList[(0..9).random()].getBalance() * 0.05))
            }

            for(i in (0..5)) {
                for (j in (0..5)) {
                    tempTransactionPool.add(generateTransactionMap())
                    tempUTXOList.add(UTXOs)
                }
            }

            //coroutines
            val job1 = GlobalScope.launch {
                val newBlock0 = Block(timeStamp = Timestamp(System.currentTimeMillis()))
                tempTransactionPool[0].values.forEach {
                    newBlock0.addTransaction(it, 0)
                }
                if(newBlock0.mine(txDifficulty, 2000)) {
                    newBlock0.previousBlock = previousBlockHash
                    blockchain.add(newBlock0)
                    previousBlockHash = newBlock0.getHash()
                } else {
                    println("Unable to mine block")
                }
            }

            val job2 = GlobalScope.launch {
                val newBlock1 = Block(timeStamp = Timestamp(System.currentTimeMillis()))
                tempTransactionPool[1].values.forEach {
                    newBlock1.addTransaction(it, 1)
                }
                if(newBlock1.mine(txDifficulty, 2000)) {
                    newBlock1.previousBlock = previousBlockHash
                    blockchain.add(newBlock1)
                    previousBlockHash = newBlock1.getHash()
                }else {
                    println("Unable to mine block")
                }
            }

            runBlocking {
                job1.join()
                job2.join()
                printData()
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

            genesisBlock.mine(txDifficulty, 10000)
            previousBlockHash = genesisBlock.getHash()
            println(genesisBlock.getHash())

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
            val block = Block(timeStamp = Timestamp(System.currentTimeMillis()))

            for (i in (0..99)) {
                block.addTransaction(userList[(0..9).random()].sendMoney(userList[(0..9).random()].publicKey, userList[(0..9).random()].getBalance() * 0.05))
            }

            block.mine(txDifficulty, 500)
            previousBlockHash = block.getHash()

            return block
        }
        
        private fun printData() {
            for (block in blockchain) {
                println("currentBlock: ${block.getHash()} prevBlock: ${block.previousBlock}")
            }
            for (user in userList) {
                println(user.getBalance())
            }
        }

        private fun generateTransactionMap(): MutableMap<Int, Transaction?> {
            val txMap: MutableMap<Int, Transaction?> = mutableMapOf()
            for(i in (0..9)) {
                val random = (0 until transactionPool.size).random()
                txMap[random] = transactionPool[random]
            }
            return txMap
        }

    }
}
