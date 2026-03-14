import type { Metadata } from "next";
import "./globals.css";
import ClientShell from "./ClientShell";

export const metadata: Metadata = {
  title: "ShieldMesh - Threat Intelligence",
  description: "Decentralized cybersecurity threat intelligence platform on Solana",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="min-h-screen bg-[#0a0a0f] text-white antialiased">
        <ClientShell>{children}</ClientShell>
      </body>
    </html>
  );
}
