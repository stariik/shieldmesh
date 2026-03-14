use anchor_lang::prelude::*;
use anchor_lang::system_program;
use crate::state::{StakingPool, StakerAccount};
use crate::errors::ShieldMeshError;

#[derive(Accounts)]
pub struct Stake<'info> {
    #[account(mut)]
    pub staker: Signer<'info>,

    #[account(
        mut,
        seeds = [b"staking_pool", pool.authority.as_ref()],
        bump = pool.bump,
    )]
    pub pool: Account<'info, StakingPool>,

    #[account(
        init_if_needed,
        payer = staker,
        space = 8 + StakerAccount::INIT_SPACE,
        seeds = [b"staker", pool.key().as_ref(), staker.key().as_ref()],
        bump,
    )]
    pub staker_account: Account<'info, StakerAccount>,

    pub system_program: Program<'info, System>,
}

pub fn stake_handler(ctx: Context<Stake>, amount: u64) -> Result<()> {
    require!(amount > 0, ShieldMeshError::InsufficientStake);

    // Transfer SOL from staker to pool PDA
    system_program::transfer(
        CpiContext::new(
            ctx.accounts.system_program.to_account_info(),
            system_program::Transfer {
                from: ctx.accounts.staker.to_account_info(),
                to: ctx.accounts.pool.to_account_info(),
            },
        ),
        amount,
    )?;

    let staker_account = &mut ctx.accounts.staker_account;
    let is_new_staker = staker_account.amount == 0 && staker_account.owner == Pubkey::default();

    staker_account.owner = ctx.accounts.staker.key();
    staker_account.pool = ctx.accounts.pool.key();
    staker_account.amount = staker_account.amount.checked_add(amount).unwrap();
    staker_account.bump = ctx.bumps.staker_account;

    let pool = &mut ctx.accounts.pool;
    pool.total_staked = pool.total_staked.checked_add(amount).unwrap();
    if is_new_staker {
        pool.staker_count = pool.staker_count.checked_add(1).unwrap();
    }

    msg!("{} staked {} lamports", staker_account.owner, amount);
    Ok(())
}
