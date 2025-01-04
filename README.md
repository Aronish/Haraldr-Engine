# Haraldr Engine
A simple engine made in OpenGL with the sole purpose of teaching me computer graphics, game engine programming and large-scale application design.

## Structure

The repository consists of a core library called ```haraldr-engine```. This contains all base functionality for creating an application using the engine. Two client applications have been provided: the editor and the offline renderer. The editor is the user interface used for editing scenes and assets. The offline renderer can be used to generate proprietary file formats for the cubemaps used in the PBR renderer.

A kind of special procedure is required to create an application, since even the ```main``` method is taken care of by the engine.

The ```main``` method is located in ```haraldr.main.EntryPoint```. It also has a member of type ````haraldr.main.Application```, which represents the client application.
```java
// haraldr.main.EntryPoint

public abstract class EntryPoint
{
    public static Application application;
    ...
    public static void main(String[] args) {...}
}
```
A client has to create a subclass of ```EntryPoint``` and supply a class of supertype ```haraldr.main.Application``` through a static initializer, e.g:

```java
import haraldr.main.EntryPoint;

public class EditorEntryPoint extends EntryPoint
{
    static
    {
        application = new EditorApplication(...);
    }
}
```
This subclass has to be set as the main class to run the application.

## Building
The project ships with Gradle, meaning you only have to clone it and run a single command to start an application.

```bash
git clone https://github.com/Aronish/Haraldr-Engine.git
```
In the root directory of the repository, either of these commands may be run directly:
```bash
./gradlew :haraldr-editor:run
./gradlew :haraldr-offline-renderer:run
```
It is also possible to generate jar files using
```bash
./gradlew :<module-name>:jar
```
The artifact will appear in ```./<module-name>/build/libs/<module-name>.jar```.

## In-Engine Imagery
Preview of the 3D renderer using <a href="http://artisaverb.info/PBT.html">Andrew Maximov's gun model</a> with a PBR material:

![Preview](preview.png?raw=true)

Editor application client built with Haraldr-Engine as a base library. Very unfinished.

![Editor](haraldr-editor.png?raw=true)

## Known Bugs:
<ul>
    <li>NVIDIA Game Ready Driver version 452.06 running on a Gigabyte 750 Ti
    is confirmed to freeze the computer indefinitely when generating high resolution cubemaps.
    This might be true for more graphics cards. In case problems occur, use version 451.57.
    </li>
    <li>
    Regarding these high resolution cubemaps, depending on the computer a TDR error might be raised
    if the GPU operations take too long.
    <br/>
    (The operating system usually assumes something went wrong if nothing gets rendered for about 2 seconds.
    This has to be mitigated to use the GPU for raw computation)
    <br/>
    To increase the TDR delay, edit these registry keys or add them if they don't exist.
    <br/>
    <b>Location: Computer\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\GraphicsDrivers</b>
    <table>
        <tr>
            <td>Key Name:</td>
            <td>Key Value:</td>
        </tr>
        <tr>
            <td>TdrLevel</td>
            <td>3</td>
        </tr>
        <tr>
            <td>TdrDelay</td>
            <td>10 - 20 or more*</td>
        </tr>
        <tr>
            <td>TdrDdiDelay</td>
            <td>10 - 20 or more*</td>
        </tr>
    </table>
    <small><i>*20 seconds should be enough for really high resolution cubemaps, but you can optionally increase it even more</i></small>
    <br/>
    Restart your computer after editing the registry.
    </li>
</ul>

## Libraries
<b>Java:</b> At least JDK 14.0.1
<br/>
<b><a href="https://www.lwjgl.org/">LWJGL:</a></b> 3.2.3_12
<br/>
<b><a href="https://github.com/stleary/JSON-java">JSON-java by Sean Leary: </a></b><br/>
<b><a href="https://github.com/decorators-squad/eo-yaml">eo-yaml</a></b>
<br/>
<b>(Jetbrains Annotations)</b>

<p>Special thanks to Joey de Vries for creating <a href="http://www.learnopengl.com">LearnOpenGL.com</a>!</p>