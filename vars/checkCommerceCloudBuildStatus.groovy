def call(codeNumber) {
    script {
        def statusResult
        while (true) {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'commerceCloudCredentials', usernameVariable: 'subscriptionId', passwordVariable: 'token']]) {
                //complicating to prevent string interpolation
                def subscriptionId = "$subscriptionId"
                def token = "$token"
                def script = '''curl --location --request GET 'https://portalrotapi.hana.ondemand.com/v2/subscriptions/''' + subscriptionId + '''/builds/''' + codeNumber + '''' --header 'Authorization: Bearer ''' + token + '''' '''
                def result = sh(script: script, returnStdout:true)
                statusResult = readJSON text: result
            }

            if("SUCCESS".equals(statusResult["status"])) {
                break;
            }
            else if("FAIL".equals(statusResult["status"])) {
                error("Build was not completed successfully on SAP Commerce Cloud")
            }
            sh('sleep 120s')
        }
        echo "Commerce Cloud Build Complete"
    }
}  