def call(commerceDir, property){
    sh("echo '${property}' >> ${commerceDir}/suite/hybris/config/local.properties")
}
