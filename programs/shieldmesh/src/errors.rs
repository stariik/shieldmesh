use anchor_lang::prelude::*;

#[error_code]
pub enum ShieldMeshError {
    #[msg("Invalid severity level. Must be 0-3.")]
    InvalidSeverity,

    #[msg("Invalid AI score. Must be 0-100.")]
    InvalidAiScore,

    #[msg("Insufficient stake amount.")]
    InsufficientStake,

    #[msg("Threat has not been verified by enough validators.")]
    InsufficientValidators,

    #[msg("Bounty has already been claimed.")]
    BountyAlreadyClaimed,

    #[msg("Threat is not in verified status.")]
    ThreatNotVerified,

    #[msg("Reporter cannot verify their own threat.")]
    SelfVerification,

    #[msg("Unstake amount exceeds staked balance.")]
    UnstakeExceedsBalance,

    #[msg("Insufficient pool balance for bounty payout.")]
    InsufficientPoolBalance,

    #[msg("Threat has already been settled.")]
    ThreatAlreadySettled,

    #[msg("Only the pool authority can perform this action.")]
    Unauthorized,
}
