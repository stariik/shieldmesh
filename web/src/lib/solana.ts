import { Connection } from "@solana/web3.js";
import { DEVNET_URL } from "./constants";

let connection: Connection | null = null;

export function getConnection(): Connection {
  if (!connection) {
    connection = new Connection(DEVNET_URL, "confirmed");
  }
  return connection;
}
