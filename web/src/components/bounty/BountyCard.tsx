"use client";

import type { Bounty } from "@/types/bounty";
import { SEVERITY_BG } from "@/lib/constants";

function timeAgo(timestamp: number): string {
  const seconds = Math.floor((Date.now() - timestamp) / 1000);
  if (seconds < 60) return `${seconds}s ago`;
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  return `${Math.floor(hours / 24)}d ago`;
}

export default function BountyCard({ bounty }: { bounty: Bounty }) {
  return (
    <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5 hover:border-[#00d4ff]/20 transition-all duration-300">
      <div className="flex items-start justify-between mb-3">
        <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider border ${SEVERITY_BG[bounty.severity]}`}>
          {bounty.severity}
        </span>
        <span className="text-xs text-gray-500 font-mono">{timeAgo(bounty.timestamp)}</span>
      </div>

      <div className="flex items-baseline gap-1 mb-1">
        <span className="text-2xl font-bold text-[#00d4ff] font-mono">{bounty.amount.toFixed(2)}</span>
        <span className="text-sm text-gray-500">SOL</span>
      </div>

      <div className="text-xs text-gray-500 font-mono mb-4">
        Threat: {bounty.threatId} / Reporter: {bounty.reporter.slice(0, 6)}...{bounty.reporter.slice(-4)}
      </div>

      <button
        disabled={bounty.claimed}
        className={`w-full py-2.5 rounded-lg text-sm font-semibold transition-all ${
          bounty.claimed
            ? "bg-white/5 text-gray-600 cursor-default border border-[#1a1a2e]"
            : "bg-gradient-to-r from-[#00d4ff] to-[#00ff88] text-[#0a0a0f] hover:opacity-90 shadow-[0_0_20px_rgba(0,212,255,0.15)]"
        }`}
      >
        {bounty.claimed ? "Claimed" : "Claim Bounty"}
      </button>
    </div>
  );
}
