use anchor_lang::prelude::*;
use crate::state::{StakingPool, ThreatAccount, BountyAccount};
use crate::errors::ShieldMeshError;

#[derive(Accounts)]
#[instruction(threat_hash: [u8; 32])]
pub struct ReportThreat<'info> {
    #[account(mut)]
    pub reporter: Signer<'info>,

    #[account(
        seeds = [b"staking_pool", pool.authority.as_ref()],
        bump = pool.bump,
    )]
    pub pool: Account<'info, StakingPool>,

    #[account(
        init,
        payer = reporter,
        space = 8 + ThreatAccount::INIT_SPACE,
        seeds = [b"threat", pool.key().as_ref(), threat_hash.as_ref()],
        bump,
    )]
    pub threat: Account<'info, ThreatAccount>,

    #[account(
        init,
        payer = reporter,
        space = 8 + BountyAccount::INIT_SPACE,
        seeds = [b"bounty", threat.key().as_ref()],
        bump,
    )]
    pub bounty: Account<'info, BountyAccount>,

    pub system_program: Program<'info, System>,
}

pub fn report_threat_handler(
    ctx: Context<ReportThreat>,
    threat_hash: [u8; 32],
    severity: u8,
    ai_score: u8,
) -> Result<()> {
    require!(severity <= 3, ShieldMeshError::InvalidSeverity);
    require!(ai_score <= 100, ShieldMeshError::InvalidAiScore);

    let clock = Clock::get()?;
    let pool = &ctx.accounts.pool;

    let reporter_key = ctx.accounts.reporter.key();
    let threat_key = ctx.accounts.threat.key();
    let bounty_amount = pool.reward_rates[severity as usize];

    let threat = &mut ctx.accounts.threat;
    threat.reporter = reporter_key;
    threat.threat_hash = threat_hash;
    threat.severity = severity;
    threat.ai_score = ai_score;
    threat.validator_count = 0;
    threat.status = 0; // PENDING
    threat.timestamp = clock.unix_timestamp;
    threat.bump = ctx.bumps.threat;

    let bounty = &mut ctx.accounts.bounty;
    bounty.reporter = reporter_key;
    bounty.threat = threat_key;
    bounty.amount = bounty_amount;
    bounty.claimed = false;
    bounty.bump = ctx.bumps.bounty;

    msg!(
        "Threat reported by {} | severity={} ai_score={} bounty={}",
        reporter_key,
        severity,
        ai_score,
        bounty_amount,
    );
    Ok(())
}
