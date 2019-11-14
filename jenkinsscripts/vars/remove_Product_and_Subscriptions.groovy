#!/usr/bin/groovy

def call (config = [:]){

   config = config as RemoveProductConfig
   def environment = config.environment as DeploymentAPIMEnvironment
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()
   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname ${environment.apiManagementRG} -ServiceName ${environment.apiManagementName}
            Remove-AzApiManagementProduct -Context \$ApiMgmtContext -ProductId "${productId}" -DeleteSubscriptions
            \$DatabasePassword = (Get-AzKeyVaultSecret -vaultName ${config.environment.vaultName} -name ${config.environment.vaultSecret}).SecretValueText
            Invoke-Sqlcmd -Server ${config.environment.serverName} -Database ${config.environment.databaseName} -password \$DatabasePassword -Username ${config.environment.databaseUsername} -Query \"DELETE FROM dbo.Product_Eigenaren WHERE ProductId = '${config.productId}'\"
         """
      }   
   }
}  