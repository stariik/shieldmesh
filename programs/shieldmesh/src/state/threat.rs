use anchor_lang::prelude::*;

#[account]
#[derive(InitSpace)]
pub struct ThreatAccount {
    pub reporter: Pubkey,
    pub threat_hash: [u8; 32],
    pub severity: u8,
    pub ai_score: u8,
    pub validator_count: u8,
    /// 0 = PENDING, 1 = VERIFIED, 2 = SETTLED
    pub status: u8,
    pub timestamp: i64,
    pub bump: u8,
}
