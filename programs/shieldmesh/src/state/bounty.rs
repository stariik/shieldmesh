use anchor_lang::prelude::*;

#[account]
#[derive(InitSpace)]
pub struct BountyAccount {
    pub reporter: Pubkey,
    pub threat: Pubkey,
    pub amount: u64,
    pub claimed: bool,
    pub bump: u8,
}
