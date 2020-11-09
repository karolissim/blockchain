package blockchain

import tx.TransactionOutput

class MyBlock {

    companion object {
        val blockchain: ArrayList<Block> = arrayListOf()
        val UTXOs: Map<String, TransactionOutput> = mapOf()
        val txDifficulty = 3
    }

}
