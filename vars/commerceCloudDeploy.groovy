def call(buildName, dbUpdateMode, environmentId, strategy) {
    echo "##### Initiate Deployment to SAP Commerce Cloud Environment #####"
    script{
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'commerceCloudCredentials', usernameVariable: 'subscriptionId', passwordVariable: 'token']]) {
            //complicating to prevent string interpolation
            def subscriptionId = "$subscriptionId"
            def token = "$token"
            def script = '''curl --location --request POST 'https://portalrotapi.hana.ondemand.com/v2/subscriptions/''' + subscriptionId + '''/deployments' --header 'Content-Type: application/json' --header 'Authorization: Bearer ''' + token + '''' --header 'Content-Type: text/plain' --data-raw '{\"buildCode\": \"''' + buildName + '''\",\"databaseUpdateMode\": \"''' +  dbUpdateMode + '''\",\"environmentCode\": \"''' + environmentId + '''\",\"strategy\": \"''' + strategy+ '''\"}' '''
            def deploy = sh(script: script,returnStdout:true)
            deploy_result = readJSON text: deploy
            deploy_code = deploy_result["code"]
            return deploy_code
        }
    }
}  