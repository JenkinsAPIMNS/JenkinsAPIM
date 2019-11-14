def connectAzure(String servicePrincipal, String subscriptionId) {
    withCredentials([usernamePassword(credentialsId: servicePrincipal, usernameVariable: 'servicePrincipalId', passwordVariable: 'servicePrincipalPassword')]) {
        runPowershellCommand """\$Password = ConvertTo-SecureString "${servicePrincipalPassword}" -AsPlainText -Force;
               \$Credential = New-Object System.Management.Automation.PSCredential("${servicePrincipalId}", \$Password);
               Connect-AzAccount -Credential \$Credential -Tenant "64458159-0d9a-4d84-966f-1a13c0ac7a34" -ServicePrincipal;
               Set-AzContext -SubscriptionId "${subscriptionId}";
            """
    }
}

def runPowershellCommand(String command) {
    def randomFileName = "powershell-${UUID.randomUUID()}.ps1"
    writeFile file: randomFileName, text: command

    sh " cat $randomFileName"
    sh " pwsh -File $randomFileName"
}

def escapePowershellVariable(String variableString) {
    // Replace single \ with double \\ and prefix $ with backtick (`$)
    return variableString.replaceAll("\\\\", "\\\\\\\\").replace('$', '`$')
}

return this
