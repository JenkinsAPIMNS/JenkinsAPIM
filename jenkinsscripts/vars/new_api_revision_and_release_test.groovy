#!/usr/bin/groovy

def call (config = [:]){

   config = config as NewApiRevisionConfigTest
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname "${config.apiManagementRG}" -ServiceName "${config.apiManagementName}";
            \$item = Get-AzApiManagementApiRevision -context \$ApiMgmtContext -apiid "${config.apiId}";
            \$newvalue = \$item.apirevision[0] -as [int];
            \$newvalue = \$newvalue + 1;
            New-AzApiManagementApiRevision -Context \$ApiMgmtContext -ApiId "${config.apiId}" -ApiRevision \$newvalue;
            Import-AzApiManagementApi -Context \$ApiMgmtContext -SpecificationFormat "${config.apiFormat}" -SpecificationPath "${config.swaggerPath}" -Path "${config.urlSuffix}" -ApiId "${config.apiId}" -ApiRevision \$newvalue;
            New-AzApiManagementApiRelease -Context \$ApiMgmtContext -ApiId "${config.apiId}" -ApiRevision \$newvalue -Note "${config.releaseNote}";
         """
      }
   }
}
