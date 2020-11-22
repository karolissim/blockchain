package blockchain

import kotlinx.coroutines.*
import tx.Transaction
import tx.TransactionOutput
import util.HashAlgorithm
import java.sql.Timestamp
import kotlin.streams.asSequence

class MyBlock {

    companion object {
        private val transactionPool: ArrayList<Transaction?> = arrayListOf()
        var UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf()
        private val blockchain: ArrayList<Block> = arrayListOf()
        private val userList: ArrayList<User> = arrayListOf()
        private const val txDifficulty = 3
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

            for (j in (0..3)) {
                tempTransactionPool.add(generateTransactionMap())
                tempUTXOList.add(UTXOs)
            }

            GlobalScope.launch {

                while(transactionPool.size >= 10) {
                    if (isActive && !isCalled) {
                        firstMiner = launch(Dispatchers.IO) {
                            mine(0, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }

                        secondMiner = launch(Dispatchers.IO) {
                            mine(1, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }

                        thirdMiner = launch(Dispatchers.IO) {
                            mine(2, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }

                        fourthMiner = launch(Dispatchers.IO) {
                            mine(3, object : JobCallback {
                                override suspend fun onFinish(status: Boolean, index: Int?, block: Block?) {
                                    if (status) {
                                        if(!isCalled) {
                                            isCalled = true
                                            blockchain.add(block!!)
                                            previousBlockHash = block.getHash()
                                            cancelJob(index!!)
                                        }
                                    }
                                }
                            })
                        }

                        delay(2000L)
                    } else isCalled = false
                }
            }

            runBlocking {
                delay(20000L)
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
            println("Created ${blockchain.size} blocks")
            for (block in blockchain) {
                println("currentBlock: ${block.getHash()} prevBlock: ${block.previousBlock}")
            }
        }

        private fun generateTransactionMap(): MutableMap<Int, Transaction?> {
            val txMap: MutableMap<Int, Transaction?> = mutableMapOf()
            val size = transactionPool.size - 1
            for (i in (0..9)) {
                val random = (0..size).random()
                txMap[random] = transactionPool[random]
            }
            return txMap
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
            thirdMiner!!.cancel()
            fourthMiner!!.cancel()

            UTXOs = tempUTXOList[index]
            for((_, value) in tempTransactionPool[index]){
                transactionPool.remove(value)
            }

            tempUTXOList.clear()
            tempTransactionPool.clear()

            for (j in (0..3)) {
                tempTransactionPool.add(generateTransactionMap())
                tempUTXOList.add(UTXOs)
            }
        }
    }

    interface JobCallback {
        suspend fun onFinish(status: Boolean, index: Int?, block: Block?)
    }
}
