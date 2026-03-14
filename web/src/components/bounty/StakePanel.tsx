"use client";

import { useState } from "react";
import { useWalletStore } from "@/store/walletStore";
import { useShieldMesh } from "@/hooks/useShieldMesh";

export default function StakePanel() {
  const { balance, stakedAmount, setStakedAmount, setBalance } = useWalletStore();
  const { stake, unstake, loading, connected, fetchStaker, fetchPool } = useShieldMesh();

  const [amount, setAmount] = useState("");
  const [mode, setMode] = useState<"stake" | "unstake">("stake");
  const [txStatus, setTxStatus] = useState<{
    type: "success" | "error" | null;
    message: string;
    signature?: string;
  }>({ type: null, message: "" });

  const [poolBalance, setPoolBalance] = useState(1247.5);

  const handleAction = async () => {
    const val = parseFloat(amount);
    if (isNaN(val) || val <= 0) return;
    if (mode === "stake" && val > balance) return;
    if (mode === "unstake" && val > stakedAmount) return;

    setTxStatus({ type: null, message: "" });

    if (connected) {
      // On-chain transaction
      const result = mode === "stake" ? await stake(val) : await unstake(val);

      if (result.success) {
        setTxStatus({
          type: "success",
          message: `${mode === "stake" ? "Staked" : "Unstaked"} ${val} SOL successfully`,
          signature: result.signature,
        });

        // Update local state optimistically, then sync from chain
        if (mode === "stake") {
          setStakedAmount(stakedAmount + val);
          setBalance(balance - val);
        } else {
          setStakedAmount(stakedAmount - val);
          setBalance(balance + val);
        }

        // Fetch real on-chain data in background
        try {
          const [stakerData, poolData] = await Promise.all([fetchStaker(), fetchPool()]);
          if (stakerData) setStakedAmount(stakerData.amount);
          if (poolData) setPoolBalance(poolData.totalStaked);
        } catch {
          // Optimistic update is fine
        }

        setAmount("");
      } else {
        setTxStatus({
          type: "error",
          message: result.error || `Failed to ${mode}`,
        });
      }
    } else {
      // Offline / mock mode
      if (mode === "stake") {
        setStakedAmount(stakedAmount + val);
        setBalance(balance - val);
      } else {
        setStakedAmount(stakedAmount - val);
        setBalance(balance + val);
      }
      setAmount("");
      setTxStatus({
        type: "success",
        message: `${mode === "stake" ? "Staked" : "Unstaked"} ${val} SOL (demo mode - connect wallet for on-chain)`,
      });
    }
  };

  return (
    <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-6 space-y-5">
      <h3 className="text-white font-semibold flex items-center gap-2">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#00d4ff" strokeWidth="2"><rect x="2" y="4" width="20" height="16" rx="2" /><path d="M12 8v8M8 12h8" /></svg>
        Staking Pool
        {!connected && (
          <span className="ml-auto text-[10px] text-yellow-400/70 font-normal uppercase tracking-wider">
            demo mode
          </span>
        )}
      </h3>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div className="bg-[#0a0a0f] rounded-lg p-3 border border-[#1a1a2e]">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-1">Pool Balance</div>
          <div className="text-lg font-bold text-[#00d4ff] font-mono">{poolBalance.toLocaleString()}</div>
          <div className="text-[10px] text-gray-600">SOL</div>
        </div>
        <div className="bg-[#0a0a0f] rounded-lg p-3 border border-[#1a1a2e]">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-1">Your Stake</div>
          <div className="text-lg font-bold text-[#00ff88] font-mono">{stakedAmount.toFixed(2)}</div>
          <div className="text-[10px] text-gray-600">SOL</div>
        </div>
        <div className="bg-[#0a0a0f] rounded-lg p-3 border border-[#1a1a2e]">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-1">APY</div>
          <div className="text-lg font-bold text-[#00ff88] font-mono">12.4%</div>
          <div className="text-[10px] text-gray-600">estimated</div>
        </div>
      </div>

      <div className="flex gap-2">
        <button
          onClick={() => setMode("stake")}
          className={`flex-1 py-2 rounded-lg text-sm font-medium transition-all ${
            mode === "stake"
              ? "bg-[#00ff88]/15 text-[#00ff88] border border-[#00ff88]/30"
              : "bg-white/5 text-gray-400 border border-[#1a1a2e] hover:text-white"
          }`}
        >
          Stake
        </button>
        <button
          onClick={() => setMode("unstake")}
          className={`flex-1 py-2 rounded-lg text-sm font-medium transition-all ${
            mode === "unstake"
              ? "bg-red-500/15 text-red-400 border border-red-500/30"
              : "bg-white/5 text-gray-400 border border-[#1a1a2e] hover:text-white"
          }`}
        >
          Unstake
        </button>
      </div>

      <div>
        <label className="text-xs text-gray-500 mb-1 block font-mono">
          Amount (SOL) - Available: {mode === "stake" ? balance.toFixed(4) : stakedAmount.toFixed(4)}
        </label>
        <div className="flex gap-2">
          <input
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
            step="0.01"
            min="0"
            className="flex-1 bg-[#0a0a0f] border border-[#1a1a2e] rounded-lg px-4 py-2.5 text-sm text-white font-mono placeholder-gray-600 focus:outline-none focus:border-[#00ff88]/40 transition-all"
          />
          <button
            onClick={() => setAmount(mode === "stake" ? balance.toFixed(4) : stakedAmount.toFixed(4))}
            className="px-3 py-2.5 text-xs text-[#00ff88] border border-[#00ff88]/30 rounded-lg hover:bg-[#00ff88]/10 transition-all"
          >
            MAX
          </button>
        </div>
      </div>

      <button
        onClick={handleAction}
        disabled={!amount || parseFloat(amount) <= 0 || loading}
        className="w-full py-3 rounded-lg text-sm font-bold transition-all bg-gradient-to-r from-[#00ff88] to-[#00d4ff] text-[#0a0a0f] hover:opacity-90 disabled:opacity-40 disabled:cursor-not-allowed shadow-[0_0_20px_rgba(0,255,136,0.15)]"
      >
        {loading ? (
          <span className="flex items-center justify-center gap-2">
            <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Processing...
          </span>
        ) : mode === "stake" ? (
          "Stake SOL"
        ) : (
          "Unstake SOL"
        )}
      </button>

      {/* Transaction status */}
      {txStatus.type && (
        <div
          className={`rounded-lg p-3 text-sm font-mono border ${
            txStatus.type === "success"
              ? "bg-green-500/10 border-green-500/30 text-green-400"
              : "bg-red-500/10 border-red-500/30 text-red-400"
          }`}
        >
          <div>{txStatus.message}</div>
          {txStatus.signature && (
            <a
              href={`https://explorer.solana.com/tx/${txStatus.signature}?cluster=devnet`}
              target="_blank"
              rel="noopener noreferrer"
              className="text-[10px] text-[#00d4ff] hover:underline mt-1 block break-all"
            >
              TX: {txStatus.signature.slice(0, 20)}...{txStatus.signature.slice(-8)}
            </a>
          )}
        </div>
      )}
    </div>
  );
}
