"use client";

import { useEffect, useCallback } from "react";
import { useConnection, useWallet } from "@solana/wallet-adapter-react";
import { LAMPORTS_PER_SOL } from "@solana/web3.js";
import { useWalletStore } from "@/store/walletStore";

/**
 * Syncs real Phantom wallet adapter state into the zustand walletStore
 * and fetches the real SOL balance from devnet.
 */
export function useWalletSync() {
  const { connection } = useConnection();
  const { connected, publicKey } = useWallet();
  const { setConnected, setPublicKey, setBalance, setBalanceLoading, reset } =
    useWalletStore();

  const fetchBalance = useCallback(async () => {
    if (!publicKey || !connection) return;
    try {
      setBalanceLoading(true);
      const lamports = await connection.getBalance(publicKey);
      setBalance(lamports / LAMPORTS_PER_SOL);
    } catch (err) {
      console.error("Failed to fetch SOL balance:", err);
      setBalance(0);
    } finally {
      setBalanceLoading(false);
    }
  }, [publicKey, connection, setBalance, setBalanceLoading]);

  // Sync connection state
  useEffect(() => {
    setConnected(connected);
    if (connected && publicKey) {
      setPublicKey(publicKey.toBase58());
    } else {
      reset();
    }
  }, [connected, publicKey, setConnected, setPublicKey, reset]);

  // Fetch balance whenever connected wallet changes
  useEffect(() => {
    if (connected && publicKey) {
      fetchBalance();
    }
  }, [connected, publicKey, fetchBalance]);

  // Subscribe to account changes for live balance updates
  useEffect(() => {
    if (!connected || !publicKey) return;

    const subscriptionId = connection.onAccountChange(
      publicKey,
      (accountInfo) => {
        setBalance(accountInfo.lamports / LAMPORTS_PER_SOL);
      },
      "confirmed"
    );

    return () => {
      connection.removeAccountChangeListener(subscriptionId);
    };
  }, [connected, publicKey, connection, setBalance]);

  return { fetchBalance };
}
