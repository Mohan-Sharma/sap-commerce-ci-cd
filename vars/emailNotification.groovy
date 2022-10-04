def call(jobName, buildNumber, buildStatus, buildURL) {
    color = buildStatus  == 'SUCCESS' ? 'green' : 'red'
    subject = """Jaas Job: ${jobName} [${buildNumber}]: ${buildStatus}"""
    body = """<p style='color: ${color}'><b>Job Name</b>: ${jobName} <br><b>Build Number</b>: [${buildNumber}] <br><b>Build Status</b>: ${buildStatus}</p>
    <p>Check console output at &QUOT;<a href='${buildURL}'>${jobName} [${buildNumber}]</a>&QUOT;</p>"""
    emailext (
            subject: subject,
            body: body,
            recipientProviders: [developers(), requestor(), contributor(), culprits(), upstreamDevelopers()],
            mimeType: 'text/html'
    )
}