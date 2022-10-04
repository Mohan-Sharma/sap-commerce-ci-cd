def call(commerceDir) {
	echo "##### Execute install script ##### -- ${commerceDir}"
	sh """
		#!/bin/bash
		cd ${commerceDir}/suite/hybris/bin/platform && . ./setantenv.sh
		cd ${commerceDir}/concur-hybris/core-customize/devops && ant develop && ant createdevdirs && rm -rf ${commerceDir}/suite/hybris/bin/modules/npm-ancillary/npmancillary/resources/npm/node/node-v10.22.0-linux-x64/bin/npx
	"""
	addProperty(commerceDir, "solrserver.instances.default.autostart=false")
	sh """cd ${commerceDir}/suite/hybris/bin/platform && . ./setantenv.sh && ant clean all"""
}
   