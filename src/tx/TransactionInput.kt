package tx

class TransactionInput(val transactionId: String) {
    var UTXO: TransactionOutput? = null
}
