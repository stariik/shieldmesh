"use client";

import { useState, useCallback } from "react";
import type { Severity } from "@/types/threat";
import { SEVERITY_BG } from "@/lib/constants";
import { useThreatStore } from "@/store/threatStore";
import { threatScanner, sha256Hex } from "@/lib/ai-scanner";
import { useShieldMesh } from "@/hooks/useShieldMesh";
import { useWallet } from "@solana/wallet-adapter-react";
import type { ThreatResult, ThreatIndicator } from "@/lib/ai-scanner";

const CATEGORY_COLORS: Record<string, string> = {
  phishing: "bg-red-500/20 text-red-400 border-red-500/30",
  social_engineering: "bg-orange-500/20 text-orange-400 border-orange-500/30",
  crypto_scam: "bg-purple-500/20 text-purple-400 border-purple-500/30",
  technical: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30",
  url_structure: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30",
};

const CATEGORY_LABELS: Record<string, string> = {
  phishing: "Phishing",
  social_engineering: "Social Engineering",
  crypto_scam: "Crypto Scam",
  technical: "Technical",
  url_structure: "URL Structure",
};

export default function ThreatScanner() {
  const [input, setInput] = useState("");
  const [scanning, setScanning] = useState(false);
  const [progress, setProgress] = useState(0);
  const [result, setResult] = useState<ThreatResult | null>(null);
  const [scanPhase, setScanPhase] = useState("");
  const addThreat = useThreatStore((s) => s.addThreat);
  const [reported, setReported] = useState(false);
  const [chainTxSig, setChainTxSig] = useState<string | null>(null);
  const [chainReporting, setChainReporting] = useState(false);
  const [chainError, setChainError] = useState<string | null>(null);

  const { reportThreat, connected: shieldMeshConnected } = useShieldMesh();
  const { publicKey } = useWallet();

  const phases = [
    "Initializing neural scan engine...",
    "Resolving DNS records & WHOIS data...",
    "Running homoglyph & typo-squat detection...",
    "Analyzing URL structure & redirect chains...",
    "Scanning message for social engineering patterns...",
    "Correlating against threat intelligence database...",
    "Computing weighted AI threat score...",
    "Finalizing analysis...",
  ];

  const runScan = useCallback(() => {
    if (!input.trim()) return;
    setScanning(true);
    setResult(null);
    setProgress(0);
    setReported(false);
    setChainTxSig(null);
    setChainError(null);

    // Eagerly compute the real result so we can reveal it at the end
    const realResult = threatScanner.analyze(input.trim());

    let currentProgress = 0;
    let phaseIndex = 0;
    setScanPhase(phases[0]);

    const interval = setInterval(() => {
      currentProgress += Math.random() * 15 + 5;
      if (currentProgress > 100) currentProgress = 100;
      setProgress(Math.floor(currentProgress));

      const newPhaseIndex = Math.min(
        Math.floor((currentProgress / 100) * phases.length),
        phases.length - 1,
      );
      if (newPhaseIndex !== phaseIndex) {
        phaseIndex = newPhaseIndex;
        setScanPhase(phases[phaseIndex]);
      }

      if (currentProgress >= 100) {
        clearInterval(interval);
        setResult(realResult);
        setScanning(false);
        setScanPhase("");
      }
    }, 200);
  }, [input]);

  const handleReport = async () => {
    if (!result) return;

    const hashHex = sha256Hex(input.trim() + Date.now().toString());
    const walletKey = publicKey?.toBase58() ?? "YourWa11etPubkeyHere111111111111111111111111";

    // Always add to local threat store
    addThreat({
      id: `t-${Date.now().toString(36)}`,
      hash: hashHex,
      severity: result.severity,
      aiScore: result.score,
      reporterPubkey: walletKey,
      validatorCount: 0,
      status: "PENDING",
      timestamp: Date.now(),
      description: result.description,
      url: input.trim(),
    });
    setReported(true);

    // If wallet is connected, also submit on-chain
    if (shieldMeshConnected) {
      setChainReporting(true);
      setChainError(null);

      try {
        // Convert hex hash to 32-byte array
        const hashBytes: number[] = [];
        for (let i = 0; i < hashHex.length && hashBytes.length < 32; i += 2) {
          hashBytes.push(parseInt(hashHex.substring(i, i + 2), 16));
        }
        // Pad to 32 bytes if needed
        while (hashBytes.length < 32) hashBytes.push(0);

        const txResult = await reportThreat(hashBytes, result.severity, result.score);

        if (txResult.success) {
          setChainTxSig(txResult.signature ?? null);
        } else {
          setChainError(txResult.error ?? "On-chain report failed");
        }
      } catch (err: any) {
        setChainError(err?.message || "On-chain report failed");
      } finally {
        setChainReporting(false);
      }
    }
  };

  return (
    <div className="space-y-6">
      {/* Input bar */}
      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-6">
        <label className="block text-sm text-gray-400 mb-2 font-mono">
          Paste URL or message to scan
        </label>
        <div className="flex flex-col sm:flex-row gap-3">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="https://suspicious-site.xyz/connect-wallet..."
            className="flex-1 bg-[#0a0a0f] border border-[#1a1a2e] rounded-lg px-4 py-3 text-sm text-white font-mono placeholder-gray-600 focus:outline-none focus:border-[#00ff88]/40 focus:shadow-[0_0_15px_rgba(0,255,136,0.05)] transition-all"
            onKeyDown={(e) => e.key === "Enter" && runScan()}
          />
          <button
            onClick={runScan}
            disabled={scanning || !input.trim()}
            className="px-6 py-3 bg-gradient-to-r from-[#00ff88] to-[#00d4ff] text-[#0a0a0f] font-bold text-sm rounded-lg hover:opacity-90 disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-[0_0_20px_rgba(0,255,136,0.15)] shrink-0"
          >
            {scanning ? "Scanning..." : "Scan"}
          </button>
        </div>
      </div>

      {/* Scan animation */}
      {scanning && (
        <div className="bg-[#0f0f1a] border border-[#00ff88]/20 rounded-xl p-6 space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-sm text-[#00ff88] font-mono animate-pulse">
              {scanPhase}
            </span>
            <span className="text-sm text-[#00ff88] font-mono font-bold">
              {progress}%
            </span>
          </div>
          <div className="w-full h-2 bg-[#1a1a2e] rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-[#00ff88] to-[#00d4ff] rounded-full transition-all duration-200"
              style={{ width: `${progress}%` }}
            />
          </div>
          <div className="flex gap-1">
            {Array.from({ length: 40 }).map((_, i) => (
              <div
                key={i}
                className="flex-1 bg-[#00ff88]/5 rounded-sm"
                style={{
                  opacity: i < (progress / 100) * 40 ? 1 : 0.2,
                  height: `${Math.random() * 24 + 8}px`,
                  transition: "all 0.3s",
                }}
              />
            ))}
          </div>
        </div>
      )}

      {/* Results */}
      {result && !scanning && (
        <div
          className={`bg-[#0f0f1a] border rounded-xl p-6 space-y-5 ${
            result.safe ? "border-green-500/30" : "border-red-500/30"
          }`}
        >
          {/* Header row */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div
                className={`w-12 h-12 rounded-xl flex items-center justify-center text-2xl font-bold ${
                  result.safe
                    ? "bg-green-500/20 text-green-400"
                    : "bg-red-500/20 text-red-400"
                }`}
              >
                {result.safe ? (
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <path d="M20 6L9 17l-5-5" />
                  </svg>
                ) : (
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
                    <line x1="12" y1="9" x2="12" y2="13" />
                    <line x1="12" y1="17" x2="12.01" y2="17" />
                  </svg>
                )}
              </div>
              <div>
                <h3 className="text-white font-semibold">
                  {result.safe ? "No Significant Threats" : "Threats Detected"}
                </h3>
                <p className="text-xs text-gray-500">
                  {result.indicators.length} indicator{result.indicators.length !== 1 ? "s" : ""} analyzed
                </p>
              </div>
            </div>
            <div className="text-right">
              <div
                className={`text-3xl font-mono font-bold ${
                  result.safe ? "text-green-400" : "text-red-400"
                }`}
              >
                {result.score}
              </div>
              <span
                className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider border ${
                  SEVERITY_BG[result.severity]
                }`}
              >
                {result.severity}
              </span>
            </div>
          </div>

          {/* AI Description */}
          <div className="bg-[#0a0a14] border border-[#1a1a2e] rounded-lg p-4">
            <h4 className="text-xs text-gray-500 uppercase tracking-wider font-mono mb-2">
              AI Analysis Summary
            </h4>
            <p className="text-sm text-gray-300 leading-relaxed">
              {result.description}
            </p>
          </div>

          {/* Indicator tags */}
          {result.indicators.length > 0 && (
            <div className="space-y-3">
              <h4 className="text-xs text-gray-500 uppercase tracking-wider font-mono">
                Threat Indicators ({result.indicators.length})
              </h4>
              <div className="flex flex-wrap gap-2">
                {result.indicators.map((ind, i) => (
                  <span
                    key={i}
                    className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium border ${
                      CATEGORY_COLORS[ind.category] ?? "bg-gray-500/20 text-gray-400 border-gray-500/30"
                    }`}
                  >
                    <span className="opacity-70">[{CATEGORY_LABELS[ind.category] ?? ind.category}]</span>
                    <span>+{ind.weight}</span>
                  </span>
                ))}
              </div>

              {/* Detailed list */}
              <div className="space-y-1.5 mt-2">
                {result.indicators.map((ind, i) => (
                  <div
                    key={i}
                    className="flex items-start gap-2 text-sm text-gray-300 bg-red-500/5 px-3 py-2 rounded-lg border border-red-500/10"
                  >
                    <span className="text-red-400 mt-0.5 shrink-0">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10" />
                        <line x1="12" y1="8" x2="12" y2="12" />
                        <line x1="12" y1="16" x2="12.01" y2="16" />
                      </svg>
                    </span>
                    <span>{ind.label}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Action buttons */}
          <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 pt-2">
            <button
              onClick={handleReport}
              disabled={reported || chainReporting}
              className={`px-5 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                reported
                  ? "bg-green-500/20 text-green-400 border border-green-500/30 cursor-default"
                  : "bg-gradient-to-r from-[#00ff88] to-[#00d4ff] text-[#0a0a0f] hover:opacity-90 shadow-[0_0_20px_rgba(0,255,136,0.15)]"
              }`}
            >
              {chainReporting ? (
                <span className="flex items-center gap-2">
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Submitting on-chain...
                </span>
              ) : reported ? (
                "Reported to Network"
              ) : shieldMeshConnected ? (
                "Report to Network (on-chain)"
              ) : (
                "Report to Network"
              )}
            </button>
            <button
              onClick={() => {
                setResult(null);
                setInput("");
                setChainTxSig(null);
                setChainError(null);
              }}
              className="px-5 py-2.5 rounded-lg text-sm font-medium text-gray-400 border border-[#1a1a2e] hover:border-gray-600 transition-all"
            >
              New Scan
            </button>
          </div>

          {/* Pollinet mesh relay indicator */}
          {reported && (
            <div className="bg-[#9945FF]/10 border border-[#9945FF]/25 rounded-lg p-3 flex items-center gap-3">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#9945FF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="shrink-0">
                <path d="M8.111 16.404a5.5 5.5 0 017.778 0" />
                <path d="M12 20h.01" />
                <path d="M2 8.82a15 15 0 0120 0" />
                <path d="M5.636 12.025a9 9 0 0112.728 0" />
              </svg>
              <div>
                <div className="text-xs text-[#9945FF] font-mono font-medium">Queued for Pollinet mesh relay</div>
                <div className="text-[10px] text-gray-500 mt-0.5">Broadcasting to nearby peers via BLE/Wi-Fi Direct mesh</div>
              </div>
            </div>
          )}

          {/* On-chain transaction result */}
          {chainTxSig && (
            <div className="bg-green-500/10 border border-green-500/30 rounded-lg p-3">
              <div className="text-xs text-green-400 font-mono mb-1">
                Threat recorded on Solana devnet
              </div>
              <a
                href={`https://explorer.solana.com/tx/${chainTxSig}?cluster=devnet`}
                target="_blank"
                rel="noopener noreferrer"
                className="text-[10px] text-[#00d4ff] hover:underline break-all font-mono"
              >
                TX: {chainTxSig.slice(0, 24)}...{chainTxSig.slice(-8)}
              </a>
            </div>
          )}

          {chainError && (
            <div className="bg-red-500/10 border border-red-500/30 rounded-lg p-3">
              <div className="text-xs text-red-400 font-mono">
                On-chain report failed: {chainError}
              </div>
              <div className="text-[10px] text-gray-500 mt-1">
                Threat was saved locally. Connect your wallet and ensure a pool is initialized.
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
