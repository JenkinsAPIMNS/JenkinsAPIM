#!/usr/bin/groovy

def call (config = [:]){

   config = config as UpdateProductConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()
   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname ${config.apiManagementRG} -ServiceName ${config.apiManagementName};
            Set-AzApiManagementProduct -Context \$ApiMgmtContext -ProductId "${config.productId}" -Title "${config.productTitle}" -Description "${config.productDescription}" -LegalTerms "${config.termsConditions}" -SubscriptionRequired \$True -ApprovalRequired \$True -State \$ProductStatus
            \$DatabasePassword = (Get-AzKeyVaultSecret -vaultName ${config.vaultName} -name ${option.vaultSecret}).SecretValueText
            Invoke-Sqlcmd -Server ${config.servername} -Database ${config.databaseName} -password \$databasePassword -Username ${config.databaseUsername} -Query \"UPDATE dbo.Product_Eigenaren SET EigenaarNaam = '\$ProductOwner' , EigenaarEmail = '\$ProductContact' , ProductNaam = '${config.productTitle}' WHERE ProductID = '${config.productID}'\"
         """
      }
   }
}  