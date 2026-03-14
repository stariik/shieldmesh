use anchor_lang::prelude::*;

pub mod errors;
pub mod instructions;
pub mod state;

use instructions::*;

declare_id!("DKcRE94UZtL18AVrDMJHviw5pUHp5L9xr11Q1njvUmvK");

#[program]
pub mod shieldmesh {
    use super::*;

    pub fn initialize_pool(ctx: Context<InitializePool>, reward_rates: [u64; 4]) -> Result<()> {
        initialize_pool_handler(ctx, reward_rates)
    }

    pub fn stake(ctx: Context<Stake>, amount: u64) -> Result<()> {
        stake_handler(ctx, amount)
    }

    pub fn unstake(ctx: Context<Unstake>, amount: u64) -> Result<()> {
        unstake_handler(ctx, amount)
    }

    pub fn report_threat(
        ctx: Context<ReportThreat>,
        threat_hash: [u8; 32],
        severity: u8,
        ai_score: u8,
    ) -> Result<()> {
        report_threat_handler(ctx, threat_hash, severity, ai_score)
    }

    pub fn verify_threat(ctx: Context<VerifyThreat>) -> Result<()> {
        verify_threat_handler(ctx)
    }

    pub fn claim_bounty(ctx: Context<ClaimBounty>) -> Result<()> {
        claim_bounty_handler(ctx)
    }
}
