#!/usr/bin/groovy

def call(Map config) {
    config = config as AddPolicyToApiConfig
    AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
    PowershellUtils powershellUtils = new PowershellUtils()

    azureRegistryUtils.withAzureRegistry {
        docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {
            powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
            powershellUtils.runPowershellCommand """
                \$ApiMgmtContext = New-AzApiManagementContext -ResourceGroupName ${config.apiManagementRG} -ServiceName ${config.apiManagementName}
                Set-AzApiManagementPolicy -Context \$ApiMgmtContext -ApiId "${config.apiId}" -Policy "${config.policy}"
            """
        }
    }
}
