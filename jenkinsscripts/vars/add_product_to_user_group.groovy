#!/usr/bin/groovy

def call (config = [:]){

   config = config as AddUserGroupConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()
   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
         
         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname "${config.apiManagementRG}" -ServiceName "${config.apiManagementName}";
            Add-AzApiManagementProductToGroup -Context \$ApiMgmtcontext -GroupId "${config.groupId}" -ProductId "${config.productId}";
         """
      }
   }
}        