<p align="center">
  <img src="https://img.shields.io/badge/Solana-9945FF?style=for-the-badge&logo=solana&logoColor=white" alt="Solana" />
  <img src="https://img.shields.io/badge/Anchor-0.32-blue?style=for-the-badge" alt="Anchor" />
  <img src="https://img.shields.io/badge/Next.js_16-000000?style=for-the-badge&logo=next.js&logoColor=white" alt="Next.js" />
  <img src="https://img.shields.io/badge/Android-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Pollinet-Mesh_Network-00C853?style=for-the-badge" alt="Pollinet" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="MIT License" />
</p>

<h1 align="center">ShieldMesh</h1>
<h3 align="center">Offline-First Decentralized Threat Intelligence on Solana</h3>

<p align="center">
  <i>Turn every device into a security node. Detect threats with AI. Relay alerts through mesh. Reward reporters on-chain.</i>
</p>

---

## The Problem

Cyberattacks and internet shutdowns are not separate crises — they are the same attack.

When authoritarian regimes cut internet access, when natural disasters destroy infrastructure, or when adversaries deliberately target connectivity, every cloud-based security tool goes dark at the exact moment it is needed most.

- **3.4 billion people** live in regions with unreliable or compromised internet connectivity
- **Internet shutdowns increased 41%** in recent years, often coinciding with coordinated cyberattacks
- **100% of current security platforms** — from antivirus to SIEM — require persistent internet connections
- Phishing, social engineering, and crypto scams **do not stop** when the network goes down; they accelerate

The communities most vulnerable to cyber threats are the same communities least likely to have the connectivity required to defend against them.

**There is no offline security layer. Until now.**

---

## The Solution

**ShieldMesh** turns every smartphone and laptop into a threat intelligence node that works with or without the internet.

| Capability | How |
|---|---|
| **Detect** | On-device AI analyzes URLs, messages, and payloads for phishing, social engineering, and crypto scams — no cloud required |
| **Relay** | Pollinet mesh networking broadcasts threat alerts device-to-device via Bluetooth/Wi-Fi Direct — no internet required |
| **Verify** | Nearby devices independently validate threats through consensus, eliminating false positives |
| **Reward** | Community members stake SOL into a bounty pool; verified threat reporters earn bounties settled on Solana |
| **Record** | Every verified threat is logged immutably on-chain — creating a transparent, community-owned threat database |

---

## How It Works

```
    USER DEVICE                    MESH NETWORK                   SOLANA
    ----------                    ------------                   ------

  +------------------+
  | User scans a     |
  | URL or message   |
  +--------+---------+
           |
           v
  +------------------+
  | On-Device AI     |       +---------------------+
  | Engine analyzes  |       |                     |
  | threat patterns  |       |   Pollinet Mesh     |
  +--------+---------+       |   P2P Broadcast     |
           |                 |                     |
     Threat Detected ------->|  Device A ~~> B     |
           |                 |  Device B ~~> C     |
           |                 |  Device C ~~> D     |
           |                 +----------+----------+
           |                            |
           |                  Nearby devices
           |                  validate threat
           |                            |
           |                            v
           |                 +----------+----------+
           |                 | First device with   |
           |                 | internet connection  |
           |                 +----------+----------+
           |                            |
           |                            v
           |                 +----------+----------+
           +---------------->| Solana Settlement   |
                             |                     |
                             | - Threat logged     |
                             |   on-chain          |
                             | - Bounty paid to    |
                             |   reporter          |
                             | - Stakers earn      |
                             |   from pool         |
                             +---------------------+
```

**The key insight:** the first device in the mesh that regains internet connectivity settles all pending verified threats on Solana in a single batch. The network heals itself.

---

## Key Features

### On-Device AI Threat Detection
Pattern-matching neural engine runs entirely on the user's device. Scan URLs, SMS messages, emails, and wallet addresses for phishing attempts, social engineering patterns, and crypto-specific scams. Zero cloud dependency. Zero latency.

### Pollinet Mesh Relay (Offline-First)
Powered by **Pollinet SDK**, ShieldMesh creates ad-hoc mesh networks over Bluetooth Low Energy and Wi-Fi Direct. Threat alerts propagate device-to-device even when every cell tower and ISP in the region is down. Threats are queued locally, relayed through the mesh, and settled on-chain when any peer regains connectivity.

### Community-Staked Bounty Pool
Stake SOL into the ShieldMesh pool. When community members detect and report verified threats, they earn bounties proportional to threat severity. Stakers fund the security of their network and share in the protocol's growth.

### Immutable On-Chain Threat Logs
Every verified threat — its hash, severity level, AI confidence score, reporter, and validators — is recorded permanently on Solana. This creates a transparent, community-owned threat intelligence database that anyone can audit.

### Android Mobile App
Native Kotlin app with Jetpack Compose UI, Room database for offline storage, Hilt dependency injection, and Pollinet mesh integration. The mobile app is the primary offline threat detection and relay device.

### Responsive Web Dashboard
Next.js 16 web application with landing page, dark cybersecurity-themed UI, Phantom wallet integration, and full mobile responsive layout with hamburger navigation.

### APK Download
The Android APK is available for direct download from the web dashboard, enabling quick installation without app store dependencies.

---

## Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Smart Contracts** | Solana + Anchor 0.32 | On-chain bounty pool, staking, threat settlement |
| **Web Frontend** | Next.js 16 + Tailwind CSS 4 | Landing page, dashboard, and responsive web app |
| **Android App** | Kotlin + Jetpack Compose | Native mobile app with offline-first architecture |
| **Local Database** | Room (Android) | Offline threat storage with sync status tracking |
| **State Management** | Zustand (Web) / StateFlow (Android) | Client-side state across wallet, mesh, and threat data |
| **Wallet** | Solana Wallet Adapter + Phantom | User authentication and transaction signing |
| **Mesh Networking** | Pollinet SDK | Offline P2P threat relay over BLE/Wi-Fi Direct |
| **DI Framework** | Hilt (Android) | Dependency injection for repositories and ViewModels |
| **Chain** | Solana (Devnet) | Fast finality, low fees, ideal for microbounties |

---

## Architecture

```
+------------------------------------------------------------------+
|                        ShieldMesh Client                         |
|                                                                  |
|  +------------------+  +------------------+  +-----------------+ |
|  |   AI Engine      |  |   Mesh Layer     |  |   Wallet Layer  | |
|  |                  |  |   (Pollinet)     |  |   (Phantom)     | |
|  |                  |  |                  |  |                 | |
|  |  - URL Scanner   |  |  - BLE Broadcast |  |  - Stake SOL    | |
|  |  - Phish Detect  |  |  - WiFi Direct   |  |  - Claim Bounty | |
|  |  - Scam Patterns |  |  - Alert Relay   |  |  - View History | |
|  +--------+---------+  +--------+---------+  +--------+--------+ |
|           |                      |                     |          |
|           v                      v                     v          |
|  +-------------------------------------------------------+       |
|  |     Zustand (Web) / Room + StateFlow (Android)        |       |
|  +---------------------------+---------------------------+       |
+------------------------------------------------------------------+
                               |
                               v
+------------------------------------------------------------------+
|                      Solana Blockchain                            |
|                                                                  |
|  Program: DKcRE94UZtL18AVrDMJHviw5pUHp5L9xr11Q1njvUmvK          |
|                                                                  |
|  +------------------+  +------------------+  +-----------------+ |
|  |  StakingPool     |  |  ThreatAccount   |  |  StakerAccount  | |
|  |                  |  |                  |  |                 | |
|  |  - authority     |  |  - reporter      |  |  - owner        | |
|  |  - total_staked  |  |  - threat_hash   |  |  - amount       | |
|  |  - staker_count  |  |  - severity      |  |  - timestamp    | |
|  |  - reward_rates  |  |  - ai_score      |  |                 | |
|  |                  |  |  - status         |  |                 | |
|  |                  |  |  - validators     |  |                 | |
|  +------------------+  +------------------+  +-----------------+ |
+------------------------------------------------------------------+
```

---

## Smart Contract

**Program ID:** `DKcRE94UZtL18AVrDMJHviw5pUHp5L9xr11Q1njvUmvK`

### Instructions

| Instruction | Description |
|---|---|
| `initialize_pool` | Deploy the staking pool with configurable reward rates per severity level |
| `stake` | Stake SOL into the community bounty pool |
| `unstake` | Withdraw staked SOL from the pool |
| `report_threat` | Submit a detected threat with its hash, severity (0-3), and AI confidence score |
| `verify_threat` | Validate a pending threat report (requires separate validator) |
| `claim_bounty` | Claim earned bounty after threat reaches VERIFIED status |

### Bounty Rates by Severity

| Severity | Bounty | Examples |
|---|---|---|
| `LOW` (0) | 0.01 SOL | Suspicious URL pattern, low-confidence phish |
| `MEDIUM` (1) | 0.05 SOL | Confirmed phishing page, social engineering attempt |
| `HIGH` (2) | 0.10 SOL | Active credential harvesting, malware distribution |
| `CRITICAL` (3) | 0.25 SOL | Zero-day exploit, coordinated attack campaign |

### Threat Lifecycle

```
PENDING  ──>  VERIFIED  ──>  SETTLED
  (0)           (1)           (2)

Report       Validators      Bounty
submitted    confirm         paid out
on-chain     the threat      to reporter
```

---

## Getting Started

### Prerequisites

- [Rust](https://rustup.rs/) (latest stable)
- [Solana CLI](https://docs.solanalabs.com/cli/install) (v1.18+)
- [Anchor CLI](https://www.anchor-lang.com/docs/installation) (v0.32+)
- [Node.js](https://nodejs.org/) (v18+)
- [Android Studio](https://developer.android.com/studio) (for mobile app)
- Java 17+ (for Android builds)

### 1. Clone the Repository

```bash
git clone https://github.com/stariik/shieldmesh.git
cd shieldmesh
```

### 2. Build the Smart Contract

```bash
anchor build
```

### 3. Run Tests

```bash
anchor test
```

### 4. Deploy to Devnet

```bash
solana config set --url devnet
anchor deploy
```

### 5. Launch the Web App

```bash
cd web
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) to see the landing page, then navigate to the dashboard.

### 6. Build the Android App

```bash
cd android
./gradlew assembleDebug
```

The APK will be at `android/app/build/outputs/apk/debug/app-debug.apk`.

Alternatively, download the pre-built APK from the web dashboard.

---

## Project Structure

```
shieldmesh/
+-- programs/
|   +-- shieldmesh/
|       +-- src/
|           +-- instructions/     # Anchor instruction handlers
|           +-- state/            # On-chain account structures
|           +-- errors.rs         # Custom error codes
|           +-- lib.rs            # Program entrypoint
+-- web/                          # Next.js 16 frontend
|   +-- src/
|       +-- app/                  # App Router pages (landing, dashboard, scan, etc.)
|       +-- components/           # UI components (layout, threats, bounty, mesh, wallet)
|       +-- hooks/                # Custom hooks (useOnlineStatus, useShieldMesh, etc.)
|       +-- store/                # Zustand stores (threat, wallet, sidebar)
|       +-- providers/            # Context providers (Wallet, WalletSync)
|       +-- lib/                  # Utilities (AI scanner, Solana program, IDL)
|   +-- public/
|       +-- ShieldMesh.apk        # Pre-built Android APK for download
+-- android/                      # Native Android app
|   +-- app/src/main/java/com/shieldmesh/app/
|       +-- ai/                   # On-device threat detection engine
|       +-- data/                 # Room database, DAOs, entities, repositories
|       +-- mesh/                 # Pollinet SDK manager and mesh networking
|       +-- sync/                 # Connectivity observer and sync manager
|       +-- ui/                   # Jetpack Compose screens and ViewModels
+-- tests/                        # Anchor integration tests
+-- Anchor.toml
+-- Cargo.toml
```

---

## Championship Tracks

ShieldMesh is built at the intersection of four competition tracks:

| Track | Relevance |
|---|---|
| **Pollinet** | Core infrastructure — offline-first mesh networking powers the entire threat relay system. ShieldMesh is a flagship use case for Pollinet's P2P capabilities. Threats are queued locally, broadcast via BLE mesh, and settled on Solana when any peer reconnects. |
| **Scriptonia** | AI-native product development — on-device threat detection models are central to the detection pipeline, not bolted on as an afterthought. The scanner runs entirely offline. |
| **TigerPay** | DeFi bounty marketplace — the staking pool and bounty claim system create an agentic commerce layer where threat intelligence has real monetary value. |
| **Cybersecurity** | Core application domain — ShieldMesh exists to solve a real, unsolved problem in decentralized security for underserved communities. |

---

## Why Solana?

ShieldMesh settles microbounties (as low as 0.01 SOL) for individual threat reports. This requires:

- **Sub-second finality** — bounties must settle before the reporter's device goes offline again
- **Sub-cent transaction fees** — microbounties are economically impossible on high-fee chains
- **High throughput** — batch settlement of hundreds of mesh-queued threats in a single burst

Solana is the only chain where this economic model works.

---

## Roadmap

- [x] Anchor smart contract with staking, reporting, verification, and bounty claims
- [x] Next.js 16 web dashboard with Phantom wallet integration
- [x] Dark cybersecurity-themed UI with mobile responsive layout
- [x] Landing page with product overview and APK download
- [x] Pollinet SDK integration for offline mesh relay
- [x] On-device AI threat detection engine
- [x] Android app (Kotlin/Jetpack Compose) with Room DB and offline-first architecture
- [x] Offline queue with auto-sync on reconnection
- [x] APK download from web dashboard
- [ ] Governance module for community-managed reward rates
- [ ] Mainnet deployment

---

## Team

Built by **Tornike** ([@stariik](https://github.com/stariik)) for the Scriptonia Solana Championship.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

<p align="center">
  <b>ShieldMesh</b> — Security doesn't stop when the internet does.
</p>
