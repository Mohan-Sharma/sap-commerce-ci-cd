def call(branch, buildName) {
    def now = new Date().format("yyyyMMddHHmm", TimeZone.getTimeZone('UTC'))
    buildName += now
    echo "##### Initiating CCv2 Build: ${buildName} #####"
    script{
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'commerceCloudCredentials', usernameVariable: 'subscriptionId', passwordVariable: 'token']]) {
            //complicating to prevent string interpolation
            def subscriptionId = "$subscriptionId"
            def token = "$token"
            def script = '''curl --location --request POST 'https://portalrotapi.hana.ondemand.com/v2/subscriptions/''' + subscriptionId + '''/builds' --header 'Content-Type: application/json' --header 'Authorization: Bearer ''' + token + '''' --header 'Content-Type: text/plain' --data-raw '{\"branch\": \"''' + branch + '''",\"name\": \"''' + buildName +'''\"}' '''
            def build = sh(script: script, returnStdout:true)
            build_result = readJSON text: build
            code_number = build_result["code"]
            return code_number
        }
    }
}  