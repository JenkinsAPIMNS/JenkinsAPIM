param([string]$SubscriptionId, [string]$ResourceGroupName, [string]$ServiceName, [string]$ProductId, [string]$ApiId)

$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${ResourceGroupName} -ServiceName ${ServiceName}

Set-AzContext -SubscriptionId $SubscriptionId
Add-AzApiManagementApiToProduct -Context $ApiMgmtContext `
    -ProductId ${ProductId} `
    -ApiId ${ApiId}
