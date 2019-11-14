#!/usr/bin/groovy

def call (config = [:]){

   config = config as UpdateApiConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
        powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
        powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname ${config.apiManagementRG} -ServiceName ${config.apiManagementName};
            Set-AzApiManagementApi -Context \$ApiMgmtContext -apiid ${config.apiId} -Name "${config.apiName}" -Description "${config.apiDescription}" -Path ${url_Suffix} -protocols @('https')
        """
      }
   }
}

