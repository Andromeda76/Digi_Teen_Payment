# Digiteen Payment Service

## Overview

Payment service responsible for wallet creation and wallet transactions,
including deposits, withdrawals, and transfers.

## Authentication

The service uses JWT authentication. Wallet ownership is determined from
the authenticated user's email.

## Concurrency Control

Wallet operations use database-level pessimistic locking (`PESSIMISTIC_WRITE`)
to prevent concurrent operations from producing an incorrect balance.

## Concurrent Withdrawal Test

The test executes 50 withdrawal requests concurrently against a wallet
with an initial balance of 100,000.

Each withdrawal attempts to withdraw 3,000.

Expected result:

- 33 successful withdrawals
- 17 failed withdrawals due to insufficient balance
- Final balance: 1,000
- Balance never becomes negative

Run:

```powershell
.\scripts\concurrency-withdrawal.ps1
