"use client";

import Balance from "@/components/wallet/Balance";
import { useWalletStore } from "@/store/walletStore";

const mockTransactions = [
  { id: "tx-1", type: "Bounty Earned", amount: "+0.25", time: "2h ago", status: "confirmed" },
  { id: "tx-2", type: "Stake Deposit", amount: "-0.50", time: "5h ago", status: "confirmed" },
  { id: "tx-3", type: "Bounty Earned", amount: "+0.10", time: "1d ago", status: "confirmed" },
  { id: "tx-4", type: "Stake Deposit", amount: "-1.00", time: "2d ago", status: "confirmed" },
  { id: "tx-5", type: "Bounty Earned", amount: "+0.05", time: "3d ago", status: "confirmed" },
  { id: "tx-6", type: "Unstake", amount: "+0.50", time: "5d ago", status: "confirmed" },
  { id: "tx-7", type: "Bounty Earned", amount: "+0.01", time: "7d ago", status: "confirmed" },
];

export default function WalletPage() {
  const { connected, publicKey } = useWalletStore();

  return (
    <div className="space-y-8 animate-fade-in max-w-4xl">
      <Balance />

      {connected && publicKey && (
        <>
          <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
            <h3 className="text-xs text-gray-500 uppercase tracking-wider mb-2">
              Wallet Address
            </h3>
            <div className="flex items-center gap-3">
              <code className="text-sm text-[#00d4ff] font-mono bg-[#0a0a0f] px-4 py-2 rounded-lg border border-[#1a1a2e] flex-1 break-all">
                {publicKey}
              </code>
              <button
                onClick={() => navigator.clipboard.writeText(publicKey)}
                className="px-3 py-2 rounded-lg text-xs text-gray-400 border border-[#1a1a2e] hover:text-[#00ff88] hover:border-[#00ff88]/30 transition-all"
              >
                Copy
              </button>
            </div>
            <a
              href={`https://explorer.solana.com/address/${publicKey}?cluster=devnet`}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block mt-3 text-xs text-[#00d4ff]/60 hover:text-[#00d4ff] font-mono transition-colors"
            >
              View on Solana Explorer
            </a>
          </div>

          <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl overflow-hidden">
            <div className="px-5 py-3 border-b border-[#1a1a2e]">
              <h3 className="text-sm text-white font-semibold">
                Transaction History
              </h3>
            </div>
            <div className="divide-y divide-[#1a1a2e]">
              {mockTransactions.map((tx) => (
                <div
                  key={tx.id}
                  className="px-5 py-3.5 flex items-center justify-between hover:bg-white/[0.02] transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                        tx.amount.startsWith("+")
                          ? "bg-[#00ff88]/10"
                          : "bg-red-500/10"
                      }`}
                    >
                      {tx.amount.startsWith("+") ? (
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2.5">
                          <path d="M12 19V5M5 12l7-7 7 7" />
                        </svg>
                      ) : (
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#ef4444" strokeWidth="2.5">
                          <path d="M12 5v14M19 12l-7 7-7-7" />
                        </svg>
                      )}
                    </div>
                    <div>
                      <div className="text-sm text-white">{tx.type}</div>
                      <div className="text-[10px] text-gray-600 font-mono">
                        {tx.time}
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div
                      className={`text-sm font-mono font-semibold ${
                        tx.amount.startsWith("+")
                          ? "text-[#00ff88]"
                          : "text-red-400"
                      }`}
                    >
                      {tx.amount} SOL
                    </div>
                    <div className="text-[10px] text-gray-600">
                      {tx.status}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </>
      )}

      {!connected && (
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-12 text-center">
          <div className="w-20 h-20 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-[#00ff88]/10 to-[#00d4ff]/10 flex items-center justify-center border border-[#00ff88]/20">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="1.5">
              <rect x="2" y="4" width="20" height="16" rx="2" />
              <path d="M16 12h.01" />
              <path d="M2 10h20" />
            </svg>
          </div>
          <h3 className="text-xl text-white font-semibold mb-2">
            Connect Your Wallet
          </h3>
          <p className="text-sm text-gray-500 mb-6 max-w-md mx-auto">
            Connect your Phantom wallet to view your balance, transaction
            history, staking details, and earned bounties.
          </p>
          <p className="text-xs text-gray-600">
            Use the &quot;Select Wallet&quot; button in the top-right corner.
          </p>
        </div>
      )}
    </div>
  );
}
