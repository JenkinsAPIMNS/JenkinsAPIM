$Password = ConvertTo-SecureString "lXJ5LFAlc5hpPS/ITUtfgKCE3byPT8PRRAZaIJ+YY8M=" -AsPlainText -Force
$Credential = New-Object System.Management.Automation.PSCredential("5c3301fc-7496-48b2-86bd-48223dcb7605", $Password)
Connect-AzAccount -Credential $Credential `
    -Tenant "64458159-0d9a-4d84-966f-1a13c0ac7a34" `
    -ServicePrincipal
