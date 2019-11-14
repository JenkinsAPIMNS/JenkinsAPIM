#!/usr/bin/groovy

def call (config = [:]){

   config = config as RemoveApiRevisionConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname ${config.apiManagementRG} -ServiceName ${config.apiManagementName}
            Remove-AzApiManagementApiRevision -Context \$ApiMgmtContext -ApiId "${config.apiId}" -ApiRevision ${config.apiRevision}
         """
      }
   }
} 