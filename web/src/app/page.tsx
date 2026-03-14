"use client";

import Link from "next/link";
import { useThreatStore } from "@/store/threatStore";
import { useWalletStore } from "@/store/walletStore";
import ThreatCard from "@/components/threats/ThreatCard";

export default function DashboardPage() {
  const threats = useThreatStore((s) => s.threats);
  const { connected, balance } = useWalletStore();
  const verified = threats.filter((t) => t.status === "VERIFIED").length;
  const pending = threats.filter((t) => t.status === "PENDING").length;
  const critical = threats.filter((t) => t.severity === "CRITICAL").length;

  const poolBalanceDisplay = connected
    ? `${balance.toFixed(2)} SOL`
    : "-- SOL";

  const stats = [
    {
      label: "Total Threats",
      value: threats.length,
      icon: (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#00d4ff" strokeWidth="2"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" /><line x1="12" y1="9" x2="12" y2="13" /><line x1="12" y1="17" x2="12.01" y2="17" /></svg>
      ),
      color: "text-[#00d4ff]",
      bgColor: "bg-[#00d4ff]/10",
    },
    {
      label: "Verified",
      value: verified,
      icon: (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2"><path d="M22 11.08V12a10 10 0 11-5.93-9.14" /><path d="M22 4L12 14.01l-3-3" /></svg>
      ),
      color: "text-[#00ff88]",
      bgColor: "bg-[#00ff88]/10",
    },
    {
      label: "Your Balance",
      value: poolBalanceDisplay,
      icon: (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#00d4ff" strokeWidth="2"><circle cx="12" cy="12" r="10" /><path d="M16 8h-6a2 2 0 100 4h4a2 2 0 010 4H8" /><path d="M12 18V6" /></svg>
      ),
      color: connected ? "text-[#00d4ff]" : "text-gray-500",
      bgColor: "bg-[#00d4ff]/10",
    },
    {
      label: "Mesh Peers",
      value: 12,
      icon: (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2"><circle cx="12" cy="5" r="3" /><circle cx="5" cy="19" r="3" /><circle cx="19" cy="19" r="3" /><line x1="12" y1="8" x2="5" y2="16" /><line x1="12" y1="8" x2="19" y2="16" /></svg>
      ),
      color: "text-[#00ff88]",
      bgColor: "bg-[#00ff88]/10",
    },
  ];

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <div
            key={stat.label}
            className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5 hover:border-[#1a1a3e] transition-all"
          >
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs text-gray-500 uppercase tracking-wider">{stat.label}</span>
              <div className={`w-8 h-8 rounded-lg ${stat.bgColor} flex items-center justify-center`}>
                {stat.icon}
              </div>
            </div>
            <div className={`text-2xl font-bold font-mono ${stat.color}`}>{stat.value}</div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2 bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-4 gap-2">
            <span className="text-sm text-gray-400 uppercase tracking-wider font-mono">Threat Overview</span>
            <div className="flex items-center gap-3 sm:gap-4 text-xs font-mono">
              <span className="text-red-400">{critical} critical</span>
              <span className="text-yellow-400">{pending} pending</span>
              <span className="text-cyan-400">{verified} verified</span>
            </div>
          </div>
          <div className="grid grid-cols-5 gap-1 h-24">
            {threats.map((t) => {
              const barColor =
                t.severity === "CRITICAL" ? "bg-red-500" :
                t.severity === "HIGH" ? "bg-orange-500" :
                t.severity === "MEDIUM" ? "bg-yellow-500" : "bg-green-500";
              return (
                <div key={t.id} className="flex flex-col items-center justify-end gap-1">
                  <div
                    className={`w-full rounded-t ${barColor} transition-all`}
                    style={{ height: `${t.aiScore}%` }}
                  />
                  <span className="text-[8px] text-gray-600 font-mono">{t.aiScore}</span>
                </div>
              );
            })}
          </div>
        </div>

        <div className="flex flex-col gap-4">
          <Link
            href="/scan"
            className="flex-1 bg-gradient-to-br from-[#00ff88]/10 to-[#00d4ff]/10 border border-[#00ff88]/20 rounded-xl p-5 flex items-center gap-4 hover:border-[#00ff88]/40 transition-all group"
          >
            <div className="w-12 h-12 rounded-xl bg-[#00ff88]/15 flex items-center justify-center shrink-0 group-hover:animate-glow-pulse">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#00ff88" strokeWidth="2"><circle cx="11" cy="11" r="8" /><path d="M21 21l-4.35-4.35" /></svg>
            </div>
            <div>
              <h3 className="text-white font-semibold text-sm">Quick Scan</h3>
              <p className="text-xs text-gray-500">Analyze a URL or message for threats</p>
            </div>
          </Link>

          <a
            href="/ShieldMesh.apk"
            download
            className="flex-1 bg-gradient-to-br from-[#00d4ff]/10 to-[#9945FF]/10 border border-[#00d4ff]/20 rounded-xl p-5 flex items-center gap-4 hover:border-[#00d4ff]/40 transition-all group"
          >
            <div className="w-12 h-12 rounded-xl bg-[#00d4ff]/15 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#00d4ff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
            </div>
            <div>
              <h3 className="text-white font-semibold text-sm">Download Mobile App</h3>
              <p className="text-xs text-gray-500">Get ShieldMesh on Android</p>
            </div>
          </a>
        </div>
      </div>

      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-sm text-gray-400 uppercase tracking-wider font-mono">Recent Threats</h2>
          <Link href="/threats" className="text-xs text-[#00ff88] hover:text-[#00ff88]/80 font-mono transition-colors">
            View All &rarr;
          </Link>
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          {threats.slice(0, 4).map((threat) => (
            <ThreatCard key={threat.id} threat={threat} />
          ))}
        </div>
      </div>
    </div>
  );
}
