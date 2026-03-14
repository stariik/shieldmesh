"use client";

import MeshStatusComponent from "@/components/mesh/MeshStatus";

export default function MeshPage() {
  return (
    <div className="space-y-8 animate-fade-in">
      <div>
        <h2 className="text-xl font-semibold text-white mb-2">Mesh Network</h2>
        <p className="text-sm text-gray-500">
          Real-time peer-to-peer threat intelligence relay network. Peers validate and propagate threat reports across the decentralized mesh.
        </p>
      </div>

      <MeshStatusComponent />

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-6">
        <h3 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-4">Network Activity</h3>
        <div className="grid grid-cols-12 sm:grid-cols-12 gap-0.5 sm:gap-1 h-24 sm:h-32 overflow-hidden">
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
