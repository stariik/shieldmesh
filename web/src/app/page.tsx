import Image from "next/image";
import Link from "next/link";

const features = [
  {
    title: "AI Threat Detection",
    description: "Neural scan engine analyzes URLs, smart contracts, and messages for phishing, scams, and exploits in real-time.",
    icon: "M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z",
    color: "#00d4ff",
  },
  {
    title: "Pollinet Mesh Relay",
    description: "Offline-first threat sharing via BLE and Wi-Fi Direct. Devices relay threat data through the mesh until one reaches the internet.",
    icon: "M8.111 16.404a5.5 5.5 0 017.778 0M12 20h.01M2 8.82a15 15 0 0120 0M5.636 12.025a9 9 0 0112.728 0",
    color: "#9945FF",
  },
  {
    title: "Solana Bounties",
    description: "Report verified threats and earn SOL rewards. Stake tokens to validate reports and build reputation in the network.",
    icon: "M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z",
    color: "#00ff88",
  },
];

const steps = [
  { num: "01", title: "Detect", desc: "AI scans for threats even without internet" },
  { num: "02", title: "Relay", desc: "Broadcast to nearby devices via BLE mesh" },
  { num: "03", title: "Validate", desc: "Peers verify and propagate through the network" },
  { num: "04", title: "Settle", desc: "Confirmed threats recorded on Solana blockchain" },
];

const techStack = [
  { name: "Solana", color: "#9945FF" },
  { name: "Pollinet SDK", color: "#00d4ff" },
  { name: "Next.js 16", color: "#ffffff" },
  { name: "Kotlin", color: "#7F52FF" },
  { name: "Jetpack Compose", color: "#4285F4" },
  { name: "Anchor", color: "#00ff88" },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-[#0a0a0f] text-white">
      {/* Hero */}
      <section className="relative overflow-hidden">
        {/* Background glow effects */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[600px] h-[600px] bg-[#00ff88]/5 rounded-full blur-[120px] pointer-events-none" />
        <div className="absolute top-20 right-0 w-[400px] h-[400px] bg-[#9945FF]/5 rounded-full blur-[100px] pointer-events-none" />

        <div className="relative max-w-5xl mx-auto px-6 pt-16 sm:pt-24 pb-20 text-center">
          {/* Logo */}
          <div className="flex items-center justify-center gap-4 mb-8">
            <Image
              src="/logo.png"
              alt="ShieldMesh Logo"
              width={64}
              height={64}
              className="drop-shadow-[0_0_20px_rgba(0,255,136,0.3)]"
            />
            <div className="text-left">
              <span className="text-3xl font-black tracking-tight">
                <span className="text-white">Shield</span>
                <span className="bg-gradient-to-r from-[#00ff88] to-[#00d4ff] bg-clip-text text-transparent">Mesh</span>
              </span>
              <div className="text-[10px] text-gray-500 uppercase tracking-[0.25em] font-mono -mt-0.5">Decentralized Threat Intelligence</div>
            </div>
          </div>

          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold leading-tight mb-6">
            Security doesn&apos;t stop
            <br />
            <span className="bg-gradient-to-r from-[#00ff88] via-[#00d4ff] to-[#9945FF] bg-clip-text text-transparent">
              when the internet does
            </span>
          </h1>

          <p className="text-lg sm:text-xl text-gray-400 max-w-2xl mx-auto mb-10 leading-relaxed">
            AI-powered threat detection that works without internet. ShieldMesh uses Pollinet mesh networking to detect, relay, and validate cybersecurity threats across devices — even fully offline — with bounty rewards settled on Solana.
          </p>

          {/* CTAs */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
            <Link
              href="/dashboard"
              className="px-8 py-3.5 bg-gradient-to-r from-[#00ff88] to-[#00d4ff] text-[#0a0a0f] font-bold text-sm rounded-xl hover:opacity-90 transition-opacity shadow-[0_0_30px_rgba(0,255,136,0.2)] w-full sm:w-auto text-center"
            >
              Launch Dashboard
            </Link>
            <a
              href="/ShieldMesh.apk"
              download
              className="px-8 py-3.5 border border-[#9945FF]/40 text-[#9945FF] font-semibold text-sm rounded-xl hover:bg-[#9945FF]/10 transition-all w-full sm:w-auto text-center flex items-center justify-center gap-2"
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              Download Android App
            </a>
          </div>

          {/* Tech stack badges */}
          <div className="flex flex-wrap items-center justify-center gap-3">
            {techStack.map((tech) => (
              <span
                key={tech.name}
                className="px-3 py-1.5 rounded-lg text-xs font-mono border border-white/10 bg-white/5"
                style={{ color: tech.color }}
              >
                {tech.name}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-5xl mx-auto px-6 py-16 sm:py-24">
        <h2 className="text-sm text-gray-500 uppercase tracking-widest font-mono text-center mb-12">Core Capabilities</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {features.map((f) => (
            <div key={f.title} className="bg-[#0f0f1a] border border-[#1a1a2e] rounded-2xl p-6 hover:border-white/10 transition-colors">
              <div
                className="w-12 h-12 rounded-xl flex items-center justify-center mb-4 border"
                style={{
                  backgroundColor: `${f.color}15`,
                  borderColor: `${f.color}25`,
                }}
              >
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={f.color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                  <path d={f.icon} />
                </svg>
              </div>
              <h3 className="text-white font-semibold mb-2">{f.title}</h3>
              <p className="text-sm text-gray-500 leading-relaxed">{f.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* How it works */}
      <section className="max-w-5xl mx-auto px-6 py-16 sm:py-24">
        <h2 className="text-sm text-gray-500 uppercase tracking-widest font-mono text-center mb-12">How It Works</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {steps.map((step) => (
            <div key={step.num} className="text-center">
              <div className="w-14 h-14 mx-auto rounded-2xl bg-gradient-to-br from-[#00ff88]/10 to-[#00d4ff]/10 border border-[#00ff88]/20 flex items-center justify-center mb-4">
                <span className="text-[#00ff88] font-mono font-bold text-lg">{step.num}</span>
              </div>
              <h3 className="text-white font-semibold mb-1">{step.title}</h3>
              <p className="text-xs text-gray-500 leading-relaxed">{step.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Offline-first highlight */}
      <section className="max-w-5xl mx-auto px-6 py-16 sm:py-24">
        <div className="bg-gradient-to-br from-[#9945FF]/10 to-[#00d4ff]/5 border border-[#9945FF]/20 rounded-2xl p-8 sm:p-12 text-center">
          <div className="w-16 h-16 mx-auto rounded-2xl bg-[#9945FF]/15 border border-[#9945FF]/30 flex items-center justify-center mb-6">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#9945FF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M8.111 16.404a5.5 5.5 0 017.778 0" />
              <path d="M12 20h.01" />
              <path d="M2 8.82a15 15 0 0120 0" />
              <path d="M5.636 12.025a9 9 0 0112.728 0" />
            </svg>
          </div>
          <h2 className="text-2xl sm:text-3xl font-bold mb-4">
            Powered by <span className="text-[#9945FF]">Pollinet</span> Mesh
          </h2>
          <p className="text-gray-400 max-w-xl mx-auto mb-6 leading-relaxed">
            No internet? No problem. ShieldMesh uses Pollinet&apos;s BLE and Wi-Fi Direct mesh networking
            to relay threat reports between nearby devices. When any device in the mesh regains connectivity,
            all queued threats are settled on the Solana blockchain.
          </p>
          <div className="flex flex-wrap items-center justify-center gap-8 text-sm font-mono">
            <div>
              <div className="text-2xl font-bold text-[#9945FF]">BLE</div>
              <div className="text-gray-500 text-xs">Bluetooth Low Energy</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-[#00d4ff]">P2P</div>
              <div className="text-gray-500 text-xs">Wi-Fi Direct</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-[#00ff88]">0 ms</div>
              <div className="text-gray-500 text-xs">Local-first latency</div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-[#1a1a2e] py-8 px-6">
        <div className="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2.5">
            <Image src="/logo.png" alt="ShieldMesh" width={28} height={28} />
            <span className="text-sm font-bold text-gray-400">Shield<span className="text-[#00ff88]">Mesh</span></span>
          </div>
          <div className="text-xs text-gray-600 font-mono text-center">
            Built for Scriptonia Championship 2026 // Solana Devnet
          </div>
          <div className="text-xs text-gray-600">
            <a href="https://github.com/stariik/shieldmesh" target="_blank" rel="noopener noreferrer" className="hover:text-gray-400 transition-colors">
              GitHub
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
