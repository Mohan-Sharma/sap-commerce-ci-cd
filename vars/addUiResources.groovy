def call(commerceDir, branch, projectRepository, tagName) {
    urlPrefix = "https://"
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'a06c8283-d34d-483f-a9c3-6c82059a4277', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        //complicating to prevent string interpolation
        def repoDomainPart = projectRepository.substring(urlPrefix.size())
        def repository = "https://$USERNAME:\$PASSWORD@" + repoDomainPart
        sh '''
            #!/bin/bash
            echo "##### Whitelisting _ui resources to commit to ccv2 branch #####"
            cd ''' + commerceDir + '''/concur-hybris
            sed -i 's/**\\/_ui\\/addons//g' .gitignore
            sed -i 's/**\\/_ui-src\\/addons//g' .gitignore
            sed -i 's/**\\/_ui\\///g' .gitignore
            git commit .gitignore -m "versioning _ui resources"
            
            echo "##### Building _ui resources #####"
            export NODE_OPTIONS="--max_old_space_size=7168"
            cd ''' + commerceDir + '''/suite/hybris/bin/platform && . ./setantenv.sh && ant -f ''' + commerceDir + '''/suite/build.xml npminstall install prepare customize sassclean sasscompile clean all production -Denvironment=local -Dproduction.legacy.mode=true
            
            echo "##### Committing _ui resources #####"
            cd ''' + commerceDir + '''/suite/hybris/bin/custom/concur/concurstorefront/web/webroot/_ui/
            git add .
            git commit -m "adding all _ui resources"
            
            git tag ''' + tagName + ''' -a -m "ccv2 release"
            cd ''' + commerceDir + '''/concur-hybris && git push ''' + repository + ''' --tags
        '''
        return tagName;
    }
}