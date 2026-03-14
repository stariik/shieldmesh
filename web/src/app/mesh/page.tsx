"use client";

import MeshStatusComponent from "@/components/mesh/MeshStatus";

const relaySteps = [
  { label: "Device A", sub: "Detects threat offline", icon: "M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" },
  { label: "BLE Relay", sub: "Broadcast via mesh", icon: "M8.111 16.404a5.5 5.5 0 017.778 0M12 20h.01M2 8.82a15 15 0 0120 0M5.636 12.025a9 9 0 0112.728 0" },
  { label: "Device B", sub: "Receives & forwards", icon: "M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" },
  { label: "Internet", sub: "First connected peer", icon: "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" },
  { label: "Solana", sub: "On-chain settlement", icon: "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" },
];

export default function MeshPage() {
  return (
    <div className="space-y-8 animate-fade-in">
      <div>
        <h2 className="text-xl font-semibold text-white mb-2">Mesh Network</h2>
        <p className="text-sm text-gray-500">
          Real-time peer-to-peer threat intelligence relay powered by Pollinet SDK. Devices share threat data via BLE/Wi-Fi Direct mesh even without internet connectivity.
        </p>
      </div>

      {/* Powered by Pollinet banner */}
      <div className="bg-gradient-to-r from-[#9945FF]/10 to-[#00d4ff]/10 border border-[#9945FF]/20 rounded-xl p-5 sm:p-6">
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-[#9945FF]/20 flex items-center justify-center shrink-0 border border-[#9945FF]/30">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#9945FF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M8.111 16.404a5.5 5.5 0 017.778 0" />
              <path d="M12 20h.01" />
              <path d="M2 8.82a15 15 0 0120 0" />
              <path d="M5.636 12.025a9 9 0 0112.728 0" />
            </svg>
          </div>
          <div className="flex-1">
            <h3 className="text-white font-semibold text-sm">Powered by Pollinet SDK</h3>
            <p className="text-xs text-gray-400 mt-1 leading-relaxed">
              Pollinet enables offline-first communication between nearby devices via BLE and Wi-Fi Direct. Threat reports, votes, and Solana transactions are queued locally, relayed through the mesh, and submitted to the blockchain when any device in the mesh regains internet access.
            </p>
          </div>
          <div className="flex gap-3 shrink-0">
            <div className="text-center">
              <div className="text-lg font-bold text-[#9945FF] font-mono">BLE</div>
              <div className="text-[9px] text-gray-500 uppercase">Primary</div>
            </div>
            <div className="text-center">
              <div className="text-lg font-bold text-[#00d4ff] font-mono">P2P</div>
              <div className="text-[9px] text-gray-500 uppercase">Wi-Fi Direct</div>
            </div>
          </div>
        </div>
      </div>

      {/* Relay Path Visualization */}
      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5 sm:p-6">
        <h3 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-6">Offline Relay Path</h3>
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-2 sm:gap-0">
          {relaySteps.map((step, i) => (
            <div key={step.label} className="flex flex-row sm:flex-col items-center sm:flex-1 gap-3 sm:gap-0">
              <div className="flex flex-col items-center">
                <div className={`w-11 h-11 rounded-xl flex items-center justify-center border ${
                  i === 4 ? "bg-[#9945FF]/15 border-[#9945FF]/30" :
                  i === 0 || i === 2 ? "bg-[#00ff88]/10 border-[#00ff88]/20" :
                  "bg-[#00d4ff]/10 border-[#00d4ff]/20"
                }`}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={
                    i === 4 ? "#9945FF" : i === 0 || i === 2 ? "#00ff88" : "#00d4ff"
                  } strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                    <path d={step.icon} />
                  </svg>
                </div>
              </div>
              <div className="sm:text-center sm:mt-2">
                <div className="text-xs text-white font-medium">{step.label}</div>
                <div className="text-[10px] text-gray-500">{step.sub}</div>
              </div>
              {i < relaySteps.length - 1 && (
                <div className="hidden sm:block absolute" style={{ display: "none" }} />
              )}
            </div>
          ))}
        </div>
        {/* Arrows between steps - desktop only */}
        <div className="hidden sm:flex items-center justify-between px-12 -mt-[52px] mb-4 pointer-events-none">
          {[0, 1, 2, 3].map((i) => (
            <svg key={i} width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#333" strokeWidth="1.5" className="mx-auto">
              <path d="M5 12h14M13 5l7 7-7 7" />
            </svg>
          ))}
        </div>
      </div>

      <MeshStatusComponent />

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5 sm:p-6">
        <h3 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-4">Network Activity</h3>
        <div className="grid grid-cols-12 gap-0.5 sm:gap-1 h-24 sm:h-32 overflow-hidden">
          {Array.from({ length: 48 }).map((_, i) => {
            const height = Math.random() * 80 + 20;
            const isRecent = i > 40;
            return (
              <div key={i} className="flex items-end">
                <div
                  className={`w-full rounded-t transition-all ${
                    isRecent ? "bg-[#00ff88]/60" : "bg-[#00ff88]/20"
                  }`}
                  style={{ height: `${height}%` }}
                />
              </div>
            );
          })}
        </div>
        <div className="flex justify-between mt-2 text-[10px] text-gray-600 font-mono">
          <span>24h ago</span>
          <span>12h ago</span>
          <span>Now</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <h3 className="text-sm text-white font-semibold mb-3">Protocol Stats</h3>
          <div className="space-y-3">
            {[
              { label: "Avg Propagation Time", value: "1.2s" },
              { label: "Network Uptime", value: "99.7%" },
              { label: "Total Validators", value: "47" },
              { label: "Consensus Threshold", value: "66%" },
              { label: "Offline Relay Success Rate", value: "94.3%" },
              { label: "Avg Mesh Hops to Settlement", value: "2.1" },
            ].map((stat) => (
              <div key={stat.label} className="flex justify-between items-center py-1.5 border-b border-[#1a1a2e] last:border-0">
                <span className="text-xs text-gray-500">{stat.label}</span>
                <span className="text-sm text-[#00d4ff] font-mono">{stat.value}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <h3 className="text-sm text-white font-semibold mb-3">Your Node</h3>
          <div className="space-y-3">
            {[
              { label: "Status", value: "Active" },
              { label: "Relayed", value: "142 msgs" },
              { label: "Reputation Score", value: "94/100" },
              { label: "Peer Connections", value: "8" },
              { label: "Offline Queue", value: "0 pending" },
              { label: "Mesh Relay Mode", value: "BLE + Wi-Fi" },
            ].map((stat) => (
              <div key={stat.label} className="flex justify-between items-center py-1.5 border-b border-[#1a1a2e] last:border-0">
                <span className="text-xs text-gray-500">{stat.label}</span>
                <span className="text-sm text-[#00ff88] font-mono">{stat.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
