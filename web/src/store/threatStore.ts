import { create } from "zustand";
import type { Threat, ThreatStatus } from "@/types/threat";
import { sha256Hex } from "@/lib/ai-scanner";

interface ThreatState {
  threats: Threat[];
  pendingOfflineCount: number;
  addThreat: (threat: Omit<Threat, "id" | "hash" | "validatorCount" | "status" | "timestamp"> & {
    id?: string;
    hash?: string;
    validatorCount?: number;
    status?: ThreatStatus;
    timestamp?: number;
  }) => void;
  updateThreat: (id: string, updates: Partial<Threat>) => void;
  getByStatus: (status: ThreatStatus) => Threat[];
  syncPending: () => void;
}

const now = Date.now();

const mockThreats: Threat[] = [
  {
    id: "t-001",
    hash: "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
    severity: "CRITICAL",
    aiScore: 97,
    reporterPubkey: "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
    validatorCount: 12,
    status: "VERIFIED",
    timestamp: now - 1800000,
    description:
      "Zero-day exploit detected in DeFi lending protocol. Reentrancy vulnerability allows unauthorized fund extraction via flash loan attack vector.",
    url: "https://defi-protocol.xyz/swap",
  },
  {
    id: "t-002",
    hash: "a1f3c2e8b9d4567890abcdef1234567890abcdef1234567890abcdef12345678",
    severity: "HIGH",
    aiScore: 82,
    reporterPubkey: "4zMMC9srt5Ri5X14GAgXhaHii3GnPAEERYPJgZJDncDU",
    validatorCount: 8,
    status: "VERIFIED",
    timestamp: now - 7200000,
    description:
      "Phishing campaign targeting Solana wallet users via fake airdrop claims. Malicious smart contract drains SPL tokens upon approval.",
    url: "https://sol-airdrop-claim.fake/connect",
  },
  {
    id: "t-003",
    hash: "b2e4d3f7c8a5678901bcdef2345678901bcdef2345678901bcdef234567890ab",
    severity: "MEDIUM",
    aiScore: 65,
    reporterPubkey: "9noXzpXnkyEcKF3DnnqrkHRBfGt7BbJYzm1EWKPN1Kmz",
    validatorCount: 4,
    status: "PENDING",
    timestamp: now - 14400000,
    description:
      "Suspicious NFT marketplace contract with unverified upgrade authority. Potential rug-pull vector identified in mint function.",
    url: "https://nft-market-sol.io/mint",
  },
  {
    id: "t-004",
    hash: "c3f5e4a8d9b6789012cdef3456789012cdef3456789012cdef3456789012bcde",
    severity: "LOW",
    aiScore: 34,
    reporterPubkey: "2WGcYYau2gLu7a3qrKRjM5F4x4ySBa1PgbRwynLTtMQ7",
    validatorCount: 2,
    status: "SETTLED",
    timestamp: now - 86400000,
    description:
      "Minor metadata inconsistency in token program. Token symbol spoofing attempt detected with similar unicode characters.",
    url: "https://token-registry.sol/verify",
  },
  {
    id: "t-005",
    hash: "d4a6f5b9e0c7890123def4567890123def4567890123def4567890123cdef45",
    severity: "CRITICAL",
    aiScore: 94,
    reporterPubkey: "5HwVDvBgxLj9x3d1K5RxJNDcyWZh7tMGq6w4HbSmFEKN",
    validatorCount: 15,
    status: "PENDING",
    timestamp: now - 600000,
    description:
      "Active bridge exploit in progress. Cross-chain message validation bypass allows minting of unbacked wrapped assets.",
    url: "https://sol-bridge.network/transfer",
  },
];

export const useThreatStore = create<ThreatState>((set, get) => ({
  threats: mockThreats,
  pendingOfflineCount: 0,

  addThreat: (partial) => {
    const ts = partial.timestamp ?? Date.now();
    const id = partial.id ?? `t-${ts.toString(36)}-${Math.random().toString(36).slice(2, 6)}`;
    const hash = partial.hash ?? sha256Hex(partial.url + partial.description + ts.toString());

    const threat: Threat = {
      id,
      hash,
      severity: partial.severity,
      aiScore: partial.aiScore,
      reporterPubkey: partial.reporterPubkey,
      validatorCount: partial.validatorCount ?? 0,
      status: partial.status ?? "PENDING",
      timestamp: ts,
      description: partial.description,
      url: partial.url,
    };

    set((state) => ({
      threats: [threat, ...state.threats],
      pendingOfflineCount: threat.status === "PENDING"
        ? state.pendingOfflineCount + 1
        : state.pendingOfflineCount,
    }));
  },

  updateThreat: (id: string, updates: Partial<Threat>) =>
    set((state) => ({
      threats: state.threats.map((t) =>
        t.id === id ? { ...t, ...updates } : t,
      ),
    })),

  getByStatus: (status: ThreatStatus) =>
    get().threats.filter((t) => t.status === status),

  syncPending: () => {
    if (typeof navigator !== "undefined" && !navigator.onLine) return;
    const pending = get().threats.filter((t) => t.status === "PENDING" && t.validatorCount === 0);
    if (pending.length === 0) return;

    // Simulate syncing: mark threats as VERIFIED after brief delay
    pending.forEach((t, i) => {
      setTimeout(() => {
        set((state) => ({
          threats: state.threats.map((th) =>
            th.id === t.id ? { ...th, status: "VERIFIED" as ThreatStatus, validatorCount: Math.floor(Math.random() * 8) + 3 } : th
          ),
          pendingOfflineCount: Math.max(0, state.pendingOfflineCount - 1),
        }));
      }, (i + 1) * 800);
    });
  },
}));
