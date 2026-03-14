import { PublicKey } from "@solana/web3.js";
import idl from "./shieldmesh.json";

export const SHIELDMESH_IDL = idl;
export const SHIELDMESH_PROGRAM_ID = new PublicKey(idl.address);
