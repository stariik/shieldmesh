"use client";

import { useState, useMemo } from "react";
import { useThreatStore } from "@/store/threatStore";
import ThreatCard from "@/components/threats/ThreatCard";
import type { Severity, ThreatStatus } from "@/types/threat";

type SortKey = "time" | "severity" | "score";
const severityOrder: Record<Severity, number> = {
  LOW: 0,
  MEDIUM: 1,
  HIGH: 2,
  CRITICAL: 3,
};

export default function ThreatsPage() {
  const threats = useThreatStore((s) => s.threats);
  const [statusFilter, setStatusFilter] = useState<ThreatStatus | "ALL">("ALL");
  const [severityFilter, setSeverityFilter] = useState<Severity | "ALL">("ALL");
  const [sortBy, setSortBy] = useState<SortKey>("time");

  const filtered = useMemo(() => {
    let result = [...threats];
    if (statusFilter !== "ALL") result = result.filter((t) => t.status === statusFilter);
    if (severityFilter !== "ALL") result = result.filter((t) => t.severity === severityFilter);

    result.sort((a, b) => {
      if (sortBy === "time") return b.timestamp - a.timestamp;
      if (sortBy === "severity") return severityOrder[b.severity] - severityOrder[a.severity];
      return b.aiScore - a.aiScore;
    });

    return result;
  }, [threats, statusFilter, severityFilter, sortBy]);

  const filterBtn = (label: string, active: boolean, onClick: () => void) => (
    <button
      onClick={onClick}
      className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
        active
          ? "bg-[#00ff88]/15 text-[#00ff88] border border-[#00ff88]/30"
          : "bg-white/5 text-gray-400 border border-[#1a1a2e] hover:text-white"
      }`}
    >
      {label}
    </button>
  );

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-wrap items-center gap-6">
        <div className="space-y-1">
          <span className="text-[10px] text-gray-600 uppercase tracking-wider block">Status</span>
          <div className="flex gap-2">
            {filterBtn("All", statusFilter === "ALL", () => setStatusFilter("ALL"))}
            {filterBtn("Pending", statusFilter === "PENDING", () => setStatusFilter("PENDING"))}
            {filterBtn("Verified", statusFilter === "VERIFIED", () => setStatusFilter("VERIFIED"))}
            {filterBtn("Settled", statusFilter === "SETTLED", () => setStatusFilter("SETTLED"))}
          </div>
        </div>
        <div className="space-y-1">
          <span className="text-[10px] text-gray-600 uppercase tracking-wider block">Severity</span>
          <div className="flex gap-2">
            {filterBtn("All", severityFilter === "ALL", () => setSeverityFilter("ALL"))}
            {filterBtn("Critical", severityFilter === "CRITICAL", () => setSeverityFilter("CRITICAL"))}
            {filterBtn("High", severityFilter === "HIGH", () => setSeverityFilter("HIGH"))}
            {filterBtn("Medium", severityFilter === "MEDIUM", () => setSeverityFilter("MEDIUM"))}
            {filterBtn("Low", severityFilter === "LOW", () => setSeverityFilter("LOW"))}
          </div>
        </div>
        <div className="space-y-1">
          <span className="text-[10px] text-gray-600 uppercase tracking-wider block">Sort</span>
          <div className="flex gap-2">
            {filterBtn("Time", sortBy === "time", () => setSortBy("time"))}
            {filterBtn("Severity", sortBy === "severity", () => setSortBy("severity"))}
            {filterBtn("AI Score", sortBy === "score", () => setSortBy("score"))}
          </div>
        </div>
      </div>

      <div className="text-xs text-gray-600 font-mono">
        Showing {filtered.length} of {threats.length} threats
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {filtered.map((threat) => (
          <ThreatCard key={threat.id} threat={threat} />
        ))}
      </div>

      {filtered.length === 0 && (
        <div className="text-center py-16 text-gray-600">
          <p className="text-sm">No threats match your filters</p>
        </div>
      )}
    </div>
  );
}
