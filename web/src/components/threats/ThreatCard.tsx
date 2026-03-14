"use client";

import type { Threat } from "@/types/threat";
import { SEVERITY_BG, STATUS_COLORS } from "@/lib/constants";

function timeAgo(timestamp: number): string {
  const seconds = Math.floor((Date.now() - timestamp) / 1000);
  if (seconds < 60) return `${seconds}s ago`;
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

export default function ThreatCard({ threat }: { threat: Threat }) {
  const scoreColor =
    threat.aiScore >= 80
      ? "text-red-400"
      : threat.aiScore >= 50
      ? "text-orange-400"
      : "text-green-400";

  const scoreBarColor =
    threat.aiScore >= 80
      ? "bg-red-500"
      : threat.aiScore >= 50
      ? "bg-orange-500"
      : "bg-green-500";

  return (
    <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5 hover:border-[#00ff88]/20 transition-all duration-300 group">
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider border ${SEVERITY_BG[threat.severity]}`}>
            {threat.severity}
          </span>
          <span className={`px-2 py-0.5 rounded text-[10px] font-medium uppercase tracking-wider border ${STATUS_COLORS[threat.status]}`}>
            {threat.status}
          </span>
        </div>
        <span className="text-xs text-gray-500 font-mono">{timeAgo(threat.timestamp)}</span>
      </div>

      <p className="text-sm text-gray-300 mb-4 leading-relaxed line-clamp-2">
        {threat.description}
      </p>

      <div className="flex items-center gap-4 mb-3">
        <div className="flex-1">
          <div className="flex items-center justify-between mb-1">
            <span className="text-[10px] text-gray-500 uppercase tracking-wider">AI Score</span>
            <span className={`text-sm font-mono font-bold ${scoreColor}`}>{threat.aiScore}</span>
          </div>
          <div className="w-full h-1.5 bg-[#1a1a2e] rounded-full overflow-hidden">
            <div
              className={`h-full rounded-full ${scoreBarColor} transition-all duration-500`}
              style={{ width: `${threat.aiScore}%` }}
            />
          </div>
        </div>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-2 text-xs text-gray-500 pt-3 border-t border-[#1a1a2e]">
        <div className="flex items-center gap-3">
          <span className="font-mono flex items-center gap-1">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" /><circle cx="12" cy="7" r="4" /></svg>
            {threat.validatorCount} validators
          </span>
          <span className="font-mono">#{threat.id}</span>
        </div>
        <span className="font-mono text-[10px] text-gray-600 truncate max-w-[120px] sm:max-w-[180px]" title={threat.hash}>
          {threat.hash.slice(0, 8)}...{threat.hash.slice(-8)}
        </span>
      </div>
    </div>
  );
}
