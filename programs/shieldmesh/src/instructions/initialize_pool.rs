use anchor_lang::prelude::*;
use crate::state::StakingPool;

#[derive(Accounts)]
pub struct InitializePool<'info> {
    #[account(mut)]
    pub authority: Signer<'info>,

    #[account(
        init,
        payer = authority,
        space = 8 + StakingPool::INIT_SPACE,
        seeds = [b"staking_pool", authority.key().as_ref()],
        bump,
    )]
    pub pool: Account<'info, StakingPool>,

    pub system_program: Program<'info, System>,
}

pub fn initialize_pool_handler(ctx: Context<InitializePool>, reward_rates: [u64; 4]) -> Result<()> {
    let pool = &mut ctx.accounts.pool;
    pool.authority = ctx.accounts.authority.key();
    pool.total_staked = 0;
    pool.staker_count = 0;
    pool.reward_rates = reward_rates;
    pool.bump = ctx.bumps.pool;

    msg!("Staking pool initialized by {}", pool.authority);
    Ok(())
}
