param([string]$SubscriptionId, [string]$ProductId, [string]$Title, [string]$State, [string]$ResourceGroupName, [string]$ServiceName)

$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${ResourceGroupName} -ServiceName ${ServiceName}

Set-AzContext -SubscriptionId $SubscriptionId
New-AzApiManagementProduct -Context $ApiMgmtContext `
    -ProductId $ProductId `
    -Title $Title `
    -State $State
