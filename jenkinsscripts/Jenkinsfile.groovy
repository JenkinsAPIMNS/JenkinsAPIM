def azureSubscriptionId = '0746e4ac-1483-4bf9-a1da-1a27bf6a7525'
def resourceGroupName = 'RG20APIM100-T'
def serviceName = 'apiportaltst'

// NOTE: The possible values for 'action' should be CREATE_PRODUCT, CREATE_API_FROM_SWAGGER, ADD_API_TO_PRODUCT and RELEASE_API (for now). Maybe use Enum?
def action = 'RELEASE_API'

// Used for CREATE_PRODUCT
def newProductId = 'RIOJenkinsTest'
def title = 'RIOJenkinsTest'

// Used for CREATE_API_FROM_SWAGGER
def swaggerPath = 'rio-price-api-1.4.0-swagger.json'
def path = 'testingapi003' // Should be unique
def apiIdCreating = 'aUniqueNameForThisApi'

// Used for ADD_API_TO_PRODUCT
def productId = 'RIOJenkinsTest'
def apiIdAdding = 'aUniqueNameForThisApi'

// Used for RELEASE_API
def apiIdReleasing = 'aUniqueNameForThisApi'
def apiVersion = '1' // Should be integer value as String

node('docker') {
    docker.image('mcr.microsoft.com/powershell').inside('--entrypoint ""') {
            stage('Test') {
                checkout scm

                sh 'pwsh -Command Install-Module -Name Az.ApiManagement -Force'
                sh 'pwsh -File setUpAzureCredentials.ps1'

                script {
                    switch (action) {
                        case 'CREATE_PRODUCT':
                            sh "pwsh -File createProduct.ps1 -SubscriptionId ${azureSubscriptionId} -ProductId ${newProductId} -Title ${title} -State 'NotPublished' -ResourceGroupName ${resourceGroupName} -ServiceName ${serviceName}"
                            break
                        case 'CREATE_API_FROM_SWAGGER':
                            sh "pwsh -File createApiFromSwagger.ps1 -SubscriptionId ${azureSubscriptionId} -ResourceGroupName ${resourceGroupName} -ServiceName ${serviceName} -SwaggerPath ${swaggerPath} -Path ${path} -ApiId ${apiIdCreating}"
                            break
                        case 'ADD_API_TO_PRODUCT':
                            sh "pwsh -File addApiToProduct.ps1 -SubscriptionId ${azureSubscriptionId} -ResourceGroupName ${resourceGroupName} -ServiceName ${serviceName} -ProductId ${productId} -ApiId ${apiIdAdding}"
                            break
                        case 'RELEASE_API':
                            sh "pwsh -File releaseApi.ps1 -SubscriptionId ${azureSubscriptionId} -ResourceGroupName ${resourceGroupName} -ServiceName ${serviceName} -ApiId ${apiIdReleasing} -Version ${apiVersion}"
                            break
                        default:
                            echo "Nothing. Should throw error"
                            break
                    }
                }

                cleanWs()
            }
    }
}
