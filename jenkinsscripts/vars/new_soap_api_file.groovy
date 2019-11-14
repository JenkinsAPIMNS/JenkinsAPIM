def call (config = [:]){

   config = config as NewSoapApiConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
         powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
         powershellUtils.runPowershellCommand """
            \$ApiMgmtContext = New-AzApiManagementContext -Resourcegroupname ${config.apiManagementRG} -ServiceName ${config.apiManagementName}
            Import-AzApiManagementApi -Context \$ApiMgmtcontext -SpecificationFormat \"Wsdl\" -ApiType \"Soap\" -WsdlServiceName ${config.WsdlServiceName} -WsdlEndpointName ${config.WsdlEndPointName} -SpecificationPath "${config.WsdlFilepath}" -Path ${config.URL_Suffix}  -ApiId "${config.ApiID}"
         """
      }   
   }
}  