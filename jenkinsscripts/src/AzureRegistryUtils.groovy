// Please use the following credentials for your Azure-container-registry credentials
// Username: 63352880-5a9b-4827-bbf8-8cdc3887abff
// Password: om4x1t9zhyeHr7VFP[IOpU/VXUlyza/?
// The credentials can be a simple username/password

Closure withAzureRegistry(Closure closure) {
    closure.setDelegate(this)
    docker.withRegistry('https://topaasAPIM.azurecr.io', 'apim-azure-container-registry', closure)
}

return this
