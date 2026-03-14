import { AnchorProvider, Program } from "@coral-xyz/anchor";
import { Connection, PublicKey } from "@solana/web3.js";
import type { AnchorWallet } from "@solana/wallet-adapter-react";
import { SHIELDMESH_IDL, SHIELDMESH_PROGRAM_ID } from "./idl";

/**
 * Build an Anchor Program instance for ShieldMesh.
 * Requires a connected wallet (AnchorWallet) to sign transactions.
 */
export function getProgram(connection: Connection, wallet: AnchorWallet) {
  const provider = new AnchorProvider(connection, wallet, {
    commitment: "confirmed",
  });
  return new Program(SHIELDMESH_IDL as any, provider);
}

/**
 * Derive the staking pool PDA for a given authority.
 */
export function getPoolPDA(authority: PublicKey): [PublicKey, number] {
  return PublicKey.findProgramAddressSync(
    [Buffer.from("staking_pool"), authority.toBuffer()],
    SHIELDMESH_PROGRAM_ID,
  );
}

/**
 * Derive the staker account PDA.
 */
export function getStakerPDA(
  pool: PublicKey,
  staker: PublicKey,
): [PublicKey, number] {
  return PublicKey.findProgramAddressSync(
    [Buffer.from("staker"), pool.toBuffer(), staker.toBuffer()],
    SHIELDMESH_PROGRAM_ID,
  );
}

/**
 * Derive the threat account PDA.
 */
export function getThreatPDA(
  pool: PublicKey,
  threatHash: Uint8Array,
): [PublicKey, number] {
  return PublicKey.findProgramAddressSync(
    [Buffer.from("threat"), pool.toBuffer(), Buffer.from(threatHash)],
    SHIELDMESH_PROGRAM_ID,
  );
}

/**
 * Derive the bounty account PDA.
 */
export function getBountyPDA(threat: PublicKey): [PublicKey, number] {
  return PublicKey.findProgramAddressSync(
    [Buffer.from("bounty"), threat.toBuffer()],
    SHIELDMESH_PROGRAM_ID,
  );
}
