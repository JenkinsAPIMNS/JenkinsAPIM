enum DeploymentAPIMEnvironment {
    TEST('apiportaltst', 'RG20APIM00-T', 'db20-product-eigenaren-t', 'ApiDBadmin', 'sw20apiman-t.database.windows.net', 'KV20APIM100-TA',  'sw20apiman-t-loginpassword'),
    ACCEPTANCE('apiportalacc', 'RG20APIM00-A', 'db20-product-eigenaren-a', 'ApiDBadmin', 'sw20apiman-a.database.windows.net', 'KV20APIM100-TA',  'sw20apiman-a-loginpassword'),
    PRODUCTION('apiportal', 'RG20APIM00', 'db20-product-eigenaren-p', 'ApiDBadminProd', 'sw20apiman-p.database.windows.net', 'KV20APIM100-P',  'sw20apiman-loginpassword')

    def apiManagementName
    def apiManagementRg
    def databaseName
    def databaseUsername
    def serverName
    def vaultName
    def vaultSecret

    DeploymentAPIMEnvironment(apiManagementName, apiManagementRg, databaseName, databaseUsername, serverName, vaultName, vaultSecret) {
        this.apiManagementName = apiManagementName
        this.apiManagementRg = apiManagementRg
        this.databaseName = databaseName
        this.databaseUsername = databaseUsername
        this.serverName = serverName
        this.vaultName = vaultName
        this.vaultSecret = vaultSecret
    }
}
