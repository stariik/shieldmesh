"use client";

import { useWalletSync } from "@/hooks/useWalletSync";

/**
 * Invisible component that syncs real wallet adapter state into zustand store.
 * Must be rendered inside WalletProvider.
 */
export default function WalletSyncProvider() {
  useWalletSync();
  return null;
}
