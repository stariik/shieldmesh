"use client";

import dynamic from "next/dynamic";

const WalletMultiButton = dynamic(
  () =>
    import("@solana/wallet-adapter-react-ui").then(
      (mod) => mod.WalletMultiButton
    ),
  { ssr: false }
);

export default function WalletConnect() {
  return (
    <div className="shieldmesh-wallet-btn">
      <WalletMultiButton
        style={{
          background: "linear-gradient(to right, #00ff88, #00d4ff)",
          color: "#0a0a0f",
          fontWeight: 700,
          fontSize: "0.8rem",
          height: "38px",
          borderRadius: "0.5rem",
          padding: "0 1rem",
          boxShadow: "0 0 20px rgba(0,255,136,0.15)",
          transition: "opacity 0.2s",
          fontFamily: "ui-monospace, SFMono-Regular, monospace",
        }}
      />
    </div>
  );
}
