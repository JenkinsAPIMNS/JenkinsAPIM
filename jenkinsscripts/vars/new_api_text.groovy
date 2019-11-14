#!/usr/bin/groovy

def call (config = [:]) {

   config = config as NewApiFileConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

         withCredentials([usernamePassword(credentialsId: config.servicePrincipal, usernameVariable: 'servicePrincipalId', passwordVariable: 'servicePrincipalPassword'),
                          usernamePassword(credentialsId: config.azureDevOpsCredentialId, usernameVariable: 'azureDevOpsUser', passwordVariable: 'azureDevOpsToken')]) {

            def apiDefinitionId = mapEnvironment(config.environment)
            def escapedSwaggerText = powershellUtils.escapePowershellVariable(config.swaggerText)
             body = createBody(apiDefinitionId, config.apiId, config.productOwner, servicePrincipalId, azureDevOpsUser,
                     azureDevOpsToken, escapedSwaggerText, config.urlSuffix, config.apiFormat, config.slackResponseUri)

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
   Map environmentMap = ['TEST':'34', 'ACC':'35', 'PROD':'36']

   return environmentMap[name.toUpperCase()]
}

private static def createBody(apiDefinitionId, apiId, productOwner, servicePrincipalApplicationID, username, accessToken,
                              swaggerText, urlSuffix, apiFormat, uri) {
    return """@"
        {
          "definitionId": $apiDefinitionId,
          "description": "Creating Sample release",
          "variables":{
              "APIID": {
              "allowOverride": true,
              "isSecret": false,
              "value": "$apiId"
              },
              "ProductOwner": {
              "allowOverride": true,
              "isSecret": false,
              "value": "$productOwner"	
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
              "SwaggerText": {
              "allowOverride": true,
              "isSecret": false,
              "value": '$swaggerText'
              },
              "URL_Suffix": {
              "allowOverride": true,
              "isSecret": false,
              "value": "$urlSuffix"	
              },
              "APIFormat": {
              "allowOverride": true,
              "isSecret": false,
              "value": "$apiFormat"	
              },
              "URI": {
              "allowOverride": true,
              "isSecret": false,
              "value": "$uri"	
              },
              "FileorURl": {
              "allowOverride": true,
              "isSecret": false,
              "value": "FILE"
              }
          }
      }
"@"""
}
