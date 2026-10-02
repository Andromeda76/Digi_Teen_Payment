# ============================================
# Digital Wallet - Duplicate Transfer Test
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
    Write-Host ".\scripts\duplicate-transfer-test.ps1"
    exit 1
}

# ============================================
# Configuration
# ============================================

$numberOfRequests = 5
$withdrawalAmount = 3000

$headers = @{
    Authorization = "Bearer $jwt"
    "Content-Type" = "application/json"
}

# ============================================
# Step 1 - Create ONE withdrawal transaction
# ============================================

Write-Host ""
Write-Host "============================================"
Write-Host " Creating withdrawal transaction"
Write-Host "============================================"

$body = @{
    amount = $withdrawalAmount
    transferType = "WITHDRAW"
    transferStatus = "CREATED"
} | ConvertTo-Json

try {

    $transaction = Invoke-RestMethod `
        -Uri "$baseUrl/transfer/create_transaction" `
        -Method Post `
        -Headers $headers `
        -Body $body `
        -ErrorAction Stop

    $requestId = $transaction.requestId

    if ([string]::IsNullOrWhiteSpace($requestId)) {
        Write-Host "FAIL: Transaction did not return requestId." -ForegroundColor Red
        exit 1
    }

    Write-Host "Transaction created successfully." -ForegroundColor Green
    Write-Host "RequestId: $requestId"

}
catch {

    Write-Host ""
    Write-Host "FAIL: Could not create withdrawal transaction." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# ============================================
# Step 2 - Execute SAME request concurrently
# ============================================

Write-Host ""
Write-Host "============================================"
Write-Host " Executing duplicate requests concurrently"
Write-Host "============================================"

Write-Host ""
Write-Host "Same RequestId will be executed $numberOfRequests times:"
Write-Host $requestId

$jobs = @()

for ($i = 1; $i -le $numberOfRequests; $i++) {

    $jobs += Start-Job -ScriptBlock {

        param(
            $baseUrl,
            $jwt,
            $requestId,
            $number
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
                Number    = $number
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
                Number    = $number
                RequestId = $requestId
                Success   = $false
                Status    = $status
                Body      = $body
                Error     = $_.Exception.Message
            }
        }

    } -ArgumentList $baseUrl, $jwt, $requestId, $i
}

Write-Host ""
Write-Host "$numberOfRequests duplicate requests have been started."
Write-Host "Waiting for all requests to finish..." -ForegroundColor Yellow

# ============================================
# Step 3 - Wait and collect results
# ============================================

$jobs | Wait-Job | Out-Null

$results = @()

foreach ($job in $jobs) {

    $jobResult = Receive-Job $job

    if ($jobResult) {
        $results += $jobResult
    }
}

# Print results before cleanup
Write-Host ""
Write-Host "============================================"
Write-Host " Response Details"
Write-Host "============================================"

foreach ($result in $results) {

    Write-Host ""
    Write-Host "Request : $($result.Number)"
    Write-Host "RequestId: $($result.RequestId)"
    Write-Host "HTTP     : $($result.Status)"
    Write-Host "Success  : $($result.Success)"
    Write-Host "Body     : $($result.Body)"

    if ($result.Error) {
        Write-Host "Error    : $($result.Error)" -ForegroundColor Red
    }
}

# Cleanup
$jobs | Remove-Job -Force

# ============================================
# Step 4 - Analyze results
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
Write-Host "HTTP successful: $($successful.Count)"
Write-Host "HTTP failed    : $($failed.Count)"

# ============================================
# Step 5 - Validate request execution
# ============================================

$testPassed = $true

Write-Host ""
Write-Host "============================================"
Write-Host " Validation"
Write-Host "============================================"

if ($totalRequests -eq $numberOfRequests) {

    Write-Host "PASS: All $numberOfRequests requests were executed." -ForegroundColor Green

}
else {

    Write-Host "FAIL: Expected $numberOfRequests requests, got $totalRequests." -ForegroundColor Red
    $testPassed = $false
}

# ============================================
# Step 6 - Validate identical request IDs
# ============================================

$uniqueRequestIds = @(
    $results |
        Select-Object -ExpandProperty RequestId -Unique
)

if ($uniqueRequestIds.Count -eq 1 -and
    $uniqueRequestIds[0] -eq $requestId) {

    Write-Host "PASS: All requests used the same requestId." -ForegroundColor Green

}
else {

    Write-Host "FAIL: Requests did not use the same requestId." -ForegroundColor Red
    $testPassed = $false
}

# ============================================
# Final result
# ============================================

Write-Host ""
Write-Host "============================================"

if ($testPassed) {

    Write-Host " RESULT: REQUEST EXECUTION PASS" -ForegroundColor Green
    Write-Host "============================================"

    Write-Host ""
    Write-Host "NOTE:"
    Write-Host "This test confirms that the same requestId was"
    Write-Host "executed concurrently $numberOfRequests times."
    Write-Host ""
    Write-Host "The final financial-effect validation should"
    Write-Host "confirm that only ONE withdrawal was applied."

    exit 0

}
else {

    Write-Host " RESULT: FAIL" -ForegroundColor Red
    Write-Host "============================================"

    exit 1
}