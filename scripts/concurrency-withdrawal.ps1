# ============================================
# Digital Wallet - Concurrent Withdrawal Test
# PowerShell 5.1 compatible
# ============================================

$baseUrl = "http://localhost:8082"

# JWT must be provided through the environment
$jwt = $env:JWT

if ([string]::IsNullOrWhiteSpace($jwt)) {
    Write-Host "ERROR: JWT environment variable is not set." -ForegroundColor Red
    Write-Host ""
    Write-Host "Run:"
    Write-Host '$env:JWT = "<your JWT>"'
    Write-Host ".\scripts\concurrency-withdrawal.ps1"
    exit 1
}

# ============================================
# Configuration
# ============================================

$numberOfWithdrawals = 50
$withdrawalAmount = 3000

$headers = @{
    Authorization = "Bearer $jwt"
    "Content-Type" = "application/json"
}

# ============================================
# Step 1 - Create withdrawal transactions
# ============================================

Write-Host ""
Write-Host "============================================"
Write-Host " Creating withdrawal transactions"
Write-Host "============================================"

$transactions = @()

for ($i = 1; $i -le $numberOfWithdrawals; $i++) {

    $body = @{
        amount = $withdrawalAmount
        transferType = "WITHDRAW"
        transferStatus = "CREATED"
    } | ConvertTo-Json

    try {

        $response = Invoke-RestMethod `
            -Uri "$baseUrl/transfer/create_transaction" `
            -Method Post `
            -Headers $headers `
            -Body $body `
            -ErrorAction Stop

        if ([string]::IsNullOrWhiteSpace($response.requestId)) {
            Write-Host "FAIL: Transaction $i did not return requestId." -ForegroundColor Red
            exit 1
        }

        $transactions += [PSCustomObject]@{
            Number    = $i
            RequestId = $response.requestId
        }

        Write-Host "Created transaction $i / $numberOfWithdrawals"

    }
    catch {

        Write-Host ""
        Write-Host "FAIL: Could not create transaction $i" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "Created $($transactions.Count) transactions." -ForegroundColor Green

# ============================================
# Step 2 - Execute all withdrawals concurrently
# ============================================

Write-Host ""
Write-Host "============================================"
Write-Host " Executing withdrawals concurrently"
Write-Host "============================================"

$jobs = @()

foreach ($transaction in $transactions) {

    $requestId = $transaction.RequestId

    $jobs += Start-Job -ScriptBlock {

        param(
            $baseUrl,
            $jwt,
            $requestId
        )

        $jobHeaders = @{
            Authorization = "Bearer $jwt"
        }

        try {

            $response = Invoke-WebRequest `
                -Uri "$baseUrl/transfer/$requestId" `
                -Method Put `
                -Headers $jobHeaders `
                -UseBasicParsing `
                -ErrorAction Stop

            [PSCustomObject]@{
                RequestId = $requestId
                Success   = $true
                Status    = [int]$response.StatusCode
                Body      = $response.Content
                Error     = $null
            }

        }
        catch {

            $status = $null
            $body = $null

            if ($_.Exception.Response) {

                try {
                    $status = [int]$_.Exception.Response.StatusCode
                }
                catch {
                    $status = $null
                }

                try {

                    $stream = $_.Exception.Response.GetResponseStream()

                    if ($stream) {
                        $reader = New-Object System.IO.StreamReader($stream)
                        $body = $reader.ReadToEnd()
                        $reader.Close()
                    }

                }
                catch {
                    $body = $_.Exception.Message
                }
            }

            [PSCustomObject]@{
                RequestId = $requestId
                Success   = $false
                Status    = $status
                Body      = $body
                Error     = $_.Exception.Message
            }
        }

    } -ArgumentList $baseUrl, $jwt, $requestId
}

Write-Host ""
Write-Host "50 requests have been started."
Write-Host "Waiting for all requests to finish..." -ForegroundColor Yellow

# Wait for all jobs
$jobs | Wait-Job | Out-Null

# Collect results
$results = @()

foreach ($job in $jobs) {

    $jobResult = Receive-Job $job

    if ($jobResult) {
        $results += $jobResult
    }
}

# Print results BEFORE cleanup
Write-Host ""
Write-Host "============================================"
Write-Host " RESPONSE DETAILS"
Write-Host "============================================"

foreach ($result in $results) {
    Write-Host ""
    Write-Host "RequestId: $($result.RequestId)"
    Write-Host "HTTP:      $($result.Status)"
    Write-Host "Success:   $($result.Success)"
    Write-Host "Body:      $($result.Body)"
}

# Cleanup jobs
$jobs | Remove-Job -Force

# ============================================
# Step 3 - Analyze results
# ============================================

Write-Host ""
Write-Host "============================================"
Write-Host " Execution Results"
Write-Host "============================================"

$totalRequests = $results.Count

$successful = @(
    $results | Where-Object {
        $_.Success -eq $true
    }
)

$failed = @(
    $results | Where-Object {
        $_.Success -eq $false
    }
)

Write-Host "Total requests : $totalRequests"
Write-Host "Successful     : $($successful.Count)"
Write-Host "Failed         : $($failed.Count)"

# ============================================
# Step 4 - Print failed requests
# ============================================

if ($failed.Count -gt 0) {

    Write-Host ""
    Write-Host "Failed requests:" -ForegroundColor Yellow

    foreach ($failure in $failed) {

        Write-Host ""
        Write-Host "RequestId: $($failure.RequestId)"
        Write-Host "HTTP:     $($failure.Status)"
        Write-Host "Body:     $($failure.Body)"
        Write-Host "Error:    $($failure.Error)"
    }
}

# ============================================
# Step 5 - Validate expected result
# ============================================

$testPassed = $true

Write-Host ""
Write-Host "============================================"
Write-Host " Validation"
Write-Host "============================================"

# Exactly 50 requests
if ($totalRequests -eq 50) {

    Write-Host "PASS: 50 requests were executed." -ForegroundColor Green

}
else {

    Write-Host "FAIL: Expected 50 requests, got $totalRequests." -ForegroundColor Red
    $testPassed = $false
}

# Exactly 33 successful withdrawals
if ($successful.Count -eq 33) {

    Write-Host "PASS: Exactly 33 withdrawals succeeded." -ForegroundColor Green

}
else {

    Write-Host "FAIL: Expected 33 successful withdrawals, got $($successful.Count)." -ForegroundColor Red
    $testPassed = $false
}

# Exactly 17 failed withdrawals
if ($failed.Count -eq 17) {

    Write-Host "PASS: Exactly 17 withdrawals failed." -ForegroundColor Green

}
else {

    Write-Host "FAIL: Expected 17 failed withdrawals, got $($failed.Count)." -ForegroundColor Red
    $testPassed = $false
}

# ============================================
# Final result
# ============================================

Write-Host ""
Write-Host "============================================"

if ($testPassed) {

    Write-Host " RESULT: PASS" -ForegroundColor Green
    Write-Host "============================================"

    exit 0

}
else {

    Write-Host " RESULT: FAIL" -ForegroundColor Red
    Write-Host "============================================"

    exit 1
}