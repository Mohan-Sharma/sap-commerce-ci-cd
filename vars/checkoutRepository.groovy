def call(commerceDir, branch, projectRepository) {
    urlPrefix = "https://"
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'a06c8283-d34d-483f-a9c3-6c82059a4277', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        //complicating to prevent string interpolation
        def repoDomainPart = projectRepository.substring(urlPrefix.size())
        def repository = "https://$USERNAME:$PASSWORD@" + repoDomainPart
        echo "##### Checkout repository #####"
        sh '''
            #!/bin/bash
            mkdir ''' + commerceDir + '''/concur-hybris
            chmod a+rwx ''' + commerceDir + '''/concur-hybris
            cd ''' + commerceDir + '''/concur-hybris && git clone ''' + repository + ''' . && git fetch --all && git checkout origin/''' + branch
    }
}