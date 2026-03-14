package com.shieldmesh.app.data.remote

import com.google.gson.annotations.SerializedName

// JSON-RPC request envelope
data class RpcRequest(
    val jsonrpc: String = "2.0",
    val id: Int = 1,
    val method: String,
    val params: List<Any> = emptyList()
)

// JSON-RPC response envelope
data class RpcResponse<T>(
    val jsonrpc: String,
    val id: Int,
    val result: T?,
    val error: RpcError?
)

data class RpcError(
    val code: Int,
    val message: String
)

// getBalance
data class BalanceResult(
    val context: ContextSlot,
    val value: Long
)

data class ContextSlot(
    val slot: Long
)

// getAccountInfo
data class AccountInfoResult(
    val context: ContextSlot,
    val value: AccountInfoValue?
)

data class AccountInfoValue(
    val data: List<String>,
    val executable: Boolean,
    val lamports: Long,
    val owner: String,
    val rentEpoch: Long
)

// sendTransaction
data class SendTransactionResult(
    val result: String?
)

// getRecentBlockhash / getLatestBlockhash
data class BlockhashResult(
    val context: ContextSlot,
    val value: BlockhashValue
)

data class BlockhashValue(
    val blockhash: String,
    @SerializedName("lastValidBlockHeight")
    val lastValidBlockHeight: Long
)

// getSignatureStatuses
data class SignatureStatusResult(
    val context: ContextSlot,
    val value: List<SignatureStatus?>
)

data class SignatureStatus(
    val slot: Long,
    val confirmations: Int?,
    val err: Any?,
    @SerializedName("confirmationStatus")
    val confirmationStatus: String?
)

// Transaction history item
data class TransactionRecord(
    val signature: String,
    val timestamp: Long,
    val amount: Double,
    val type: String // "send" or "receive"
)
