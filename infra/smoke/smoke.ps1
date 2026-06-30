param(
    [string]$GatewayBaseUrl = "http://localhost:48080",
    [string]$Username = "admin",
    [string]$Password = "admin123"
)

$ErrorActionPreference = "Stop"

function Assert-OkResult {
    param(
        [Parameter(Mandatory = $true)] $Result,
        [Parameter(Mandatory = $true)] [string] $Step
    )
    if ($null -eq $Result.code) {
        throw "$Step did not return CommonResult JSON"
    }
    if ([int]$Result.code -ne 0) {
        throw "$Step failed: code=$($Result.code), msg=$($Result.msg)"
    }
}

function Invoke-Health {
    param([string] $Path)
    $uri = "$GatewayBaseUrl$Path"
    Write-Host "GET $uri"
    Invoke-RestMethod -Method Get -Uri $uri | Out-Null
}

Write-Host "Checking gateway health..."
Invoke-Health "/actuator/health"

Write-Host "Logging in through gateway..."
$loginBody = @{
    username = $Username
    password = $Password
} | ConvertTo-Json
$login = Invoke-RestMethod -Method Post `
    -Uri "$GatewayBaseUrl/admin-api/system/auth/login" `
    -ContentType "application/json" `
    -Body $loginBody
Assert-OkResult $login "login"

$accessToken = $login.data.accessToken
if ([string]::IsNullOrWhiteSpace($accessToken)) {
    throw "login did not return accessToken"
}
$headers = @{
    Authorization = "Bearer $accessToken"
}

Write-Host "Uploading a small file through gateway..."
$tempFile = New-TemporaryFile
Set-Content -LiteralPath $tempFile.FullName -Value "diary microservice smoke $(Get-Date -Format o)" -Encoding UTF8
try {
    $upload = Invoke-RestMethod -Method Post `
        -Uri "$GatewayBaseUrl/admin-api/file/upload" `
        -Headers $headers `
        -Form @{
            file = Get-Item -LiteralPath $tempFile.FullName
            businessType = "smoke"
            businessId = "smoke-$(Get-Date -Format yyyyMMddHHmmss)"
        }
    Assert-OkResult $upload "file upload"
    $fileId = $upload.data.id
    if ($null -eq $fileId) {
        throw "file upload did not return file id"
    }

    Write-Host "Querying diary list through gateway..."
    $diaryPage = Invoke-RestMethod -Method Get `
        -Uri "$GatewayBaseUrl/admin-api/diary/page?pageNo=1&pageSize=10" `
        -Headers $headers
    Assert-OkResult $diaryPage "diary page"

    Write-Host "Querying blog list through gateway..."
    $blogPage = Invoke-RestMethod -Method Get `
        -Uri "$GatewayBaseUrl/admin-api/blog/page?pageNo=1&pageSize=10" `
        -Headers $headers
    Assert-OkResult $blogPage "blog page"

    Write-Host "Smoke passed. fileId=$fileId"
} finally {
    Remove-Item -LiteralPath $tempFile.FullName -Force -ErrorAction SilentlyContinue
}
