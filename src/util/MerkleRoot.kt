package util

import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import java.util.stream.Collectors

class MerkleRoot {
    fun getMerkleTree(transaction: MutableList<String>): String {
        val hashes = transaction.stream()
            .map { hash -> Sha256Hash.wrapReversed(Utils.HEX.decode(hash.toLowerCase()))}
            .collect(Collectors.toList())
        return Utils.HEX.encode(computeMerkle(hashes).reversedBytes)
    }

    private fun computeMerkle(hashList: MutableList<Sha256Hash>): Sha256Hash{
        var merkle = hashList
        if(merkle.isEmpty())
            return Sha256Hash.ZERO_HASH
        else if(merkle.size == 1)
            return merkle.first()

        while(merkle.size > 1) {
            if(merkle.size % 2 != 0)
                merkle.add(merkle.last())
            assert(merkle.size % 2 == 0)

            val newMerkle: ArrayList<Sha256Hash> = arrayListOf()
            for(i in 0 until merkle.size step 2) {
                val concatData = ByteArray(Sha256Hash.LENGTH * 2)
                System.arraycopy(merkle[i].bytes, 0, concatData, 0, Sha256Hash.LENGTH)
                System.arraycopy(merkle[i+1].bytes, 0, concatData, Sha256Hash.LENGTH, Sha256Hash.LENGTH)

                val newRoot = Sha256Hash.wrap(Sha256Hash.hashTwice(concatData))
                newMerkle.add(newRoot)
            }

            merkle = newMerkle
        }

        return merkle.first()
    }
}
