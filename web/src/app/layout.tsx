import type { Metadata, Viewport } from "next";
import "./globals.css";
import ClientShell from "./ClientShell";

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
};

export const metadata: Metadata = {
  title: "ShieldMesh - Threat Intelligence",
  description: "AI-powered threat detection that works without internet. Detect, relay, and validate cybersecurity threats offline with bounty rewards on Solana.",
  icons: {
    icon: "/favicon.ico",
    apple: "/apple-touch-icon.png",
  },
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
