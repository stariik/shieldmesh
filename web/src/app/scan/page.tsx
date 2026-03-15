"use client";

import ThreatScanner from "@/components/threats/ThreatScanner";

export default function ScanPage() {
  return (
    <div className="max-w-3xl mx-auto space-y-8 animate-fade-in">
      <div>
        <h2 className="text-xl font-semibold text-white mb-2">AI Threat Scanner</h2>
        <p className="text-sm text-gray-500">
          Paste a URL, message, or upload a suspicious file to analyze for threats using the ShieldMesh neural detection engine.
        </p>
      </div>

      <ThreatScanner />

      <div className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-xl p-6">
        <h3 className="text-sm text-gray-400 uppercase tracking-wider font-mono mb-4">How it works</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="space-y-2">
            <div className="w-10 h-10 rounded-xl bg-[#00ff88]/10 flex items-center justify-center">
              <span className="text-[#00ff88] font-mono font-bold">01</span>
            </div>
            <h4 className="text-sm text-white font-medium">Submit Target</h4>
            <p className="text-xs text-gray-500 leading-relaxed">
              Paste any URL, contract address, or message you want to analyze for potential threats.
            </p>
          </div>
          <div className="space-y-2">
            <div className="w-10 h-10 rounded-xl bg-[#00d4ff]/10 flex items-center justify-center">
              <span className="text-[#00d4ff] font-mono font-bold">02</span>
            </div>
            <h4 className="text-sm text-white font-medium">AI Analysis</h4>
            <p className="text-xs text-gray-500 leading-relaxed">
              Our neural engine checks against known threat patterns, malicious bytecode signatures, and phishing indicators.
            </p>
          </div>
          <div className="space-y-2">
            <div className="w-10 h-10 rounded-xl bg-[#00ff88]/10 flex items-center justify-center">
              <span className="text-[#00ff88] font-mono font-bold">03</span>
            </div>
            <h4 className="text-sm text-white font-medium">Report & Earn</h4>
            <p className="text-xs text-gray-500 leading-relaxed">
              Report verified threats to the mesh network and earn SOL bounties when validators confirm your findings.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
