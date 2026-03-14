"use client";

import type { ReactNode } from "react";
import WalletProvider from "@/providers/WalletProvider";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import WalletSyncProvider from "@/providers/WalletSyncProvider";

export default function ClientShell({ children }: { children: ReactNode }) {
  return (
    <WalletProvider>
      <WalletSyncProvider />
      <Sidebar />
      <Header />
      <main className="ml-64 mt-16 min-h-[calc(100vh-4rem)] p-8">
        {children}
      </main>
    </WalletProvider>
  );
}
