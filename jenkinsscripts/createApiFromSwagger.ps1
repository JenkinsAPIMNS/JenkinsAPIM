param([string]$SubscriptionId, [string]$ResourceGroupName, [string]$ServiceName, [string]$SwaggerPath, [string]$Path, [string]$ApiId)

$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${ResourceGroupName} -ServiceName ${ServiceName}

Set-AzContext -SubscriptionId $SubscriptionId
Import-AzApiManagementApi -Context $ApiMgmtContext `
    -SpecificationFormat "Swagger" `
    -SpecificationPath $SwaggerPath `
    -Path $Path `
    -ApiId $ApiId
