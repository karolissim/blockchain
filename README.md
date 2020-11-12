# blockchain

## v0.2
Simplified blockchain realisation with UTXO transaction model

## Data classes
```kotlin
class Block(
            var previousBlock: String = MyBlock.previousBlockHash,
            private val timeStamp: Timestamp,
            private val version: String = "0.1",
            private var nonce: Int = 0,
            private var currentBlock: String,
            private val transactions: MutableMap<String, Transaction> = mutableMapOf()
            private var merkleRoot: String? = null)

class User(
            val publicKey: String,
            private val UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf())

class Transaction(
            private val senderId: String,
            val receiverId: String, 
            val value: Double, 
            var txInputs: ArrayList<TransactionInput>?,
            var txId: String? = null,
            var txOutputs: ArrayList<TransactionOutput> = arrayListOf())

class TransactionInput(
            val transactionId: String,
            var UTXO: TransactionOutput? = null)

class TransactionOutput(
            val receiverId: String, 
            val value: Double, 
            val transactionId: String)
```

## Transaction verification
```kotlin
if (getBalance() < amount) {
    println("Balance is too low send money")
    return null
}

if(!verifyTransactionId()) {
    println("Failed to verify transaction id")
    return false
}

private fun verifyTransactionId(): Boolean {
    return txId == HashAlgorithm().hashFunction(senderId + receiverId + value)
}
```

### How it works
1. 10 random users and 100 random transactions are being generated 
2. Genesis block is being created which includes 10 transactions (1 transaction of 1000 coins for each user)
3. As I am trying to implement multithreading application 4 miners are creating as Jobs 
4. While transactionPool's size is greater than 10 mining is performed by different miners in sequence 
5. Miner's who has finished mining first block is being added to chain   
6. Process repeats until there is no more valid or possible transactions

### Results
This release isn't final version, although blockchain and transaction model works properly 
some coroutines improvements are needed for a program to work fully and properly.   
