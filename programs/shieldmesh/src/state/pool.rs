use anchor_lang::prelude::*;

#[account]
#[derive(InitSpace)]
pub struct StakingPool {
    pub authority: Pubkey,
    pub total_staked: u64,
    pub staker_count: u32,
    /// Reward rates in lamports for severity levels: [LOW, MEDIUM, HIGH, CRITICAL]
    pub reward_rates: [u64; 4],
    pub bump: u8,
}
