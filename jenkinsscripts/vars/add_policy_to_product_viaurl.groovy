#!/usr/bin/groovy

def call (Map config){
   config = config as AddPolicyToProductViaUrlConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry{
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${config.apiManagementRg} -ServiceName ${config.apiManagementName}
            Set-AzApiManagementPolicy -Context \$ApiMgmtContext -ProductId "${config.productid}" -PolicyUrl "${config.policyUrl}"
         """
      }
   }
}        
