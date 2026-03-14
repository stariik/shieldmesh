import type { Severity } from "@/types/threat";

export const PROGRAM_ID = "DKcRE94UZtL18AVrDMJHviw5pUHp5L9xr11Q1njvUmvK";

export const DEVNET_URL = "https://api.devnet.solana.com";

export const SEVERITY_LABELS: Record<Severity, string> = {
  LOW: "Low",
  MEDIUM: "Medium",
  HIGH: "High",
  CRITICAL: "Critical",
};

export const SEVERITY_COLORS: Record<Severity, string> = {
  LOW: "#22c55e",
  MEDIUM: "#eab308",
  HIGH: "#f97316",
  CRITICAL: "#ef4444",
};

export const SEVERITY_BG: Record<Severity, string> = {
  LOW: "bg-green-500/20 text-green-400 border-green-500/30",
  MEDIUM: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30",
  HIGH: "bg-orange-500/20 text-orange-400 border-orange-500/30",
  CRITICAL: "bg-red-500/20 text-red-400 border-red-500/30",
};

export const REWARD_RATES: Record<Severity, number> = {
  LOW: 0.01,
  MEDIUM: 0.05,
  HIGH: 0.1,
  CRITICAL: 0.25,
};

export const STATUS_COLORS: Record<string, string> = {
  PENDING: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30",
  VERIFIED: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30",
  SETTLED: "bg-green-500/20 text-green-400 border-green-500/30",
};
