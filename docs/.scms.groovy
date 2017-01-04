def baseExcludes = ['build.sh', 'build/**', 'Makefile', 'readme.md']

//find what version we're on:
def projectVersion = new XmlSlurper().parse(new File("../pom.xml").newReader()).version.text()

scms {
    excludes = baseExcludes

    model {
        apptype = 'application'
        port = 8080
        maven.project.version = projectVersion
    }

    model.apptype = 'application'

    patterns {
        '**/*.rst' {
            renderer = 'velocity' //ordinarily only looks for files with vtl extension
            outputFileExtension = 'rst' //ordinarily html
        }
    }
}

environments {

    def springExcludes = baseExcludes + ['source/access-control.rst', 'source/appendix/web-stormpath-properties.rst']

    servlet {
        scms {
            excludes = baseExcludes +
                        //the following files are not relevant for the servlet docs.  We exclude them here
                        //so the sphinx parser doesn't warn us during rendering, e.g.
                        //    WARNING: document isn't included in any toctree
                        ['source/forwarded-request.rst',
                        'source/about_sczuul.rst',
                        'source/tutorial.rst',
                        'source/appendix/default-stormpath-properties.rst',
                        'source/appendix/spring-boot-core-properties.rst',
                        'source/appendix/spring-boot-web-properties.rst',
                        'source/appendix/forgot-password.rst',
                        'source/appendix/change-password.rst',
                        'source/appendix/head.rst',
                        'source/appendix/login.rst',
                        'source/appendix/register.rst',
                        'source/appendix/verify.rst']
            model {
                servlet = true
                maven.project.groupId = 'com.stormpath.sdk'
                maven.project.artifactId = 'stormpath-servlet-plugin'
            }
        }
    }

    sczuul {
        scms {
            excludes = springExcludes + ['source/tutorial.rst']
            model {
                sczuul = true
                apptype = 'gateway'
                port = 8000
                maven.project.groupId = 'com.stormpath.spring'
                maven.project.artifactId = 'stormpath-zuul-spring-cloud-starter'
            }
        }
    }

    springboot {
        scms {
            excludes = springExcludes + ['source/about_sczuul.rst', 'source/forwarded-request.rst']
            model {
                springboot = true
                maven.project.groupId = 'com.stormpath.spring'
                maven.project.artifactId = 'stormpath-default-spring-boot-starter'
            }
        }
    }

    spring {
        scms {
            excludes = springExcludes + ['source/about_sczuul.rst', 'source/forwarded-request.rst']
            model {
                spring = true
                maven.project.groupId = 'com.stormpath.spring'
                maven.project.artifactId = 'stormpath-spring-security-webmvc'
            }
        }
    }
}