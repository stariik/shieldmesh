"use client";

import { usePathname } from "next/navigation";
import WalletConnect from "@/components/wallet/WalletConnect";
import StatusBanner from "./StatusBanner";

const pageTitles: Record<string, string> = {
  "/": "Dashboard",
  "/scan": "Threat Scanner",
  "/threats": "Threat Feed",
  "/bounties": "Bounties",
  "/wallet": "Wallet",
  "/mesh": "Mesh Network",
};

export default function Header() {
  const pathname = usePathname();
  const title = pageTitles[pathname] || "ShieldMesh";

  return (
    <header className="fixed top-0 left-64 right-0 h-16 bg-[#0d0d14]/80 backdrop-blur-xl border-b border-[#1a1a2e] flex items-center justify-between px-8 z-40">
      <div className="flex items-center gap-4">
        <h1 className="text-lg font-semibold text-white">{title}</h1>
        <StatusBanner />
      </div>
      <div className="flex items-center gap-4">
        <div className="text-xs text-gray-500 font-mono hidden md:block">
          {new Date().toLocaleTimeString("en-US", { hour12: false })}
        </div>
        <WalletConnect />
      </div>
    </header>
  );
}
