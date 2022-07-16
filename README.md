# CrossDimCommands
[![Curseforge](http://cf.way2muchnoise.eu/full_640655_downloads.svg)](https://minecraft.curseforge.com/projects/cross-dim-commands) [![Curseforge](http://cf.way2muchnoise.eu/versions/For%20MC_640655_all.svg)](https://minecraft.curseforge.com/projects/cross-dim-commands)

Small mod that allows "weather" commands to be used from any dimension and properly translates coordinates inputted into the "worldborder center" command.

To add this to your project, add this to your build.gradle


```
repositories {
    maven {
        url = "https://maven.tamaized.com/releases"
    }
}

dependencies {
    runtimeOnly fg.deobf("team-twilight:crossdimcommands:{version}")
}
```
