package com.shieldmesh.app.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolanaRpcClient @Inject constructor() {

    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Default to devnet; can be changed at runtime
    var rpcUrl: String = "https://api.devnet.solana.com"

    private suspend fun <T> rpcCall(method: String, params: List<Any>, typeToken: TypeToken<RpcResponse<T>>): T? {
        return withContext(Dispatchers.IO) {
            val rpcRequest = RpcRequest(method = method, params = params)
            val body = gson.toJson(rpcRequest).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(rpcUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext null

            val rpcResponse: RpcResponse<T> = gson.fromJson(responseBody, typeToken.type)
            if (rpcResponse.error != null) {
                throw SolanaRpcException(rpcResponse.error.code, rpcResponse.error.message)
            }
            rpcResponse.result
        }
    }

    suspend fun getBalance(pubkey: String): Long {
        val result = rpcCall<BalanceResult>(
            method = "getBalance",
            params = listOf(pubkey),
            typeToken = object : TypeToken<RpcResponse<BalanceResult>>() {}
        )
        return result?.value ?: 0L
    }

    suspend fun getLatestBlockhash(): String {
        val result = rpcCall<BlockhashResult>(
            method = "getLatestBlockhash",
            params = listOf(mapOf("commitment" to "finalized")),
            typeToken = object : TypeToken<RpcResponse<BlockhashResult>>() {}
        )
        return result?.value?.blockhash ?: throw SolanaRpcException(-1, "Failed to get blockhash")
    }

    suspend fun sendTransaction(signedTxBase64: String): String {
        val result = rpcCall<String>(
            method = "sendTransaction",
            params = listOf(signedTxBase64, mapOf("encoding" to "base64")),
            typeToken = object : TypeToken<RpcResponse<String>>() {}
        )
        return result ?: throw SolanaRpcException(-1, "Failed to send transaction")
    }

    suspend fun getAccountInfo(pubkey: String): AccountInfoValue? {
        val result = rpcCall<AccountInfoResult>(
            method = "getAccountInfo",
            params = listOf(pubkey, mapOf("encoding" to "base64")),
            typeToken = object : TypeToken<RpcResponse<AccountInfoResult>>() {}
        )
        return result?.value
    }

    suspend fun getSignatureStatuses(signatures: List<String>): List<SignatureStatus?> {
        val result = rpcCall<SignatureStatusResult>(
            method = "getSignatureStatuses",
            params = listOf(signatures),
            typeToken = object : TypeToken<RpcResponse<SignatureStatusResult>>() {}
        )
        return result?.value ?: emptyList()
    }

    fun lamportsToSol(lamports: Long): Double = lamports / 1_000_000_000.0

    fun solToLamports(sol: Double): Long = (sol * 1_000_000_000).toLong()
}

class SolanaRpcException(val code: Int, override val message: String) : Exception(message)
