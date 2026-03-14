import type { Severity } from "./threat";

export interface Bounty {
  id: string;
  threatId: string;
  reporter: string;
  amount: number;
  claimed: boolean;
  timestamp: number;
  severity: Severity;
}
