"use client";

import { useState, useEffect } from "react";
import { useOnlineStatus } from "@/hooks/useOnlineStatus";

export default function StatusBanner() {
  const isOnline = useOnlineStatus();
  const [justReconnected, setJustReconnected] = useState(false);

  useEffect(() => {
    if (isOnline) {
      setJustReconnected(true);
      const timer = setTimeout(() => setJustReconnected(false), 2000);
      return () => clearTimeout(timer);
    }
  }, [isOnline]);

  const state = !isOnline
    ? "offline"
    : justReconnected
    ? "syncing"
    : "synced";

  const config = {
    offline: { color: "bg-red-500", glow: "shadow-[0_0_6px_#ef4444]", label: "Offline" },
    syncing: { color: "bg-yellow-500", glow: "shadow-[0_0_6px_#eab308]", label: "Syncing" },
    synced: { color: "bg-[#00ff88]", glow: "shadow-[0_0_6px_#00ff88]", label: "Synced" },
  }[state];

  return (
    <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/5">
      <div className={`w-2 h-2 rounded-full ${config.color} ${config.glow} ${state === "syncing" ? "animate-pulse" : ""}`} />
      <span className="text-xs text-gray-400">{config.label}</span>
    </div>
  );
}
