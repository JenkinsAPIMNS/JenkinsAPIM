#!/usr/bin/groovy

def call (config = [:]) {

    config = config as NewProductConfig
    AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
    PowershellUtils powershellUtils = new PowershellUtils()

    azureRegistryUtils.withAzureRegistry {
        docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

            withCredentials([usernamePassword(credentialsId: config.servicePrincipal, usernameVariable: 'servicePrincipalId', passwordVariable: 'servicePrincipalPassword'),
                             usernamePassword(credentialsId: config.azureDevOpsCredentialId, usernameVariable: 'azureDevOpsUser', passwordVariable: 'azureDevOpsToken')]) {

                productDefinitionId = mapEnvironment(config.environment)
                body = createBody(productDefinitionId, config.productId, config.productDescription, config.productOwner, config.productContact, servicePrincipalId, azureDevOpsUser, azureDevOpsToken, config.slackResponseUri)

                powershellUtils.connectAzure(config.servicePrincipal, config.subscriptionId)
                powershellUtils.runPowershellCommand """
                    \$EncodedCredentials = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("${azureDevOpsUser}:${azureDevOpsToken}"));
                    \$Headers = @{ Authorization = "Basic \$EncodedCredentials" };
                    Invoke-WebRequest -uri "https://vsrm.dev.azure.com/ns-topaas/APIM/_apis/release/releases?api-version=5.0" -Body ${body} -Method 'POST' -Headers \$Headers -ContentType "application/json";
               """
            }
        }
    }
}

private static def mapEnvironment(String name) {
    Map environmentMap = ['TEST':'22', 'ACC': '24', 'PROD':'25']

    return environmentMap[name.toUpperCase()]
}

private static def createBody(productDefinitionId, productID, productDescription, productOwner, productContact, servicePrincipalApplicationID, username, accessToken, uri) {
    return """@"
        {
            "definitionId": $productDefinitionId,
            "description": "Creating Sample release",
            "variables":{
            "ProductID": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$productID"
            },
            "ProductDescription": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$productDescription"
            },
            "ProductOwner": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$productOwner"
            },
            "ProductContact": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$productContact"
            },
            "ServicePrincipalApplicationID": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$servicePrincipalApplicationID"
            },
            "Username": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$username"
            },
            "AccessToken": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$accessToken"
            },
            "URI": {
                "allowOverride": true,
                "isSecret": false,
                "value": "$uri"
            }
        }
        }
"@"""
}
