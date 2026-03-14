"use client";

import { useState, useEffect } from "react";
import type { MeshPeer, MeshStatus as MeshStatusType } from "@/types/mesh";

const generatePeers = (): MeshPeer[] => {
  const peerIds = [
    "peer-a3f8c2", "peer-b7d1e4", "peer-c9a5f6", "peer-d2b8c3",
    "peer-e6f4a1", "peer-f1c7d9", "peer-a8e3b2", "peer-b4f9c5",
    "peer-c3d6a7", "peer-d7e2f8", "peer-e1a4b6", "peer-f5c8d3",
  ];
  const now = Date.now();
  return peerIds.map((id) => ({
    id,
    lastSeen: now - Math.floor(Math.random() * 60000),
    relayedCount: Math.floor(Math.random() * 500) + 10,
  }));
};

export default function MeshStatusComponent() {
  const [status, setStatus] = useState<MeshStatusType>({
    peers: [],
    connectedCount: 0,
    totalRelayed: 0,
  });

  useEffect(() => {
    const peers = generatePeers();
    setStatus({
      peers,
      connectedCount: peers.length,
      totalRelayed: peers.reduce((sum, p) => sum + p.relayedCount, 0),
    });

    const interval = setInterval(() => {
      setStatus((prev) => ({
        ...prev,
        totalRelayed: prev.totalRelayed + Math.floor(Math.random() * 5),
        peers: prev.peers.map((p) => ({
          ...p,
          lastSeen: Date.now() - Math.floor(Math.random() * 30000),
          relayedCount: p.relayedCount + Math.floor(Math.random() * 3),
        })),
      }));
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  const healthPercent = Math.min((status.connectedCount / 15) * 100, 100);
  const healthColor =
    healthPercent >= 70 ? "text-[#00ff88]" : healthPercent >= 40 ? "text-yellow-400" : "text-red-400";
  const healthBarColor =
    healthPercent >= 70 ? "bg-[#00ff88]" : healthPercent >= 40 ? "bg-yellow-500" : "bg-red-500";

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Connected Peers</div>
          <div className="text-3xl font-bold text-[#00d4ff] font-mono">{status.connectedCount}</div>
          <div className="text-xs text-gray-600 mt-1">active nodes</div>
        </div>
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Total Relayed</div>
          <div className="text-3xl font-bold text-[#00ff88] font-mono">{status.totalRelayed.toLocaleString()}</div>
          <div className="text-xs text-gray-600 mt-1">threat intel messages</div>
        </div>
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Mesh Health</div>
          <div className={`text-3xl font-bold font-mono ${healthColor}`}>{Math.round(healthPercent)}%</div>
          <div className="w-full h-1.5 bg-[#1a1a2e] rounded-full overflow-hidden mt-2">
            <div className={`h-full rounded-full ${healthBarColor} transition-all`} style={{ width: `${healthPercent}%` }} />
          </div>
        </div>
      </div>

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl overflow-hidden">
        <div className="px-5 py-3 border-b border-[#1a1a2e]">
          <h3 className="text-sm text-white font-semibold">Peer List</h3>
        </div>
        <div className="divide-y divide-[#1a1a2e]">
          {status.peers.map((peer) => {
            const secsAgo = Math.floor((Date.now() - peer.lastSeen) / 1000);
            const isActive = secsAgo < 30;
            return (
              <div key={peer.id} className="px-5 py-3 flex items-center justify-between hover:bg-white/[0.02] transition-colors">
                <div className="flex items-center gap-3">
                  <div className={`w-2 h-2 rounded-full ${isActive ? "bg-[#00ff88] shadow-[0_0_6px_#00ff88]" : "bg-gray-600"}`} />
                  <span className="text-sm text-gray-300 font-mono">{peer.id}</span>
                </div>
                <div className="flex items-center gap-6 text-xs text-gray-500 font-mono">
                  <span>{peer.relayedCount} relayed</span>
                  <span>{secsAgo}s ago</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
