"use client";

import { useCallback, useState } from "react";
import { useConnection, useWallet, useAnchorWallet } from "@solana/wallet-adapter-react";
import { PublicKey, LAMPORTS_PER_SOL, SystemProgram } from "@solana/web3.js";
import { BN } from "@coral-xyz/anchor";
import {
  getProgram,
  getPoolPDA,
  getStakerPDA,
  getThreatPDA,
  getBountyPDA,
} from "@/lib/program";
import { SHIELDMESH_PROGRAM_ID } from "@/lib/idl";
import type { Severity } from "@/types/threat";

// Map severity string to on-chain u8 value
const SEVERITY_MAP: Record<Severity, number> = {
  LOW: 0,
  MEDIUM: 1,
  HIGH: 2,
  CRITICAL: 3,
};

const SEVERITY_FROM_U8: Record<number, Severity> = {
  0: "LOW",
  1: "MEDIUM",
  2: "HIGH",
  3: "CRITICAL",
};

const STATUS_FROM_U8: Record<number, string> = {
  0: "PENDING",
  1: "VERIFIED",
  2: "SETTLED",
};

export interface PoolState {
  authority: string;
  totalStaked: number;
  stakerCount: number;
  rewardRates: number[];
}

export interface StakerState {
  owner: string;
  pool: string;
  amount: number;
  reputation: number;
}

export interface OnChainThreat {
  pubkey: string;
  reporter: string;
  threatHash: number[];
  severity: Severity;
  aiScore: number;
  validatorCount: number;
  status: string;
  timestamp: number;
}

export interface OnChainBounty {
  pubkey: string;
  reporter: string;
  threat: string;
  amount: number;
  claimed: boolean;
}

export interface TxResult {
  success: boolean;
  signature?: string;
  error?: string;
}

/**
 * Pool authority public key -- defaults to wallet pubkey for initialize,
 * but for fetching pools created by others you can pass a known authority.
 * For this app we store the known pool authority in localStorage after init,
 * or you can set it via the NEXT_PUBLIC_POOL_AUTHORITY env var.
 */
function getPoolAuthority(): PublicKey | null {
  const envAuth = process.env.NEXT_PUBLIC_POOL_AUTHORITY;
  if (envAuth) {
    try {
      return new PublicKey(envAuth);
    } catch {
      // invalid key, ignore
    }
  }
  if (typeof window !== "undefined") {
    const stored = localStorage.getItem("shieldmesh_pool_authority");
    if (stored) {
      try {
        return new PublicKey(stored);
      } catch {
        // invalid
      }
    }
  }
  return null;
}

export function useShieldMesh() {
  const { connection } = useConnection();
  const { publicKey, connected } = useWallet();
  const anchorWallet = useAnchorWallet();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // --- helpers ---

  const getAnchorProgram = useCallback(() => {
    if (!anchorWallet) throw new Error("Wallet not connected");
    return getProgram(connection, anchorWallet);
  }, [connection, anchorWallet]);

  const resolvePoolAuthority = useCallback((): PublicKey => {
    const stored = getPoolAuthority();
    if (stored) return stored;
    if (publicKey) return publicKey;
    throw new Error("No pool authority found. Initialize a pool first or set NEXT_PUBLIC_POOL_AUTHORITY.");
  }, [publicKey]);

  // --- instructions ---

  const initializePool = useCallback(
    async (rewardRates: number[]): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const rates = rewardRates.map((r) => new BN(r * LAMPORTS_PER_SOL));
        const [poolPDA] = getPoolPDA(publicKey);

        const sig = await program.methods
          .initializePool(rates)
          .accounts({
            authority: publicKey,
            pool: poolPDA,
            systemProgram: SystemProgram.programId,
          })
          .rpc();

        // Remember this pool authority
        if (typeof window !== "undefined") {
          localStorage.setItem("shieldmesh_pool_authority", publicKey.toBase58());
        }

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to initialize pool";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey],
  );

  const stake = useCallback(
    async (amount: number): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const authority = resolvePoolAuthority();
        const [poolPDA] = getPoolPDA(authority);
        const [stakerPDA] = getStakerPDA(poolPDA, publicKey);

        const lamports = new BN(Math.floor(amount * LAMPORTS_PER_SOL));

        const sig = await program.methods
          .stake(lamports)
          .accounts({
            staker: publicKey,
            pool: poolPDA,
            stakerAccount: stakerPDA,
            systemProgram: SystemProgram.programId,
          })
          .rpc();

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to stake";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey, resolvePoolAuthority],
  );

  const unstake = useCallback(
    async (amount: number): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const authority = resolvePoolAuthority();
        const [poolPDA] = getPoolPDA(authority);
        const [stakerPDA] = getStakerPDA(poolPDA, publicKey);

        const lamports = new BN(Math.floor(amount * LAMPORTS_PER_SOL));

        const sig = await program.methods
          .unstake(lamports)
          .accounts({
            staker: publicKey,
            pool: poolPDA,
            stakerAccount: stakerPDA,
            systemProgram: SystemProgram.programId,
          })
          .rpc();

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to unstake";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey, resolvePoolAuthority],
  );

  const reportThreat = useCallback(
    async (
      threatHash: number[],
      severity: Severity,
      aiScore: number,
    ): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const authority = resolvePoolAuthority();
        const [poolPDA] = getPoolPDA(authority);

        const hashArray = Uint8Array.from(threatHash);
        const [threatPDA] = getThreatPDA(poolPDA, hashArray);
        const [bountyPDA] = getBountyPDA(threatPDA);

        const sig = await program.methods
          .reportThreat(Array.from(hashArray), SEVERITY_MAP[severity], aiScore)
          .accounts({
            reporter: publicKey,
            pool: poolPDA,
            threat: threatPDA,
            bounty: bountyPDA,
            systemProgram: SystemProgram.programId,
          })
          .rpc();

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to report threat";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey, resolvePoolAuthority],
  );

  const verifyThreat = useCallback(
    async (threatPubkey: string): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const authority = resolvePoolAuthority();
        const [poolPDA] = getPoolPDA(authority);
        const threatKey = new PublicKey(threatPubkey);
        const [stakerPDA] = getStakerPDA(poolPDA, publicKey);

        const sig = await program.methods
          .verifyThreat()
          .accounts({
            validator: publicKey,
            pool: poolPDA,
            threat: threatKey,
            stakerAccount: stakerPDA,
          })
          .rpc();

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to verify threat";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey, resolvePoolAuthority],
  );

  const claimBounty = useCallback(
    async (threatPubkey: string): Promise<TxResult> => {
      setLoading(true);
      setError(null);
      try {
        const program = getAnchorProgram();
        if (!publicKey) throw new Error("Wallet not connected");

        const authority = resolvePoolAuthority();
        const [poolPDA] = getPoolPDA(authority);
        const threatKey = new PublicKey(threatPubkey);
        const [bountyPDA] = getBountyPDA(threatKey);

        const sig = await program.methods
          .claimBounty()
          .accounts({
            reporter: publicKey,
            pool: poolPDA,
            threat: threatKey,
            bounty: bountyPDA,
            systemProgram: SystemProgram.programId,
          })
          .rpc();

        setLoading(false);
        return { success: true, signature: sig };
      } catch (err: any) {
        const msg = err?.message || "Failed to claim bounty";
        setError(msg);
        setLoading(false);
        return { success: false, error: msg };
      }
    },
    [getAnchorProgram, publicKey, resolvePoolAuthority],
  );

  // --- fetch functions ---

  const fetchPool = useCallback(async (): Promise<PoolState | null> => {
    try {
      const authority = resolvePoolAuthority();
      const [poolPDA] = getPoolPDA(authority);

      if (!anchorWallet) {
        // Read-only: use a raw account fetch
        const accountInfo = await connection.getAccountInfo(poolPDA);
        if (!accountInfo) return null;
        // Need anchor program to decode even for read
        return null;
      }

      const program = getAnchorProgram();
      const pool = await (program.account as any).stakingPool.fetch(poolPDA);

      return {
        authority: pool.authority.toBase58(),
        totalStaked: pool.totalStaked.toNumber() / LAMPORTS_PER_SOL,
        stakerCount: pool.stakerCount,
        rewardRates: pool.rewardRates.map((r: any) => r.toNumber() / LAMPORTS_PER_SOL),
      };
    } catch {
      return null;
    }
  }, [connection, anchorWallet, getAnchorProgram, resolvePoolAuthority]);

  const fetchStaker = useCallback(async (): Promise<StakerState | null> => {
    try {
      if (!publicKey || !anchorWallet) return null;

      const program = getAnchorProgram();
      const authority = resolvePoolAuthority();
      const [poolPDA] = getPoolPDA(authority);
      const [stakerPDA] = getStakerPDA(poolPDA, publicKey);

      const staker = await (program.account as any).stakerAccount.fetch(stakerPDA);

      return {
        owner: staker.owner.toBase58(),
        pool: staker.pool.toBase58(),
        amount: staker.amount.toNumber() / LAMPORTS_PER_SOL,
        reputation: staker.reputation,
      };
    } catch {
      return null;
    }
  }, [connection, publicKey, anchorWallet, getAnchorProgram, resolvePoolAuthority]);

  const fetchThreats = useCallback(async (): Promise<OnChainThreat[]> => {
    try {
      if (!anchorWallet) return [];
      const program = getAnchorProgram();
      const accounts = await (program.account as any).threatAccount.all();

      return accounts.map((acc: any) => ({
        pubkey: acc.publicKey.toBase58(),
        reporter: acc.account.reporter.toBase58(),
        threatHash: Array.from(acc.account.threatHash),
        severity: SEVERITY_FROM_U8[acc.account.severity] || "LOW",
        aiScore: acc.account.aiScore,
        validatorCount: acc.account.validatorCount,
        status: STATUS_FROM_U8[acc.account.status] || "PENDING",
        timestamp: acc.account.timestamp.toNumber() * 1000,
      }));
    } catch {
      return [];
    }
  }, [anchorWallet, getAnchorProgram]);

  const fetchBounties = useCallback(async (): Promise<OnChainBounty[]> => {
    try {
      if (!anchorWallet) return [];
      const program = getAnchorProgram();
      const accounts = await (program.account as any).bountyAccount.all();

      return accounts.map((acc: any) => ({
        pubkey: acc.publicKey.toBase58(),
        reporter: acc.account.reporter.toBase58(),
        threat: acc.account.threat.toBase58(),
        amount: acc.account.amount.toNumber() / LAMPORTS_PER_SOL,
        claimed: acc.account.claimed,
      }));
    } catch {
      return [];
    }
  }, [anchorWallet, getAnchorProgram]);

  return {
    // state
    loading,
    error,
    connected: connected && !!anchorWallet,

    // instructions
    initializePool,
    stake,
    unstake,
    reportThreat,
    verifyThreat,
    claimBounty,

    // queries
    fetchPool,
    fetchStaker,
    fetchThreats,
    fetchBounties,
  };
}
