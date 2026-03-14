use anchor_lang::prelude::*;
use crate::state::{StakingPool, ThreatAccount, BountyAccount};
use crate::errors::ShieldMeshError;

#[derive(Accounts)]
pub struct ClaimBounty<'info> {
    #[account(mut)]
    pub reporter: Signer<'info>,

    #[account(
        mut,
        seeds = [b"staking_pool", pool.authority.as_ref()],
        bump = pool.bump,
    )]
    pub pool: Account<'info, StakingPool>,

    #[account(
        mut,
        seeds = [b"threat", pool.key().as_ref(), threat.threat_hash.as_ref()],
        bump = threat.bump,
        constraint = threat.reporter == reporter.key() @ ShieldMeshError::Unauthorized,
        constraint = threat.status == 1 @ ShieldMeshError::ThreatNotVerified,
    )]
    pub threat: Account<'info, ThreatAccount>,

    #[account(
        mut,
        seeds = [b"bounty", threat.key().as_ref()],
        bump = bounty.bump,
        constraint = bounty.reporter == reporter.key() @ ShieldMeshError::Unauthorized,
        constraint = !bounty.claimed @ ShieldMeshError::BountyAlreadyClaimed,
    )]
    pub bounty: Account<'info, BountyAccount>,

    pub system_program: Program<'info, System>,
}

pub fn claim_bounty_handler(ctx: Context<ClaimBounty>) -> Result<()> {
    let threat = &mut ctx.accounts.threat;
    require!(
        threat.validator_count >= 2,
        ShieldMeshError::InsufficientValidators
    );

    let bounty = &mut ctx.accounts.bounty;
    let payout = bounty.amount;

    // Ensure pool has enough lamports beyond rent-exempt minimum
    let pool = &mut ctx.accounts.pool;
    let pool_lamports = pool.to_account_info().lamports();
    let rent = Rent::get()?;
    let min_rent = rent.minimum_balance(pool.to_account_info().data_len());
    require!(
        pool_lamports.saturating_sub(min_rent) >= payout,
        ShieldMeshError::InsufficientPoolBalance
    );

    // Transfer lamports from pool PDA to reporter
    **pool.to_account_info().try_borrow_mut_lamports()? -= payout;
    **ctx.accounts.reporter.to_account_info().try_borrow_mut_lamports()? += payout;

    bounty.claimed = true;
    threat.status = 2; // SETTLED

    // Reduce total_staked to reflect the payout
    pool.total_staked = pool.total_staked.saturating_sub(payout);

    msg!(
        "Bounty of {} lamports claimed by {} for threat {}",
        payout,
        ctx.accounts.reporter.key(),
        ctx.accounts.threat.key(),
    );
    Ok(())
}
