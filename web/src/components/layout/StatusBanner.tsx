"use client";

import { useState, useEffect } from "react";

type SyncState = "offline" | "syncing" | "synced";

const stateConfig: Record<SyncState, { color: string; glow: string; label: string }> = {
  offline: { color: "bg-red-500", glow: "shadow-[0_0_6px_#ef4444]", label: "Offline" },
  syncing: { color: "bg-yellow-500", glow: "shadow-[0_0_6px_#eab308]", label: "Syncing" },
  synced: { color: "bg-[#00ff88]", glow: "shadow-[0_0_6px_#00ff88]", label: "Synced" },
};

export default function StatusBanner() {
  const [state, setState] = useState<SyncState>("syncing");

  useEffect(() => {
    const timer = setTimeout(() => setState("synced"), 2000);
    return () => clearTimeout(timer);
  }, []);

  const config = stateConfig[state];

  return (
    <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/5">
      <div className={`w-2 h-2 rounded-full ${config.color} ${config.glow} ${state === "syncing" ? "animate-pulse" : ""}`} />
      <span className="text-xs text-gray-400">{config.label}</span>
    </div>
  );
}
