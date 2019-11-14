#!/usr/bin/groovy

def call (config = [:]){

   config = config as NewApiRevisionConfig
   AzureRegistryUtils azureRegistryUtils = new AzureRegistryUtils()
   PowershellUtils powershellUtils = new PowershellUtils()

   azureRegistryUtils.withAzureRegistry {
      docker.image('topaasAPIM.azurecr.io/apimanagementimage').inside('--entrypoint ""') {

         withCredentials([usernamePassword(credentialsId: config.servicePrincipal, usernameVariable: 'servicePrincipalId', passwordVariable: 'servicePrincipalPassword'),
                          usernamePassword(credentialsId: config.azureDevOpsCredentialId, usernameVariable: 'azureDevOpsUser', passwordVariable: 'azureDevOpsToken')]) {

            apiDefinitionId = mapEnvironment(config.environment)
            escapedSwaggerText = powershellUtils.escapePowershellVariable(config.swaggerText)
            body = createBody(apiDefinitionId, config.apiId, escapedSwaggerText, config.urlSuffix, config.apiFormat, config.slackResponseUri)

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
   Map environmentMap = ['TEST':'45', 'ACCEPTANCE':'41', 'PRODUCTION':'42']

   return environmentMap[name.toUpperCase()]
}

private static def createBody(apiDefinitionId, apiId, swaggerText, urlSuffix, apiFormat, uri) {
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
