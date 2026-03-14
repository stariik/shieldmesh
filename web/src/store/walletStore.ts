import { create } from "zustand";

interface WalletState {
  connected: boolean;
  publicKey: string | null;
  balance: number;
  balanceLoading: boolean;
  stakedAmount: number;
  earnedBounties: number;
  setConnected: (connected: boolean) => void;
  setPublicKey: (publicKey: string | null) => void;
  setBalance: (balance: number) => void;
  setBalanceLoading: (loading: boolean) => void;
  setStakedAmount: (amount: number) => void;
  setEarnedBounties: (amount: number) => void;
  reset: () => void;
}

export const useWalletStore = create<WalletState>((set) => ({
  connected: false,
  publicKey: null,
  balance: 0,
  balanceLoading: false,
  stakedAmount: 0,
  earnedBounties: 0,
  setConnected: (connected) => set({ connected }),
  setPublicKey: (publicKey) => set({ publicKey }),
  setBalance: (balance) => set({ balance }),
  setBalanceLoading: (loading) => set({ balanceLoading: loading }),
  setStakedAmount: (amount) => set({ stakedAmount: amount }),
  setEarnedBounties: (amount) => set({ earnedBounties: amount }),
  reset: () =>
    set({
      connected: false,
      publicKey: null,
      balance: 0,
      balanceLoading: false,
      stakedAmount: 0,
      earnedBounties: 0,
    }),
}));
