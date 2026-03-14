export type Severity = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
export type ThreatStatus = "PENDING" | "VERIFIED" | "SETTLED";

export interface Threat {
  id: string;
  hash: string;
  severity: Severity;
  aiScore: number;
  reporterPubkey: string;
  validatorCount: number;
  status: ThreatStatus;
  timestamp: number;
  description: string;
  url: string;
}
