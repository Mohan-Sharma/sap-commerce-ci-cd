def call(commerceDir) {
    echo "##### Extract commerce platform ##### -- ${commerceDir}"
    sh """
        #!/bin/bash
        mkdir ${commerceDir}/suite 
        chmod a+rwx ${commerceDir}/suite
        unzip -o $JENKINS_HOME/userContent/HYBRIS_PLATFORM_2005.ZIP -d ${commerceDir}/suite
       """
    /** Uncomment if you will be using the Integration Extension Pack
    echo "##### Extract commerce integration pack #####"
    sh "unzip -o ../CXCOMINTPK2005*.ZIP -d ${commerceDir}/core-customize"
    **/
}  