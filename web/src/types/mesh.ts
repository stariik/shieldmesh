export interface MeshPeer {
  id: string;
  lastSeen: number;
  relayedCount: number;
}

export interface MeshStatus {
  peers: MeshPeer[];
  connectedCount: number;
  totalRelayed: number;
}
