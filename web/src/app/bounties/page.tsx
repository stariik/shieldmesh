"use client";

import { useEffect, useState } from "react";
import BountyCard from "@/components/bounty/BountyCard";
import StakePanel from "@/components/bounty/StakePanel";
import { useShieldMesh } from "@/hooks/useShieldMesh";
import type { Bounty } from "@/types/bounty";
import type { OnChainBounty, OnChainThreat } from "@/hooks/useShieldMesh";
import type { Severity } from "@/types/threat";

const now = Date.now();

const SEVERITY_FROM_U8: Record<number, Severity> = {
  0: "LOW",
  1: "MEDIUM",
  2: "HIGH",
  3: "CRITICAL",
};

const mockBounties: Bounty[] = [
  { id: "b-001", threatId: "t-001", reporter: "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU", amount: 0.25, claimed: false, timestamp: now - 3600000, severity: "CRITICAL" },
  { id: "b-002", threatId: "t-002", reporter: "4zMMC9srt5Ri5X14GAgXhaHii3GnPAEERYPJgZJDncDU", amount: 0.10, claimed: true, timestamp: now - 14400000, severity: "HIGH" },
  { id: "b-003", threatId: "t-003", reporter: "9noXzpXnkyEcKF3DnnqrkHRBfGt7BbJYzm1EWKPN1Kmz", amount: 0.05, claimed: false, timestamp: now - 28800000, severity: "MEDIUM" },
  { id: "b-004", threatId: "t-004", reporter: "2WGcYYau2gLu7a3qrKRjM5F4x4ySBa1PgbRwynLTtMQ7", amount: 0.01, claimed: true, timestamp: now - 86400000, severity: "LOW" },
  { id: "b-005", threatId: "t-005", reporter: "5HwVDvBgxLj9x3d1K5RxJNDcyWZh7tMGq6w4HbSmFEKN", amount: 0.25, claimed: false, timestamp: now - 1200000, severity: "CRITICAL" },
  { id: "b-006", threatId: "t-001", reporter: "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU", amount: 0.10, claimed: false, timestamp: now - 7200000, severity: "HIGH" },
];

/** Map on-chain bounty + threat data into the Bounty type used by BountyCard */
function mapOnChainBounties(
  bounties: OnChainBounty[],
  threats: OnChainThreat[],
): Bounty[] {
  const threatMap = new Map(threats.map((t) => [t.pubkey, t]));

  return bounties.map((b, i) => {
    const threat = threatMap.get(b.threat);
    return {
      id: b.pubkey.slice(0, 8),
      threatId: b.threat.slice(0, 8),
      reporter: b.reporter,
      amount: b.amount,
      claimed: b.claimed,
      timestamp: threat?.timestamp ?? Date.now(),
      severity: threat?.severity ?? "LOW",
    };
  });
}

export default function BountiesPage() {
  const { connected, fetchBounties, fetchThreats, fetchPool, fetchStaker } = useShieldMesh();
  const [bounties, setBounties] = useState<Bounty[]>(mockBounties);
  const [poolTotalStaked, setPoolTotalStaked] = useState<number | null>(null);
  const [stakerReputation, setStakerReputation] = useState<number | null>(null);
  const [dataSource, setDataSource] = useState<"mock" | "chain">("mock");

  useEffect(() => {
    if (!connected) {
      setBounties(mockBounties);
      setDataSource("mock");
      setPoolTotalStaked(null);
      setStakerReputation(null);
      return;
    }

    let cancelled = false;

    async function loadOnChainData() {
      try {
        const [chainBounties, chainThreats, pool, staker] = await Promise.all([
          fetchBounties(),
          fetchThreats(),
          fetchPool(),
          fetchStaker(),
        ]);

        if (cancelled) return;

        if (chainBounties.length > 0) {
          setBounties(mapOnChainBounties(chainBounties, chainThreats));
          setDataSource("chain");
        } else {
          // No on-chain bounties yet; show mock as fallback
          setBounties(mockBounties);
          setDataSource("mock");
        }

        if (pool) setPoolTotalStaked(pool.totalStaked);
        if (staker) setStakerReputation(staker.reputation);
      } catch {
        // Fall back to mock data on error
        if (!cancelled) {
          setBounties(mockBounties);
          setDataSource("mock");
        }
      }
    }

    loadOnChainData();
    return () => { cancelled = true; };
  }, [connected, fetchBounties, fetchThreats, fetchPool, fetchStaker]);

  const totalPool = bounties.reduce((sum, b) => sum + b.amount, 0);
  const claimedCount = bounties.filter((b) => b.claimed).length;
  const unclaimedCount = bounties.filter((b) => !b.claimed).length;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Data source indicator */}
      {connected && (
        <div className="flex items-center gap-2">
          <div
            className={`w-2 h-2 rounded-full ${
              dataSource === "chain" ? "bg-green-400" : "bg-yellow-400"
            }`}
          />
          <span className="text-[10px] text-gray-500 uppercase tracking-wider font-mono">
            {dataSource === "chain" ? "Live on-chain data" : "Demo data (no on-chain bounties found)"}
          </span>
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Total Bounty Pool</div>
          <div className="text-2xl font-bold text-[#00d4ff] font-mono">
            {poolTotalStaked !== null ? poolTotalStaked.toFixed(2) : totalPool.toFixed(2)} SOL
          </div>
        </div>
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Unclaimed</div>
          <div className="text-2xl font-bold text-[#00ff88] font-mono">{unclaimedCount}</div>
        </div>
        <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-5">
          <div className="text-[10px] text-gray-500 uppercase tracking-wider mb-2">Claimed</div>
          <div className="text-2xl font-bold text-gray-400 font-mono">{claimedCount}</div>
          {stakerReputation !== null && (
            <div className="text-[10px] text-[#00d4ff] mt-1 font-mono">Rep: {stakerReputation}</div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          <h2 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-4">Available Bounties</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {bounties.map((bounty) => (
              <BountyCard key={bounty.id} bounty={bounty} />
            ))}
          </div>
        </div>
        <div>
          <h2 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-4">Staking</h2>
          <StakePanel />
        </div>
      </div>
    </div>
  );
}
