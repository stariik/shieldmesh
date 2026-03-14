use anchor_lang::prelude::*;
use crate::state::{StakingPool, ThreatAccount, StakerAccount};
use crate::errors::ShieldMeshError;

#[derive(Accounts)]
pub struct VerifyThreat<'info> {
    #[account(mut)]
    pub validator: Signer<'info>,

    #[account(
        seeds = [b"staking_pool", pool.authority.as_ref()],
        bump = pool.bump,
    )]
    pub pool: Account<'info, StakingPool>,

    #[account(
        mut,
        seeds = [b"threat", pool.key().as_ref(), threat.threat_hash.as_ref()],
        bump = threat.bump,
    )]
    pub threat: Account<'info, ThreatAccount>,

    #[account(
        mut,
        seeds = [b"staker", pool.key().as_ref(), validator.key().as_ref()],
        bump = staker_account.bump,
        constraint = staker_account.owner == validator.key(),
        constraint = staker_account.amount > 0 @ ShieldMeshError::InsufficientStake,
    )]
    pub staker_account: Account<'info, StakerAccount>,
}

pub fn verify_threat_handler(ctx: Context<VerifyThreat>) -> Result<()> {
    let threat = &mut ctx.accounts.threat;

    require!(
        ctx.accounts.validator.key() != threat.reporter,
        ShieldMeshError::SelfVerification
    );
    require!(threat.status == 0, ShieldMeshError::ThreatAlreadySettled);

    threat.validator_count = threat.validator_count.checked_add(1).unwrap();

    // Auto-verify once we reach the minimum validator threshold
    if threat.validator_count >= 2 {
        threat.status = 1; // VERIFIED
    }

    // Reward the validator with reputation
    let staker_account = &mut ctx.accounts.staker_account;
    staker_account.reputation = staker_account.reputation.checked_add(1).unwrap();

    msg!(
        "Threat verified by {} | validators={} status={}",
        ctx.accounts.validator.key(),
        threat.validator_count,
        threat.status,
    );
    Ok(())
}
