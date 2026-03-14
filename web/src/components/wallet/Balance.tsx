"use client";

import { useWalletStore } from "@/store/walletStore";

export default function Balance() {
  const { balance, balanceLoading, stakedAmount, earnedBounties, connected } =
    useWalletStore();

  if (!connected) {
    return (
      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-8 text-center">
        <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-white/5 flex items-center justify-center">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#444" strokeWidth="2">
            <rect x="2" y="4" width="20" height="16" rx="2" />
            <path d="M16 12h.01" />
            <path d="M2 10h20" />
          </svg>
        </div>
        <h3 className="text-white font-semibold mb-1">Wallet Not Connected</h3>
        <p className="text-sm text-gray-500">
          Connect your Phantom wallet to view balance details
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
        <div className="flex items-center gap-2 mb-3">
          <div className="w-8 h-8 rounded-lg bg-[#00ff88]/10 flex items-center justify-center">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2">
              <rect x="2" y="4" width="20" height="16" rx="2" />
              <path d="M16 12h.01" />
            </svg>
          </div>
          <span className="text-xs text-gray-500 uppercase tracking-wider">
            Balance
          </span>
        </div>
        <div className="flex items-baseline gap-1">
          {balanceLoading ? (
            <span className="text-2xl font-bold text-gray-500 font-mono animate-pulse">
              ...
            </span>
          ) : (
            <span className="text-2xl font-bold text-white font-mono">
              {balance.toFixed(4)}
            </span>
          )}
          <span className="text-sm text-gray-500">SOL</span>
        </div>
        <span className="text-[10px] text-gray-600 font-mono mt-1 block">
          devnet
        </span>
      </div>

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
        <div className="flex items-center gap-2 mb-3">
          <div className="w-8 h-8 rounded-lg bg-[#00d4ff]/10 flex items-center justify-center">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00d4ff" strokeWidth="2">
              <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6" />
            </svg>
          </div>
          <span className="text-xs text-gray-500 uppercase tracking-wider">
            Staked
          </span>
        </div>
        <div className="flex items-baseline gap-1">
          <span className="text-2xl font-bold text-[#00d4ff] font-mono">
            {stakedAmount.toFixed(4)}
          </span>
          <span className="text-sm text-gray-500">SOL</span>
        </div>
      </div>

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
        <div className="flex items-center gap-2 mb-3">
          <div className="w-8 h-8 rounded-lg bg-[#00ff88]/10 flex items-center justify-center">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <path d="M16 8h-6a2 2 0 100 4h4a2 2 0 010 4H8" />
              <path d="M12 18V6" />
            </svg>
          </div>
          <span className="text-xs text-gray-500 uppercase tracking-wider">
            Earned Bounties
          </span>
        </div>
        <div className="flex items-baseline gap-1">
          <span className="text-2xl font-bold text-[#00ff88] font-mono">
            {earnedBounties.toFixed(4)}
          </span>
          <span className="text-sm text-gray-500">SOL</span>
        </div>
      </div>
    </div>
  );
}
