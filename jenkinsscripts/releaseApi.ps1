param([string]$SubscriptionId, [string]$ResourceGroupName, [string]$ServiceName, [string]$ApiId, [string]$Version)

$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${ResourceGroupName} -ServiceName ${ServiceName}
Set-AzContext -SubscriptionId $SubscriptionId

New-AzApiManagementApiRelease -Context $ApiMgmtContext `
    -ApiId $ApiId `
    -ApiRevision $Version `
