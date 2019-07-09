[![Build Status](https://travis-ci.org/ida-mediafoundry/jetpack-patch-system.svg?branch=master)](https://travis-ci.org/ida-mediafoundry/jetpack-patch-system)

# Jetpack - Patch System
(powered by iDA Mediafoundry)

This is a project that contains A Touch UI admin console to manage patches.
Navigate to http://localhost:4502/jetpack/patch-system.html to see this tool in action.

## Description

The JetPack Patch System allows you to check-in groovy scripts in the code base, which can be deployed to AEM.
AEM can run these patches using a system that will never run a patch that was executed before, unless modified.

A Touch UI overview screen provided insights in all patches and their execution state.

# Pre-requisites

Download and install ICF Olson Groovy Console: https://github.com/OlsonDigital/aem-groovy-console#installation
(We have a dependency to 13.0.0, other version might work as well, but is not tested)

# Patch Management

Upload all patches to /apps/patches/<project>/<patch>.groovy
* /apps/patches is a sling:Folder
* project is a nt:folder, sling:Folder or sling:OrderedFolder. A jcr:title can be added for a more readable name.
* <patch>.groovy is a valid groovy script

It's also possible to nest & group multiple project folders.

_Recommendation_: per installed zip, upload to 1 <project> folder.

# User configuration

The System user is configured automatically.

# Remote API

The remote API could be used for CI purposes:

* Execute a POST call to http://localhost:4502/services/patches/trigger to trigger the patch system.
* Execute a GET call to http://localhost:4502/services/patches/check to check if the patch system is still running.
* Execute a GET call to http://localhost:4502/services/patches/list to get a list of patches to execute.

*Don't forget to set the content-type on the request to 'application/json'.*

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, Sling Models and WCMCommand.
* ui.apps: contains the /apps part containing the html, js, css and .content.xml files.

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Testing

There are three levels of testing contained in the project:

unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test
