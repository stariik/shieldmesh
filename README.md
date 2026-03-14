<p align="center">
  <img src="https://img.shields.io/badge/Solana-9945FF?style=for-the-badge&logo=solana&logoColor=white" alt="Solana" />
  <img src="https://img.shields.io/badge/Anchor-0.32-blue?style=for-the-badge" alt="Anchor" />
  <img src="https://img.shields.io/badge/Next.js_15-000000?style=for-the-badge&logo=next.js&logoColor=white" alt="Next.js" />
  <img src="https://img.shields.io/badge/Pollinet-Mesh_Network-00C853?style=for-the-badge" alt="Pollinet" />
  <img src="https://img.shields.io/badge/TensorFlow_Lite-On_Device_AI-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white" alt="TF Lite" />
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
| **Detect** | On-device AI (TensorFlow Lite) analyzes URLs, messages, and payloads for phishing, social engineering, and crypto scams — no cloud required |
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
  | (TensorFlow Lite)|       |   Pollinet Mesh     |
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
TensorFlow Lite models run entirely on the user's device. Scan URLs, SMS messages, emails, and wallet addresses for phishing attempts, social engineering patterns, and crypto-specific scams. Zero cloud dependency. Zero latency.

### Community-Staked Bounty Pool
Stake SOL into the ShieldMesh pool. When community members detect and report verified threats, they earn bounties proportional to threat severity. Stakers fund the security of their network and share in the protocol's growth.

### Offline-First Mesh Architecture
Powered by **Pollinet SDK**, ShieldMesh creates ad-hoc mesh networks over Bluetooth and Wi-Fi Direct. Threat alerts propagate device-to-device even when every cell tower and ISP in the region is down.

### Immutable On-Chain Threat Logs
Every verified threat — its hash, severity level, AI confidence score, reporter, and validators — is recorded permanently on Solana. This creates a transparent, community-owned threat intelligence database that anyone can audit.

### Phantom Wallet Integration
Seamless connection through Solana's Phantom wallet adapter for staking, claiming bounties, and browsing on-chain threat data.

### Dark Cybersecurity-Themed UI
A purpose-built interface designed for security operators and community defenders, with real-time threat feeds, staking dashboards, and mesh network status visualization.

---

## Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Smart Contracts** | Solana + Anchor 0.32 | On-chain bounty pool, staking, threat settlement |
| **Frontend** | Next.js 15 + Tailwind CSS 4 | Web application and dashboard |
| **State Management** | Zustand | Client-side state across wallet, mesh, and threat data |
| **Wallet** | Solana Wallet Adapter + Phantom | User authentication and transaction signing |
| **Mesh Networking** | Pollinet SDK | Offline P2P threat relay over Bluetooth/Wi-Fi Direct |
| **On-Device AI** | TensorFlow Lite | Threat classification without cloud inference |
| **Chain** | Solana (Devnet) | Fast finality, low fees, ideal for microbounties |

---

## Architecture

```
+------------------------------------------------------------------+
|                        ShieldMesh Client                         |
|                                                                  |
|  +------------------+  +------------------+  +-----------------+ |
|  |   AI Engine      |  |   Mesh Layer     |  |   Wallet Layer  | |
|  |   (TF Lite)      |  |   (Pollinet)     |  |   (Phantom)     | |
|  |                  |  |                  |  |                 | |
|  |  - URL Scanner   |  |  - BLE Broadcast |  |  - Stake SOL    | |
|  |  - SMS Analyzer  |  |  - WiFi Direct   |  |  - Claim Bounty | |
|  |  - Phish Detect  |  |  - Alert Relay   |  |  - View History | |
|  +--------+---------+  +--------+---------+  +--------+--------+ |
|           |                      |                     |          |
|           v                      v                     v          |
|  +-------------------------------------------------------+       |
|  |              Zustand State Management                 |       |
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
- [Yarn](https://yarnpkg.com/)

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

Open [http://localhost:3000](http://localhost:3000) and connect your Phantom wallet.

---

## Championship Tracks

ShieldMesh is built at the intersection of four competition tracks:

| Track | Relevance |
|---|---|
| **Pollinet** | Core infrastructure — offline-first mesh networking powers the entire threat relay system. ShieldMesh is a flagship use case for Pollinet's P2P capabilities. |
| **Scriptonia** | AI-native product development — on-device TensorFlow Lite models are central to the threat detection pipeline, not bolted on as an afterthought. |
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

## Project Structure

```
shieldmesh/
+-- programs/
|   +-- shieldmesh/
|       +-- src/
|           +-- instructions/     # Anchor instruction handlers
|           |   +-- initialize_pool.rs
|           |   +-- stake.rs
|           |   +-- unstake.rs
|           |   +-- report_threat.rs
|           |   +-- verify_threat.rs
|           |   +-- claim_bounty.rs
|           +-- state/            # On-chain account structures
|           |   +-- pool.rs       # StakingPool
|           |   +-- threat.rs     # ThreatAccount
|           |   +-- staker.rs     # StakerAccount
|           |   +-- bounty.rs     # BountyAccount
|           +-- errors.rs         # Custom error codes
|           +-- lib.rs            # Program entrypoint
+-- web/                          # Next.js 15 frontend
|   +-- src/
+-- tests/                        # Anchor integration tests
+-- migrations/
+-- Anchor.toml
+-- Cargo.toml
```

---

## Roadmap

- [x] Anchor smart contract with staking, reporting, verification, and bounty claims
- [x] Next.js 15 frontend with Phantom wallet integration
- [x] Dark cybersecurity-themed UI
- [ ] Pollinet SDK integration for offline mesh relay
- [ ] TensorFlow Lite on-device threat classifier
- [ ] Mobile app (React Native) with BLE mesh support
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
