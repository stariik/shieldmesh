"use client";

import { usePathname } from "next/navigation";
import WalletConnect from "@/components/wallet/WalletConnect";
import StatusBanner from "./StatusBanner";
import { useSidebarStore } from "@/store/sidebarStore";

const pageTitles: Record<string, string> = {
  "/dashboard": "Dashboard",
  "/scan": "Threat Scanner",
  "/threats": "Threat Feed",
  "/bounties": "Bounties",
  "/wallet": "Wallet",
  "/mesh": "Mesh Network",
};

export default function Header() {
  const pathname = usePathname();
  const title = pageTitles[pathname] || "ShieldMesh";
  const toggle = useSidebarStore((s) => s.toggle);

  return (
    <header className="fixed top-0 left-0 lg:left-64 right-0 h-14 lg:h-16 bg-[#0d0d14]/80 backdrop-blur-xl border-b border-[#1a1a2e] flex items-center justify-between px-4 lg:px-8 z-30">
      <div className="flex items-center gap-3">
        {/* Hamburger menu - mobile only */}
        <button
          onClick={toggle}
          className="lg:hidden p-2 -ml-1 rounded-lg text-gray-400 hover:text-white hover:bg-white/5 transition-colors"
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <line x1="3" y1="6" x2="21" y2="6" />
            <line x1="3" y1="12" x2="21" y2="12" />
            <line x1="3" y1="18" x2="21" y2="18" />
          </svg>
        </button>
        <h1 className="text-base lg:text-lg font-semibold text-white">{title}</h1>
        <div className="hidden sm:block">
          <StatusBanner />
        </div>
      </div>
      <div className="flex items-center gap-3 lg:gap-4">
        <div className="text-xs text-gray-500 font-mono hidden md:block">
          {new Date().toLocaleTimeString("en-US", { hour12: false })}
        </div>
        <WalletConnect />
      </div>
    </header>
  );
}
