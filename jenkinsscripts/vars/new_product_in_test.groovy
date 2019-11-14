#!/usr/bin/groovy

def call (config = [:]){

   config = config as NewProductConfigTest
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
           \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname "${config.apiManagementRG}" -ServiceName "${config.apiManagementName}";
           New-AzApiManagementProduct -Context \$ApiMgmtcontext -ProductId "${config.productId}" -Title "${config.productTitle}" -Description "${config.productDescription}" -State Published -SubscriptionRequired \$True -ApprovalRequired \$True;
           \$DatabasePassword = (Get-AzKeyVaultSecret -vaultName "KV20APIM100-TA" -name "sw20apiman-t-LoginPassword").SecretValueText;
           Invoke-Sqlcmd -Server "sw20apiman-t.database.windows.net" -Database "db20-product-eigenaren-t" -Password \$DatabasePassword -Username "ApiDBadmin" -Query "INSERT INTO dbo.Product_Eigenaren (ProductId, Productnaam, EigenaarNaam, EigenaarEmail) VALUES ('${config.productId}', '${config.productTitle}', '${config.productOwner}', '${config.productContact}')";
         """
      }
   }
}
