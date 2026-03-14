use anchor_lang::prelude::*;

#[account]
#[derive(InitSpace)]
pub struct StakerAccount {
    pub owner: Pubkey,
    pub pool: Pubkey,
    pub amount: u64,
    pub reputation: u32,
    pub bump: u8,
}
