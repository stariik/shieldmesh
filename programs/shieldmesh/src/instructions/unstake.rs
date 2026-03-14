use anchor_lang::prelude::*;
use crate::state::{StakingPool, StakerAccount};
use crate::errors::ShieldMeshError;

#[derive(Accounts)]
pub struct Unstake<'info> {
    #[account(mut)]
    pub staker: Signer<'info>,

    #[account(
        mut,
        seeds = [b"staking_pool", pool.authority.as_ref()],
        bump = pool.bump,
    )]
    pub pool: Account<'info, StakingPool>,

    #[account(
        mut,
        seeds = [b"staker", pool.key().as_ref(), staker.key().as_ref()],
        bump = staker_account.bump,
        constraint = staker_account.owner == staker.key(),
    )]
    pub staker_account: Account<'info, StakerAccount>,

    pub system_program: Program<'info, System>,
}

pub fn unstake_handler(ctx: Context<Unstake>, amount: u64) -> Result<()> {
    let staker_account = &mut ctx.accounts.staker_account;
    require!(amount > 0 && amount <= staker_account.amount, ShieldMeshError::UnstakeExceedsBalance);

    // Transfer SOL from pool PDA back to staker
    let pool = &mut ctx.accounts.pool;
    let pool_lamports = pool.to_account_info().lamports();
    let rent = Rent::get()?;
    let min_rent = rent.minimum_balance(pool.to_account_info().data_len());
    require!(
        pool_lamports.saturating_sub(min_rent) >= amount,
        ShieldMeshError::InsufficientPoolBalance
    );

    **pool.to_account_info().try_borrow_mut_lamports()? -= amount;
    **ctx.accounts.staker.to_account_info().try_borrow_mut_lamports()? += amount;

    staker_account.amount = staker_account.amount.checked_sub(amount).unwrap();
    pool.total_staked = pool.total_staked.checked_sub(amount).unwrap();

    if staker_account.amount == 0 {
        pool.staker_count = pool.staker_count.checked_sub(1).unwrap();
    }

    msg!("{} unstaked {} lamports", staker_account.owner, amount);
    Ok(())
}
