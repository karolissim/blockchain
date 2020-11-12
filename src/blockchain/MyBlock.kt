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
        var UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf()
        private val userList: ArrayList<User> = arrayListOf()
        private val transactionPool: ArrayList<Transaction?> = arrayListOf()
        private const val txDifficulty = 2
        var previousBlockHash = "0"
        private var tempTransactionPool: ArrayList<MutableMap<Int, Transaction?>> = arrayListOf()
        var tempUTXOList: ArrayList<MutableMap<String, TransactionOutput>> = arrayListOf()
        var isCalled: Boolean = false

        var firstMiner: Job? = null
        var secondMiner: Job? = null
        var thirdMiner: Job? = null
        var fourthMiner: Job? = null

        @JvmStatic
        fun main(args: Array<String>) {
            blockchain.add(generateGenesisBlock())

            for (i in (0..99)) {
                transactionPool.add(userList[(0..9).random()].sendMoney(userList[(0..9).random()].publicKey, userList[(0..9).random()].getBalance() * 0.05))
            }

            for (j in (0..5)) {
                tempTransactionPool.add(generateTransactionMap())
                tempUTXOList.add(UTXOs)
            }

            GlobalScope.launch {

                while(transactionPool.size >= 10) {
                    println("$isCalled ${transactionPool.size}")
                    if (isActive && !isCalled) {
                        firstMiner = launch(Dispatchers.IO) {
                            mine(0, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            println("finished: $index")
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }

                        secondMiner = launch(Dispatchers.IO) {
                            delay(500L)
                            mine(1, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            println("finished: $index")
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }
                        delay(4000L)
                    } else isCalled = false
                }
            }

            runBlocking {
                delay(50000L)
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
            for (i in (0..9)) {
                val random = (0 until transactionPool.size).random()
                txMap[random] = transactionPool[random]
            }
            return txMap
        }

        private suspend fun createJob(index: Int, jobCallback: JobCallback): Job {

            return GlobalScope.launch {
                val block = Block(timeStamp = Timestamp(System.currentTimeMillis()))
                tempTransactionPool[index].values.forEach {
                    block.addTransaction(it, index)
                }
                if (block.mine(txDifficulty, 2000)) {
                    block.previousBlock = previousBlockHash
                    blockchain.add(block)
                    previousBlockHash = block.getHash()
                    jobCallback.onFinish(true, index, block)
                } else {
                    jobCallback.onFinish(false, null, null)
                    println("Unable to mine block")
                }
            }
        }

        private suspend fun mine(index: Int, jobCallback: JobCallback) {
            val block = Block(timeStamp = Timestamp(System.currentTimeMillis()))
            tempTransactionPool[index].values.forEach {
                block.addTransaction(it, index)
            }

            if (block.mine(txDifficulty, 7000)) {
                block.previousBlock = previousBlockHash
                jobCallback.onFinish(true, index, block)
            } else {
                jobCallback.onFinish(false, null, null)
                println("Unable to mine block")
            }
        }

        private fun cancelJob(index: Int) {
            firstMiner!!.cancel()
            secondMiner!!.cancel()
            println("CALLED")
            UTXOs = tempUTXOList[index]
            for((_, value) in tempTransactionPool[index]){
                transactionPool.remove(value)
            }
            for (j in (0..5)) {
                tempTransactionPool.add(generateTransactionMap())
                tempUTXOList.clear()
                tempUTXOList.add(UTXOs)
            }
            println("CANCELED")
        }
    }

    interface JobCallback {
        suspend fun onFinish(status: Boolean, index: Int?, block: Block?)
    }
}
